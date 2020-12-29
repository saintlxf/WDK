package pers.lxf.wdk.mvc;

import org.springframework.util.ResourceUtils;
import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.database.DBSchemaLoader;
import pers.lxf.wdk.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 根据配置生成mybatis的mapper和interface
 */
public class MybatisGenerator {


    private List<DBTableDefinition> tableDefinitionList;

    public List<DBTableDefinition> getTableDefinitionList() {
        return tableDefinitionList;
    }

    public void setTableDefinitionList(List<DBTableDefinition> tableDefinitionList) {
        this.tableDefinitionList = tableDefinitionList;
    }

    private List<String> generateMapper(DBTableDefinition tableDefinition){
        List<String> result = null;
        // 得到工程目录

        String mapperDir = WDKConfig.projectDir+WDKConfig.resourceDir+"/mappers/";
        // 建立目录
        FileUtil.createDirWithCheck(mapperDir);
        // 得到表名作为文件名
        String mapperFileName = tableDefinition.getTableName()+".xml";
        File mapperFile = new File(mapperDir+mapperFileName);
        // 生成文件（文件存在时，删除原文件，重新生成）
        FileUtil.createFileWithDeleteFile(mapperFile);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(mapperFile));
            // 写入xml头
            writeXmlHeader(bw,tableDefinition);
            // 写入xml内容
            result = writeXmlContent(bw,tableDefinition);
            bw.write("</mapper>");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 写入ResultMap
     * @param bw 写入器
     * @param tableDefinition 表定义
     */
    private void writeResultMap(BufferedWriter bw,DBTableDefinition tableDefinition){
        // 生成ResultMap
        String resultMap = "\t<resultMap id=\"BaseResultMap\" type=\""+MybatisUtil.generateEntityBeanName(tableDefinition)+"\">\r\n";
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        for (DBTableColumnDefinition columnDefinition :
                columnList) {
            resultMap += "\t\t<result column=\""+columnDefinition.getColumnName()+"\" property=\""+MybatisUtil.generateEntityAttributeName(columnDefinition.getColumnName())+"\" jdbcType=\""+SQLKit.tableDefinitionTypeMapJdbcType.get(columnDefinition.getDataType())+"\"/>\r\n";
        }
        resultMap += "\t</resultMap>\r\n";
        try {
            bw.write(resultMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void writeAllColumn(BufferedWriter bw,DBTableDefinition tableDefinition){
        String sql = "\t<sql id=\"All_Column\">\r\n";
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        if(columnList.size()>0) {
            sql += "\t\t";
            for (DBTableColumnDefinition column :
                    columnList) {
                sql += column.getColumnName()+",";
            }
            //System.out.println(sql);
            sql = StringUtil.dropLastComma(sql);
            sql += "\r\n\t</sql>\r\n";
        }
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeInsertColumn(BufferedWriter bw,DBTableDefinition tableDefinition){

        String sql = "\t<sql id=\"Insert_Column\">\r\n";
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        if(columnList.size()>0) {
            sql += "\t\t";
            for(int i=0;i<columnList.size();i++){
                DBTableColumnDefinition column = columnList.get(i);
                if(column.isIncrement()){
                    continue;
                }
                sql += column.getColumnName()+",";
            }
            sql = StringUtil.dropLastComma(sql);
            sql += "\r\n\t</sql>\r\n";
        }
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String writeQueryAllSql(BufferedWriter bw, DBTableDefinition tableDefinition) {
        String ret = MybatisUtil.generateQueryAllMethodName(tableDefinition);
        String sql ="\t<select id=\""+ ret +"\" resultMap=\"BaseResultMap\">\r\n";
        sql += "\t\tSELECT\r\n";
        sql += "\t\t<include refid=\"All_Column\"/>\r\n";
        sql += "\t\tFROM "+tableDefinition.getTableName()+"\r\n";
        sql += "\t</select>\r\n";
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ret.length()>0){
            ret = "List<"+MybatisUtil.generateEntityBeanName(tableDefinition)+"> "+ret+"();";
        }
        return ret;
    }

    private String writeQuerySqlByPrimaryKey(BufferedWriter bw,List<DBTableColumnDefinition> primaryColumnList,DBTableDefinition tableDefinition){
        String ret = MybatisUtil.generateMethodNameByPrimaryKey(primaryColumnList);
        // 获得方法名称
        String sql ="\t<select id=\""+ ret +"\" resultMap=\"BaseResultMap\">\r\n";
        sql += "\t\tSELECT\r\n";
        sql += "\t\t<include refid=\"All_Column\"/>\r\n";
        sql += "\t\tFROM "+tableDefinition.getTableName()+"\r\n";
        sql += "\t\tWHERE " + MybatisUtil.generateQueryWhereStatement(primaryColumnList)+"\r\n";
        sql += "\t</select>\r\n";
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 写参数表
        if(ret.length()>0){
            ret = MybatisUtil.generateEntityBeanName(tableDefinition)+ " "+ret;
            ret += "("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumnList)+");";
        }
        return ret;
    }



    private String writeInsertSql(BufferedWriter bw,DBTableDefinition tableDefinition){
        String ret = MybatisUtil.generateInsertMethodName(tableDefinition);
        String sql = "\t<insert id=\""+ret+"\" parameterType=\""+MybatisUtil.generateEntityBeanName(tableDefinition)+"\">\r\n";
        sql += "\t\tINSERT INTO "+tableDefinition.getTableName()+"(\r\n";
        sql += "\t\t<include refid=\"Insert_Column\"/>\r\n";
        sql += "\t\t)values(\r\n";
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        for (int i = 0; i < columnList.size(); i++) {
            DBTableColumnDefinition column = columnList.get(i);
            if(column.isIncrement()){
                continue;
            }
            sql += "\t\t#{"+MybatisUtil.generateEntityAttributeName(column.getColumnName())+",jdbcType="+ SQLKit.tableDefinitionTypeMapJdbcType.get(column.getDataType()) +"},\r\n";
        }
        sql = StringUtil.dropLastComma(sql);
        sql = "\t"+sql;
        sql += ")\r\n";
        sql += "\t</insert>\r\n";
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 写参数
        if(ret.length()>0){
            ret = "void "+ret;
            ret += "("+MybatisUtil.generateEntityBeanName(tableDefinition)+" "+StringUtil.firstLittle2LowCase(tableDefinition.getTableName())+");";
        }
        return ret;
    }

    private String writeUpdateSql(BufferedWriter bw,List<DBTableColumnDefinition> primaryColumnList,DBTableDefinition tableDefinition){
        String ret = MybatisUtil.generateUpdateMethodName(tableDefinition);
        String sql = "\t<update id=\""+ret+"\"  parameterType=\""+MybatisUtil.generateEntityBeanName(tableDefinition)+"\">\r\n";
        sql += "\t\tUPDATE " + tableDefinition.getTableName()+"\r\n";
        sql += "\t\t<trim prefix=\"set\" suffixOverrides=\",\">\r\n";
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        for (DBTableColumnDefinition column :
                columnList) {
            if(column.isPrimaryKey()){
                continue;
            }
            String entityBeanAttributeName = MybatisUtil.generateEntityAttributeName(column.getColumnName());
            sql += "\t\t<if test=\""+entityBeanAttributeName+"!=null\">"+column.getColumnName()+"=#{"+entityBeanAttributeName+"},</if>\r\n";
        }
        sql += "\t\t</trim>\r\n";
        sql += "\t\tWHERE " + MybatisUtil.generateQueryWhereStatement(primaryColumnList)+"\r\n";
        sql += "\t</update>\r\n";
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ret.length()>0){
            ret = "void "+ret;
            ret += "("+MybatisUtil.generateEntityBeanName(tableDefinition)+" "+StringUtil.firstLittle2LowCase(tableDefinition.getTableName())+");";
        }
        return ret;
    }

    private String writeDeleteSql(BufferedWriter bw,List<DBTableColumnDefinition> primaryColumnList,DBTableDefinition tableDefinition){
        String ret = "deleteBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumnList);
        String sql = "\t<delete id=\""+ret+"\">\r\n";
        sql+= "\t\tDELETE FROM "+tableDefinition.getTableName()+" \r\n";
        sql += "\t\tWHERE "+MybatisUtil.generateQueryWhereStatement(primaryColumnList)+"\r\n";
        sql += "\t</delete>\r\n";
        try {
            bw.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ret.length()>0){
            ret = "void "+ret;
            ret += "("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumnList)+");";
        }
        return ret;
    }



    /**
     * 写mapper内容
     * @param bw xml文件写入器
     * @param tableDefinition 表结构定义
     */
    private List<String> writeXmlContent(BufferedWriter bw, DBTableDefinition tableDefinition) {
        List<String> result = new ArrayList<String>();
        // 得到主键列表
        List<DBTableColumnDefinition> primaryColumnList = MybatisUtil.getPrimaryColumnList(tableDefinition);
        // 写resultmap
        writeResultMap(bw,tableDefinition);
        // 写列定义
        writeAllColumn(bw,tableDefinition);
        // 写Insert列定义
        writeInsertColumn(bw,tableDefinition);
        // 写查询语句
        result.add(writeQuerySqlByPrimaryKey(bw,primaryColumnList,tableDefinition));
        result.add(writeQueryAllSql(bw,tableDefinition));
        //result.add(writeQueryAllWithDictionarySql(bw,tableDefinition));
        // 写insert语句
        result.add(writeInsertSql(bw,tableDefinition));
        // 写update语句
        result.add(writeUpdateSql(bw,primaryColumnList,tableDefinition));
        // 写delete语句
        result.add(writeDeleteSql(bw,primaryColumnList,tableDefinition));
        return result;
    }

//    private String writeQueryAllWithDictionarySql(BufferedWriter bw, DBTableDefinition tableDefinition) {
//        String ret = "";
//        List<DBTableColumnDefinition> columns = tableDefinition.getColumns();
//        List<DBTableColumnDefinition> columnLinkOtherTable = columns.stream().filter(column->StringUtil.notEmpty(column.getLinkOtherTable())).collect(Collectors.toList());
//        String sql ="\t<select id=\""+ ret +"\" resultMap=\"BaseResultMap\">\r\n";
//        sql += "\t\tSELECT\r\n";
//        sql += "\t\t<include refid=\"All_Column\"/>\r\n";
//        sql += "\t\tFROM "+tableDefinition.getTableName()+"\r\n";
//        sql += "\t\tWHERE " + MybatisUtil.generateQueryWhereStatement(primaryColumnList)+"\r\n";
//        sql += "\t</select>\r\n";
//        return null;
//    }


    /**
     * 根据节点名返回填充的字符串
     * @param elementName 节点名称
     * @return 要填充的字符串
     */
    private String getFillStrByElementName(String elementName,DBTableDefinition tableDefinition){
        String ret = "";
        if(elementName.equals("mapper")){
            //得到dao类全路径名
            ret = WDKConfig.basePackageName+WDKConfig.daoPackageName+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+WDKConfig.daoStringName;
        }
        return ret;
    }

    /**
     * 填充占位符
     */
    private String fillPlaceHolder(String str, DBTableDefinition tableDefinition){
        // 判断占位符的节点名
        String elementName = XmlUtil.getXmlElementName(str);
        // 根据节点名，得到要填充的内容,将内容拼接到字符串中
        return XmlUtil.fillPlaceHolder(str,getFillStrByElementName(elementName,tableDefinition));
    }
    /**
     * 写xml头
     * @param bw 写入文件的写入器
     */
    private void writeXmlHeader(BufferedWriter bw,DBTableDefinition tableDefinition) {
        File testFile = null;
        try {
            // 读取模板文件，写入mapper
            testFile = ResourceUtils.getFile("classpath:template/mapper.template");
            BufferedReader br = new BufferedReader(new FileReader(testFile));
            String line = null;
            while((line = br.readLine())!=null){
                String newLine = null;
                // 判断是否需要填充占位符
                if(line.indexOf("?")>0&&line.indexOf("?xml")<0){
                    // 填充占位符
                    newLine = fillPlaceHolder(line,tableDefinition);
                }else{
                    newLine = line;
                }
                bw.write(newLine+"\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private void writeDaoFile(BufferedWriter bw, List<String> ifFunctionList,DBTableDefinition tableDefinition) throws IOException {

        // 写包名
        String javaStatement = "package "+StringUtil.dropLastChar(WDKConfig.basePackageName+WDKConfig.daoPackageName)+";\r\n";
        // 写import
        javaStatement += "import " + WDKConfig.basePackageName+ WDKConfig.entityBeanPackageName+MybatisUtil.generateEntityBeanName(tableDefinition)+";\r\n";
        javaStatement += "import java.util.List;\r\n";

        javaStatement += "public interface "+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+WDKConfig.daoStringName+"{\r\n";
        for (String function :
                ifFunctionList) {
            javaStatement+="\tpublic " + function+"\r\n";
        }
        javaStatement += "}";
        // 写定义
        bw.write(javaStatement);

    }
    private void generateDao(List<String> ifFunctionList,DBTableDefinition tableDefinition){
        String fullDaoPackageName = WDKConfig.basePackageName+WDKConfig.daoPackageName;
        String daoPackagePath = fullDaoPackageName.replaceAll("\\.","/");
        //System.out.println(projectDir+codeDir+daoPackagePath);
        String fullDaoPackagePath = WDKConfig.projectDir+WDKConfig.codeDir+daoPackagePath;
        FileUtil.createDirWithCheck(fullDaoPackagePath);
        // 生成文件
        File DaoFile = new File(fullDaoPackagePath+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+WDKConfig.daoStringName+".java");
        FileUtil.createFileWithDeleteFile(DaoFile);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(DaoFile));
            writeDaoFile(bw,ifFunctionList,tableDefinition);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeEntityBeanFile(BufferedWriter bw, DBTableDefinition tableDefinition) throws IOException {
        // 写包名
        String packageStatement = "package "+StringUtil.dropLastChar(WDKConfig.basePackageName+WDKConfig.entityBeanPackageName)+";\r\n";
        // 引入包
        String importSattement = "";
        // 写类定义
        String javaStatement = "public class "+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+"{\r\n";

        // 写属性定义
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        String getSetFunctionStr = "";
        for (DBTableColumnDefinition column :
                columnList) {
            String attributeName = MybatisUtil.generateEntityAttributeName(column.getColumnName());
            javaStatement +="\tprivate "+SQLKit.tableDefinitionTypeMapJavaType.get(column.getDataType()) + " "+attributeName+";\r\n";
            if(SQLKit.javaTypeMapImportPackage.get(SQLKit.tableDefinitionTypeMapJavaType.get(column.getDataType()))!=null){
                importSattement += "import "+SQLKit.javaTypeMapImportPackage.get(SQLKit.tableDefinitionTypeMapJavaType.get(column.getDataType()))+";\r\n";
            }
            getSetFunctionStr += "\tpublic "+SQLKit.tableDefinitionTypeMapJavaType.get(column.getDataType())+ " get"+StringUtil.firstLittle2UpCase(attributeName)+"(){\r\n";
            getSetFunctionStr += "\t\treturn "+attributeName+";\r\n";
            getSetFunctionStr += "\t}\r\n";
            getSetFunctionStr += "\tpublic void set"+StringUtil.firstLittle2UpCase(attributeName)+"("+SQLKit.tableDefinitionTypeMapJavaType.get(column.getDataType()) +" "+ attributeName+"){\r\n";
            getSetFunctionStr += "\t\tthis."+attributeName+"="+attributeName+";\r\n";
            getSetFunctionStr += "}\r\n";
        }
        javaStatement = packageStatement+importSattement+javaStatement+getSetFunctionStr+"}";

        bw.write(javaStatement);
    }

    private void generateEntityBean(DBTableDefinition tableDefinition){
        String fullBeanPackageName = WDKConfig.basePackageName+WDKConfig.entityBeanPackageName;
        String entityBeanPackagePath = fullBeanPackageName.replaceAll("\\.","/");
        String fullEntityBeanPackagePath = WDKConfig.projectDir+WDKConfig.codeDir+entityBeanPackagePath;
        FileUtil.createDirWithCheck(fullEntityBeanPackagePath);
        File EntityBeanFile = new File(fullEntityBeanPackagePath+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+WDKConfig.entityBeanStringName+".java");
        FileUtil.createFileWithDeleteFile(EntityBeanFile);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(EntityBeanFile));
            writeEntityBeanFile(bw,tableDefinition);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 生成mapper的xml和接口
     */
    public void generateMapperAndInterface(){
        for (DBTableDefinition tableDefinition :
                tableDefinitionList) {
            // 生成实体bean
            generateEntityBean(tableDefinition);
            // 生成mapper
            List<String> interfaceFunction =  generateMapper(tableDefinition);
            // 生成Dao
            generateDao(interfaceFunction,tableDefinition);
            //tableDefinition;
        }
    }
}
