package pers.lxf.wdk.beans;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ResourceUtils;
import pers.lxf.wdk.database.DBSchemaLoader;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DatabaseCreator implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DBSchemaLoader dbSchemaLoader;


    public static Workbook readExcel(File dbExcel){
        Workbook wb = null;
        String dbExcelFileName = dbExcel.getName();
        String extString = dbExcelFileName.substring(dbExcelFileName.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(dbExcel);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //System.out.println("start");
        // 获取数据库连接
        //System.out.println(ClassPath.getClassPath());
//        try {
//            File cfgFile = ResourceUtils.getFile("classpath:schema.xlsx");
//            Workbook workbook = readExcel(cfgFile);
//            if(workbook!=null){
//                Sheet sheet0 = workbook.getSheetAt(0);
//                Cell cell = sheet0.getRow(1).getCell(1);
//                System.out.println(cell.getNumericCellValue());
//                List<String> ll = new LinkedList<String>();
//                List<DBTable> listTable = jdbcTemplate.query("SELECT table_name FROM information_schema.tables WHERE table_schema='myweb'",new tableRowMapper());
//
//                for (DBTable dbTable :
//                        listTable) {
//                    System.out.println(dbTable.getTableName());
//                }
//                String sql = "CREATE TABLE `bbb` (`id` int(11) NOT NULL AUTO_INCREMENT,  `aaa` varchar(32) DEFAULT NULL COMMENT '这是aaa',  `bbb` datetime DEFAULT NULL COMMENT '这是时间',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
//
//                jdbcTemplate.execute(sql);
//            }
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
        //dbSchemaLoader.loadSchema();

    }
    private static final class tableRowMapper implements RowMapper<DBTable> {

        public DBTable mapRow(ResultSet resultSet, int i) throws SQLException {
            return new DBTable(resultSet.getString("table_name"));
        }
    }
}
