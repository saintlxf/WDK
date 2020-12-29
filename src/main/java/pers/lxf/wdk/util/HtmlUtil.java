package pers.lxf.wdk.util;

import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;

import java.util.ArrayList;
import java.util.List;

public class HtmlUtil {
    public static String maintainFileSuffix = "Maintain";
    public static String generateHtmlFileName(DBTableDefinition tableDefinition){
        return tableDefinition.getTableName()+maintainFileSuffix;
    }
    public static String generateAddButtonIdAndName(String tableName){
        return "add"+StringUtil.firstLittle2UpCase(tableName)+"Btn";
    }
    public static String generateMaintainModalDialogName(String tableName){
        return "modal"+StringUtil.firstLittle2UpCase(tableName)+"Maintain";
    }
    public static String generateMaintainFormName(String tableName){
        return "maintain"+StringUtil.firstLittle2UpCase(tableName)+"Form";
    }
    public static List<String> generateFormElementByColumnDef(DBTableColumnDefinition columnDefinition){
        List<String> result = new ArrayList<>();
        if(columnDefinition.isBeMaintain()) {
            String entityAttributeName = MybatisUtil.generateEntityAttributeName(columnDefinition.getColumnName());
            result.add("<div class=\"form-group\">");
            result.add("<label for=\"" + entityAttributeName + "\">" + columnDefinition.getColumnName() + "</label>");
            result.add("<input type=\"text\" class=\"form-control\" id=\"" + entityAttributeName + "\" name=\""+entityAttributeName+"\">");
            result.add("</div>");
        }
        return result;
    }
    public static String generateFormOptionTypeHiddenInputName(String tableName){
        return tableName+"OptType";
    }
    public static String generateMaintainModalSubmitButtonId(String tableName){
        return tableName+"MaintainSubmitBtn";
    }
}
