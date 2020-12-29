package pers.lxf.wdk.database;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ResourceUtils;
import pers.lxf.wdk.beans.DBTable;
import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.mvc.ControllerGenerator;
import pers.lxf.wdk.mvc.HtmlGenerator;
import pers.lxf.wdk.mvc.MybatisGenerator;
import pers.lxf.wdk.mvc.ServiceGenerator;
import pers.lxf.wdk.util.ExcelUtil;
import pers.lxf.wdk.util.SQLKit;
import pers.lxf.wdk.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static pers.lxf.wdk.beans.DatabaseCreator.readExcel;

/**
 * 数据库结构读取器
 */
public class DBSchemaLoader {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MybatisGenerator mybatisGenerator;
    @Autowired
    private ServiceGenerator serviceGenerator;
    @Autowired
    private ControllerGenerator controllerGenerator;
    @Autowired
    private HtmlGenerator htmlGenerator;



    private boolean transformString2Bool(String str,boolean defaultValue){
        boolean ret = defaultValue;
        if(str!=null){
            if("是".equals(str)||"true".equalsIgnoreCase(str)){
                ret = true;
            }else if("否".equals(str)||"false".equalsIgnoreCase(str)){
                ret = false;
            }
        }
        return ret;
    }

    private boolean transformString2Bool(String str){
        if(str!=null&&"是".equals(str)){
            return true;
        }else {
            return false;
        }
    }
    private static final class TableRowMapper implements RowMapper<DBTable> {

        public DBTable mapRow(ResultSet resultSet, int i) throws SQLException {
            return new DBTable(resultSet.getString("table_name"));
        }
    }

    private static final class TableDefinitionRowMapper implements RowMapper<DBTableColumnDefinition> {

        public DBTableColumnDefinition mapRow(ResultSet resultSet, int i) throws SQLException {
            DBTableColumnDefinition dbTableColumnDefinition =  new DBTableColumnDefinition(resultSet.getString("column_name"),
                    resultSet.getString("data_type"),
                    resultSet.getInt("character_maximum_length"),
                    SQLKit.isPrimeryKey(resultSet.getString("column_key")),
                    !SQLKit.transformString2Boolean(resultSet.getString("is_nullable")),// 数据库中获得的表结构中是可以为空，与定义相反，所以这里取反
                    SQLKit.isIncrement(resultSet.getString("extra")),
                    resultSet.getString("column_comment"));

            return dbTableColumnDefinition;
        }
    }

