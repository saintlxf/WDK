package pers.lxf.wdk.util;

import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.mvc.ControllerGenerator;

public class ControllerUtil {
    public static String generateControllerClassName(DBTableDefinition tableDefinition){
        return StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+ ControllerGenerator.controllerClassSuffix;
    }
}
