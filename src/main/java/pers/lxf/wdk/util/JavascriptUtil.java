package pers.lxf.wdk.util;

public class JavascriptUtil {
    public static String generateDataTableName(String tableName){
        return tableName+"_table";
    }
    public static String generateDataTableUpdateFunctionName(String tableName){
        return tableName+"Update";
    }
    public static String generateDataTableDeleteFunctionName(String tableName){
        return tableName+"Delete";
    }
}