    /**
     * 生成修改表的sql语句
     * @param dbTableDefinition
     * @return 修改表的sql
     */
    private String[] generateModifyTableSql(DBTableDefinition dbTableDefinition){
        String[] sql=new String[2];
        // 查询数据库中表结构
        String queryTableDefinitionSql = "SELECT " +
                "column_name,is_nullable,data_type,character_maximum_length,column_key,extra,column_comment " +
                "FROM information_schema.columns " +
                "WHERE table_schema = 'myweb' AND table_name = '"+dbTableDefinition.getTableName()+"'";
        List<DBTableColumnDefinition> columns = jdbcTemplate.query(queryTableDefinitionSql,new TableDefinitionRowMapper());
        // 比较每一列的名称，类型及长度等信息
        List<DBTableColumnDefinition> defColumns = dbTableDefinition.getColumns();
        String modifySql = "",addSql = "";
        String addPrimeryKey = "";
        String delPrimeryKey = "";
        for (DBTableColumnDefinition columnDefinition :
                defColumns) {
            boolean columnExist = false;
            for (DBTableColumnDefinition columnReal :
                    columns) {
                if (columnDefinition.getColumnName().equals(columnReal.getColumnName())) {
                    String modifyColumnSql = ""; // 该字段的sql表达式
                    if(!columnDefinition.getDataType().equals(columnReal.getDataType())){
                        // 数据类型变化
                        modifyColumnSql += columnDefinition.getDataType();
                        // 如果数据类型需要指定长度
                        if(SQLKit.isDataTypeNeedLength(columnDefinition.getDataType())){
                            modifyColumnSql += "("+columnDefinition.getDataLength()+")";
                        }
                    }else{
                        // 数据类型没变，检查长度是否变化
                        if(columnDefinition.getDataLength()>0) {
                            if (SQLKit.isDataTypeNeedLength(columnDefinition.getDataType())&&columnDefinition.getDataLength() != columnReal.getDataLength()) {
                                modifyColumnSql += columnDefinition.getDataType() + "(" + columnDefinition.getDataLength() + ")";
                            }
                        }
                    }
                    // 非空
                    if(columnDefinition.isNotNULL() != columnReal.isNotNULL()){
                        if(columnDefinition.isNotNULL()){
                            modifyColumnSql += " NOT NULL ";
                        }
                    }
                    // 主键
                    if(columnDefinition.isPrimaryKey() != columnReal.isPrimaryKey()){
                        if(columnDefinition.isPrimaryKey())
                            addPrimeryKey += "'"+columnDefinition.getColumnName()+"',";
                        else
                            delPrimeryKey += "'"+columnDefinition.getColumnName()+"',";
                    }
                    if(columnDefinition.isIncrement()!=columnReal.isIncrement()){
                        if(columnDefinition.isIncrement()){
                            modifyColumnSql += " AUTO_INCREMENT";
                        }
                    }
                    if(modifyColumnSql.length()>0){
                        modifySql += columnDefinition.getColumnName() + " " + modifyColumnSql+",";
                    }
                    columnExist = true;
                    break;
                }

            }
            if(!columnExist){
                // 新增列
                addSql += columnDefinition.getColumnName()+ " "+
                        columnDefinition.getDataType()+(SQLKit.isDataTypeNeedLength(columnDefinition.getDataType())?"("+columnDefinition.getDataLength()+")":"")+
                        (columnDefinition.isNotNULL()?" NOT NULL":"")+
                        (columnDefinition.isIncrement()?" AUTO_INCREMENT":"");
                if(columnDefinition.isPrimaryKey()){
                    addPrimeryKey += "'"+columnDefinition.getColumnName()+"',";
                }
            }
            //String addPrimaryKeySql = ""

        }
        if (modifySql.length()>0) {
            modifySql = "ALTER TABLE " + dbTableDefinition.getTableName() + " MODIFY " + modifySql;
            sql[0] = StringUtil.dropLastComma(modifySql);
        }
        if(addSql.length()>0) {
            addSql = "ALTER TABLE " + dbTableDefinition.getTableName() + " ADD " + addSql;
            sql[1] = addSql;
        }
        return sql;
    }
    private String generateCtreateTableSql(DBTableDefinition dbTableDefinition){
        String sql = "";
        List<DBTableColumnDefinition> columnList = dbTableDefinition.getColumns();
        String primaryKey = "";
        for(int i=0;i<columnList.size();i++){
            DBTableColumnDefinition columnDefinition = columnList.get(i);
            sql += columnDefinition.getColumnName();
            sql += " "+columnDefinition.getDataType() + (SQLKit.isDataTypeNeedLength(columnDefinition.getDataType())?"("+columnDefinition.getDataLength()+")":"");
            sql += columnDefinition.isNotNULL()?" NOT NULL":"";
            sql += columnDefinition.isIncrement()? " AUTO_INCREMENT":"";
            if(columnDefinition.isPrimaryKey()){
                primaryKey += "`"+columnDefinition.getColumnName()+"`,";
            }
            sql += ",";
        }
        sql = "CREATE TABLE "+dbTableDefinition.getTableName()+" ("+sql;
        sql += " PRIMARY KEY ("+ StringUtil.dropLastComma(primaryKey)+"))";
        return sql;
    }
    /**
     * 检查数据库中表与定义的表的区别
     * 返回列表，包括需要操作的表，是新增还是更新
     */
    private void checkDBAndDefinition(List<DBTableDefinition> tableDefinitionList){
        // 连接数据库得到所有表
        List<DBTable> listTable = jdbcTemplate.query("SELECT table_name FROM information_schema.tables WHERE table_schema='myweb'",new TableRowMapper());
        //System.out.println(tableDefinitionList.size());
        for (DBTableDefinition dbTableDefinition :
                tableDefinitionList) {
            String[] modifySqls = null;
            String addTableSql = null;
            if (isTableNameExist(dbTableDefinition.getTableName(),listTable)) {
                // 修改表结构
                modifySqls = generateModifyTableSql(dbTableDefinition);
            }else{
                System.out.println("1111111111");
                // 新增表
                addTableSql = generateCtreateTableSql(dbTableDefinition);
            }
            System.out.println("-----------modify_sql---------------");
            if(modifySqls!=null)
            for (String sql :
                    modifySqls) {
                if(!StringUtil.isEmpty(sql)) {
                    jdbcTemplate.execute(sql);
                }
            }
            System.out.println("------------------------------------");
            System.out.println("-----------create_sql---------------");
            System.out.println(addTableSql);
            if(!StringUtil.isEmpty(addTableSql)) {
                jdbcTemplate.execute(addTableSql);
            }
            System.out.println("------------------------------------");
        }
        // 查找不存在的，需要删除的表
    }

