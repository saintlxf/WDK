package pers.lxf.wdk.template;

import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.util.ControllerUtil;
import pers.lxf.wdk.util.HtmlUtil;
import pers.lxf.wdk.util.JavascriptUtil;
import pers.lxf.wdk.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class JavascriptTemplate {
    public static List<String> generateDocumentReady(List<String> scriptStatementList,int indentLevel){
        List<String> result = new ArrayList<String>();

        String baseIndent = StringUtil.generateSameChar(indentLevel,"\t");

        result.add(baseIndent+"$(document).ready(function() {");
        for (int i = 0; i < scriptStatementList.size(); i++) {
            result.add(baseIndent+scriptStatementList.get(i));
        }
        result.add("});");
        return result;
    }

    public static List<String> generateAddButtonEvent(String tableName){
        List<String> result = new ArrayList<>();
        result.add("$(\"#"+ HtmlUtil.generateAddButtonIdAndName(tableName)+"\").click(function(){");
        result.add("\tclearFormInputContent($(\"#"+HtmlUtil.generateMaintainFormName(tableName)+"\"));");
        result.add("\t$(\"#"+HtmlUtil.generateFormOptionTypeHiddenInputName(tableName)+"\").val('add');");
        result.add("\tconsole.log('click');");
        result.add("});");
        return result;
    }

    public static List<String> generateModalSubmitScript(String tableName){
        List<String> result = new ArrayList<>();

        result.add("$(\"#"+ HtmlUtil.generateMaintainModalSubmitButtonId(tableName)+"\").click(function(){");
        result.add("\tvar url = \"\";");
        result.add("\tif($(\"#"+HtmlUtil.generateFormOptionTypeHiddenInputName(tableName)+"\").val()=='add'){");
        result.add("\t\turl='/"+tableName+"/add';");
        result.add("\t}else if($(\"#"+HtmlUtil.generateFormOptionTypeHiddenInputName(tableName)+"\").val()=='update'){");
        result.add("\t\turl='/"+tableName+"/update';");
        result.add("\t}");
        result.add("\tvar formData = $(\"#"+HtmlUtil.generateMaintainFormName(tableName)+"\").serializeObject();");
        result.add("\t$.ajax({");
        result.add("\t\ttype : 'post',");
        result.add("\t\tdataType : 'text',");
        result.add("\t\turl : url,");
        result.add("\t\tdata : JSON.stringify(formData),");
        result.add("\t\tcontentType : 'application/json',");
        result.add("\t\tsuccess : function(){");
        result.add("\t\t\tconsole.log('submit success');");
        result.add("\t\t\t"+ JavascriptUtil.generateDataTableName(tableName)+".ajax.reload();");
        result.add("\t\t}");
        result.add("\t});");
        result.add("\t$(\"#"+HtmlUtil.generateMaintainModalDialogName(tableName)+"\").modal('hide');");

        result.add("});");
        return result;
    }
    public static List<String> generateFunctionScript(String functionName,String parameter,List<String> content){
        List<String> result = new ArrayList<>();
        result.add("var "+functionName+"=function("+parameter+"){");
        result.addAll(content);
        result.add("}");
        return result;
    }

    public static  List<String> generateUpdateScript(String tableName){
        List<String> result = new ArrayList<>();
        result.add("$.ajax({");
        result.add("\turl: '/"+tableName+"/query?id='+id,");
        result.add("\tmethod: 'get',");
        result.add("\tsuccess: function(data) {");
        result.add("\t\t$(\"#"+HtmlUtil.generateMaintainModalDialogName(tableName)+" input\").each(function(){");
        result.add("\t\t\t$(this).val(data[this.name]);");
        result.add("\t\t});");
        result.add("\t\t$(\"#"+HtmlUtil.generateFormOptionTypeHiddenInputName(tableName)+"\").val('update');");
        result.add("\t}");
        result.add("});");

        // 弹出模态框
        result.add("$(\"#"+HtmlUtil.generateMaintainModalDialogName(tableName)+"\").modal('show');");

        return result;
    }
    public static List<String> generateDeleteScript(String tableName){
        List<String> result = new ArrayList<>();
        result.add("$.ajax({");
        result.add("\turl: '/"+tableName+"/delete?id='+id,");
        result.add("\tmethod: 'get',");
        result.add("\tsuccess: function(data) {");
        result.add("\t\t"+ JavascriptUtil.generateDataTableName(tableName)+".ajax.reload();");
        result.add("\t}");
        result.add("});");
        return result;
    }
}
