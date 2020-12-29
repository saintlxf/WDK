package pers.lxf.wdk.util;

import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.mvc.ServiceGenerator;

public class ServiceUtil {
    public static String generateServiceClassName(DBTableDefinition tableDefinition){
        return StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+ ServiceGenerator.serviceSuffix;
    }
}