    private boolean isTableNameExist(String tableName,List<DBTable> listTable) {
        for (DBTable dbTable :
                listTable) {
            if (dbTable.getTableName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据Row生成列定义
     * @param row excel一行
     * @return 列定义
     */
    private DBTableColumnDefinition getCoumnDefinitionFromRow(Row row){
        // 处理列定义
        String colName = ExcelUtil.getCellString(row.getCell(0));
        if(colName==null){
            return null;
        }
        String dataType = ExcelUtil.getCellString(row.getCell(1));
        //System.out.println(dataType);
        int colLength = ExcelUtil.getCellInt(row.getCell(2));
        boolean isPremeryKey = transformString2Bool(ExcelUtil.getCellString(row.getCell(3)));
        boolean isNull = transformString2Bool(ExcelUtil.getCellString(row.getCell(4)));
        boolean isIncrement = transformString2Bool(ExcelUtil.getCellString(row.getCell(5)));
        String comment = ExcelUtil.getCellString(row.getCell(6));
        boolean isBeMaintain = transformString2Bool(ExcelUtil.getCellString(row.getCell(7)));
        String displayTxt = ExcelUtil.getCellString(row.getCell(8));
        String linkOtherTable = ExcelUtil.getCellString(row.getCell(9));
        String pageControl = ExcelUtil.getCellString(row.getCell(10));
        DBTableColumnDefinition dbTableColumnDefinition = new DBTableColumnDefinition(colName,dataType,colLength,isPremeryKey,isNull,isIncrement,comment);
        dbTableColumnDefinition.setBeMaintain(isBeMaintain);
        dbTableColumnDefinition.setDisplayTxt(displayTxt);
        dbTableColumnDefinition.setLinkOtherTable(linkOtherTable);
        dbTableColumnDefinition.setPageControl(pageControl);
        return dbTableColumnDefinition;
    }

    public List<DBTableDefinition> loadSchema(){
        List<DBTableDefinition> tableDefinitionList = null;
        // 获取excel
        try {
            File cfgFile = ResourceUtils.getFile("classpath:schema.xlsx");
            Workbook workbook = readExcel(cfgFile);
            if(workbook!=null){
                Sheet sheet0 = workbook.getSheetAt(0);
                int startRowNum = sheet0.getFirstRowNum();
                int endRowNum = sheet0.getLastRowNum();
                if(startRowNum>0&&endRowNum>0&&endRowNum>startRowNum) {
                    tableDefinitionList = new ArrayList<DBTableDefinition>();
                    DBTableDefinition dbTableDefinition = null;
                    boolean lastRowIsNewTable = false;
                    for (int i = startRowNum; i <= endRowNum; i++) {
                        Row row = sheet0.getRow(i);
                        if(row==null){
                            continue;
                        }
                        String cellContent = ExcelUtil.getCellString(row.getCell(0));
                        if (cellContent != null && "tablename".equals(cellContent.trim().toLowerCase())) {
                            // 新表
                            // 得到表名
                            String tableName = ExcelUtil.getCellString(row.getCell(1));
                            // 得到是否增加维护页面
                            String maintainPage = ExcelUtil.getCellString(row.getCell(2));
                            String dictionary = ExcelUtil.getCellString(row.getCell(3));
                            // 将之前的表定义写入list
                            if(dbTableDefinition!=null) {
                                tableDefinitionList.add(dbTableDefinition);
                            }
                            dbTableDefinition = new DBTableDefinition();
                            dbTableDefinition.setTableName(tableName);
                            dbTableDefinition.setMaintainPage(StringUtil.getKeyValueBoolean(maintainPage,"MaintainPage",true));
                            dbTableDefinition.setDictionary(StringUtil.getKeyValueBoolean(dictionary,"Dictionary",false));
                            lastRowIsNewTable = true;
                        }else{
                            if(lastRowIsNewTable){
                                lastRowIsNewTable = false;
                                continue;
                            }
                            DBTableColumnDefinition columnDefinition = getCoumnDefinitionFromRow(row);
                            if(columnDefinition==null){
                                continue;
                            }
                            if(dbTableDefinition.getColumns()==null){
                                dbTableDefinition.setColumns(new ArrayList());
                            }
                            dbTableDefinition.getColumns().add(columnDefinition);
                        }
                    }
                    tableDefinitionList.add(dbTableDefinition);
                    // 建表
                    // 比对数据库中表结构与表结构定义
                    checkDBAndDefinition(tableDefinitionList);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tableDefinitionList;
    }
}


