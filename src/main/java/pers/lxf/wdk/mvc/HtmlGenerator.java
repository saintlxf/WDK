package pers.lxf.wdk.mvc;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.template.FileWriterTemplate;
import pers.lxf.wdk.template.HtmlTemplate;
import pers.lxf.wdk.template.JavascriptTemplate;
import pers.lxf.wdk.util.FileUtil;
import pers.lxf.wdk.util.HtmlUtil;
import pers.lxf.wdk.util.JavascriptUtil;
import pers.lxf.wdk.util.MybatisUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Html代码生成器
 */
public class HtmlGenerator {
    public static String staticDir = "static/";
    public static String jsDir = "js/";
    public static String resourceDataTableImagePath = "images/";
    public static boolean needCopyDependencyFile = true;
    // 生成Html代码
    public void generateHtml(List<DBTableDefinition> tableDefinitionList){
        String htmlPath = WDKConfig.projectDir+ WDKConfig.resourceDir+staticDir;
        for (DBTableDefinition tableDefinition :
                tableDefinitionList) {
            if(tableDefinition.isMaintainPage()) {
                String maintainHtmlFileName = HtmlUtil.generateHtmlFileName(tableDefinition);
                FileWriterTemplate ft = new FileWriterTemplate(htmlPath, maintainHtmlFileName + ".html");
                String htmlText = generateHtmlByTemplate(tableDefinition);
                ft.writeFile(htmlText);
            }
        }
        File dataTableImageDir = null;
        try {
            dataTableImageDir = ResourceUtils.getFile("classpath:page_resource/images/dateTable/");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File jsDir = FileUtil.getResourceFile("classpath:page_resource/js/");

        // 拷贝css、image和js文件
        FileUtil.copyDirAllFiles(dataTableImageDir,new File(WDKConfig.projectDir+ WDKConfig.resourceDir+staticDir+resourceDataTableImagePath));
        try {
            FileUtils.copyDirectory(jsDir,new File(WDKConfig.projectDir+ WDKConfig.resourceDir+staticDir+"js/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String generateHtmlByTemplate(DBTableDefinition tableDefinition){
        List<String> headTexts = generateHeadText();
        List<String> bodyTexts = generateBodyText(tableDefinition);
        List<String> scriptTexts = generateScriptText(tableDefinition);
        String htmlText = HtmlTemplate.generateHtml(headTexts,bodyTexts,scriptTexts);
        return htmlText;
    }

    private List<String> generateScriptText(DBTableDefinition tableDefinition) {
        List<String> result = new ArrayList<String>();
        List<String> scriptFileList = new ArrayList<String>();
        scriptFileList.add("js/jquery-3.4.1.js");
        scriptFileList.add("js/jquery.dataTables.js");
        scriptFileList.add("/components/bootstrap/js/bootstrap.js");
        scriptFileList.add("js/jqueryExt.js");
        scriptFileList.add("js/pageCommon.js");
        // 生成script文件
        List<String> scriptFiles = generateScriptFile(tableDefinition);
        // 加入需要引入的列表
        scriptFileList.addAll(scriptFiles);

        List<String> scriptTextList = HtmlTemplate.generateIncludeScriptList(scriptFileList,1);

        result.addAll(scriptTextList);


        return result;
    }

    private List<String> generateScriptFile(DBTableDefinition tableDefinition) {
        List<String> result = new ArrayList<String>();
        String jsPath = WDKConfig.projectDir+ WDKConfig.resourceDir+staticDir+jsDir;
        String maintainHtmlFileName = HtmlUtil.generateHtmlFileName(tableDefinition);
        FileWriterTemplate ft = new FileWriterTemplate(jsPath,maintainHtmlFileName+".js");
        String scriptText = generateScriptDataTableScript(tableDefinition);
        ft.writeFile(scriptText);
        result.add(jsDir+maintainHtmlFileName+".js");
        return  result;
    }


    private String generateScriptDataTableScript(DBTableDefinition tableDefinition) {
        String ret = "";
        // 全局变量
        ret += "var "+JavascriptUtil.generateDataTableName(tableDefinition.getTableName())+";\r\n";
        List<String> scriptStringList = new ArrayList<String>();
        scriptStringList.add("\t"+ JavascriptUtil.generateDataTableName(tableDefinition.getTableName()) +"=$('#"+tableDefinition.getTableName()+"_table').DataTable( {");
        scriptStringList.add("\t\t\"ajax\": {");
        scriptStringList.add("\t\t\turl:'/"+tableDefinition.getTableName()+"/queryAll',");
        scriptStringList.add("\t\t\t\"dataSrc\":function (result) {");
        scriptStringList.add("\t\t\t\treturn result;");
        scriptStringList.add("\t\t\t}},");
        scriptStringList.add("\t\t\"columns\": [");
        for (int i = 0; i < tableDefinition.getColumns().size(); i++) {
            scriptStringList.add("\t\t\t{ data:"+ "\""+ MybatisUtil.generateEntityAttributeName(tableDefinition.getColumns().get(i).getColumnName())+"\"},");
        }
        scriptStringList.add("\t\t\t{ data: null,\"render\":function(data,type,row,meta){");
        String returnStr = "";
        returnStr += HtmlTemplate.generateButton("修改","btn btn-primary btn-sm","onclick=\""+JavascriptUtil.generateDataTableUpdateFunctionName(tableDefinition.getTableName())+"(\\\''+data.id+'\\\')\"");
        returnStr += HtmlTemplate.generateButton("删除","btn btn-warning btn-sm","onclick=\""+JavascriptUtil.generateDataTableDeleteFunctionName(tableDefinition.getTableName())+"(\\\''+data.id+'\\\')\"");
        scriptStringList.add("\t\t\t\treturn '"+returnStr+"';");
        scriptStringList.add("\t\t\t}}");
        scriptStringList.add("\t\t]");
        scriptStringList.add("\t});");

        List<String> scriptList =  JavascriptTemplate.generateDocumentReady(scriptStringList,0);
        for (int i = 0; i < scriptList.size(); i++) {
            ret += scriptList.get(i)+"\r\n";
        }
        List<String> addButtonScriptList = JavascriptTemplate.generateAddButtonEvent(tableDefinition.getTableName());
        for(int i=0;i<addButtonScriptList.size();i++){
            ret += addButtonScriptList.get(i)+"\r\n";
        }
        // 模态框提交按钮script
        List<String> modalSubmitScriptList = JavascriptTemplate.generateModalSubmitScript(tableDefinition.getTableName());
        for(int i=0;i<modalSubmitScriptList.size();i++){
            ret += modalSubmitScriptList.get(i)+"\r\n";
        }
        // 生成修改按钮的script函数
        List<String> updateScriptContent = JavascriptTemplate.generateUpdateScript(tableDefinition.getTableName());
        List<String> tableCustomButtonScriptList = JavascriptTemplate.generateFunctionScript(JavascriptUtil.generateDataTableUpdateFunctionName(tableDefinition.getTableName()),"id",updateScriptContent);
        for(int i=0;i<tableCustomButtonScriptList.size();i++){
            ret += tableCustomButtonScriptList.get(i)+"\r\n";
        }
        // 生成删除按钮的script函数
        List<String> deleteScriptContent = JavascriptTemplate.generateDeleteScript(tableDefinition.getTableName());
        List<String> deletemButtonScriptList = JavascriptTemplate.generateFunctionScript(JavascriptUtil.generateDataTableDeleteFunctionName(tableDefinition.getTableName()),"id",deleteScriptContent);
        for(int i=0;i<deletemButtonScriptList.size();i++){
            ret += deletemButtonScriptList.get(i)+"\r\n";
        }
        return ret;
    }


    private List<String> generateBodyText(DBTableDefinition tableDefinition) {
        List<String> result = new ArrayList<String>();
        // 增加按钮
        List<String> buttonAddText = HtmlTemplate.generateModalButton(HtmlUtil.generateAddButtonIdAndName(tableDefinition.getTableName()),"添加","btn btn-primary btn-sm",HtmlUtil.generateMaintainModalDialogName(tableDefinition.getTableName()));
        String headerText = "";
        for (int i = 0; i < tableDefinition.getColumns().size(); i++) {
            DBTableColumnDefinition column = (DBTableColumnDefinition) tableDefinition.getColumns().get(i);
            headerText += "<th>"+column.getDisplayTxt()+"</th>";
        }
        headerText += "<th>操作</th>";
        String tableDef = "<table id=\""+tableDefinition.getTableName()+"_table\" class=\"display\" cellspacing=\"0\" width=\"100%\">";
        List<String> tableStringList = HtmlTemplate.generateTable(tableDef,1,headerText,null,null);
        result.addAll(buttonAddText);
        result.addAll(tableStringList);
        // 增加模块框
        List<String> maintainModelList = generateMaintainMole(tableDefinition);
        result.addAll(maintainModelList);
        return result;
    }

    private List<String> generateMaintainMole(DBTableDefinition tableDefinition) {
        List<String> modalBody = new ArrayList<>();
        modalBody.add("<form id=\""+HtmlUtil.generateMaintainFormName(tableDefinition.getTableName())+"\">");
        // 增加隐藏的id域
        modalBody.add(HtmlTemplate.generateHiddenInput("id","id",""));
        // 根据表定义，生成需要添加的表单
        for (DBTableColumnDefinition column :
                tableDefinition.getColumns()) {
            modalBody.addAll(HtmlUtil.generateFormElementByColumnDef(column));

        }
        // 增加hidden域
        modalBody.add(HtmlTemplate.generateHiddenInput(HtmlUtil.generateFormOptionTypeHiddenInputName(tableDefinition.getTableName()),HtmlUtil.generateFormOptionTypeHiddenInputName(tableDefinition.getTableName()),""));
        modalBody.add("</form>");
        List<String> modalTextList = HtmlTemplate.generateModel(1,HtmlUtil.generateMaintainModalDialogName(tableDefinition.getTableName()),"添加数据",HtmlUtil.generateMaintainModalSubmitButtonId(tableDefinition.getTableName()),modalBody);
        return modalTextList;
    }

    private List<String> generateHeadText() {
        List<String> result = new ArrayList<String>();
        result.add("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/components/bootstrap/css/bootstrap.min.css\">");
        result.add("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/jquery.dataTables.css\">");
        return result;
    }

}
