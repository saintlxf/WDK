package pers.lxf.wdk.util;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SQLKit {
    public static Map<String,String> jdbcTypeMapJava = new HashMap<String, String>(){
        {
            put("CHAR","String");
            put("VARCHAR","String");
            put("LONGVARCHAR","String");
            put("NUMERIC","java.math.BigDecimal");
            put("DECIMAL","java.math.BigDecimal");
            put("BIT","boolean");
            put("BOOLEAN","boolean");
            put("TINYINT","byte");
            put("SMALLINT","short");
            put("INTEGER","INTEGER");
            put("BIGINT","long");
        }
    };

    public static Map<String,String> tableDefinitionTypeMapJdbcType = new HashMap<String, String>(){{
       put("int","INTEGER");
       put("varchar","VARCHAR");
       put("datetime","DATE");
    }};
    public static Map<String,String> tableDefinitionTypeMapJavaType = new HashMap<String, String>(){{
        put("int","int");
        put("varchar","String");
        put("datetime","Date");
    }};

    public static Map<String,String> javaTypeMapImportPackage = new HashMap<String, String>(){{
        put("Date","java.util.Date");
    }};

    public String generateCreateTableSQL(){
        return "";
    }
    public static boolean transformString2Boolean(String str){
        if(str==null){
            return false;
        }
        if("yes".equals(str.trim().toLowerCase())||"是".equals(str.trim().toLowerCase())){
            return true;
        }
        return false;
    }
    public static boolean isPrimeryKey(String str){
        if(str==null){
            return false;
        }
        if("PRI".equals(str.trim())){
            return true;
        }
        return false;
    }
    public static boolean isIncrement(String str){
        if(str==null){
            return false;
        }
        if("auto_increment".equals(str.trim())){
            return true;
        }
        return false;
    }

    /**
     * 判断数据类型是否需要指定长度
     * @param dataType
     * @return
     */
    public static boolean isDataTypeNeedLength(String dataType){
        if("char".equals(dataType.trim().toLowerCase())||"varchar".equals(dataType.trim().toLowerCase())){
            return true;
        }
        return false;
    }
}
