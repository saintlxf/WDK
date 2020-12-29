package pers.lxf.wdk.template;

import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.util.HtmlUtil;
import pers.lxf.wdk.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HtmlTemplate {
    public static String generateHtml(List<String> header, List<String> body,List<String> includeScript){
        String htmlText = "<!DOCTYPE html>\r\n";
        htmlText += "<html>\r\n";
        String headerText = "";
        headerText +="<head>\r\n";
        if(header!=null) {
            for (String str :
                    header) {
                headerText += str + "\r\n";
            }
            headerText += "</head>\r\n";
        }
        String bodyText = "";
        bodyText += "<body>\r\n";
        if(body!=null) {
            for (int i = 0; i < body.size(); i++) {
                bodyText += body.get(i) + "\r\n";
            }
        }
        String scriptText = "";
        if(includeScript!=null) {
            for (int i = 0; i < includeScript.size(); i++) {
                scriptText += includeScript.get(i) + "\r\n";
            }
        }


        htmlText = htmlText + headerText+bodyText+scriptText;
        htmlText += "</body>\r\n";
        htmlText += "</html>";
        return htmlText;
    }

    private static void checkAndCompleteAttribute(String attributeName,String attributeValue,StringBuffer buttonText){
        if(!StringUtil.isEmpty(attributeValue)){
            buttonText.append(" "+attributeName +"=\""+attributeValue+"\"");
        }
    }

    public static List<String> generateButton(String id, String name, String value,String cssClass,String cssStyle){
        List<String> result = new ArrayList<>();
        StringBuffer buttonText = new StringBuffer();
        buttonText.append("<button");
        checkAndCompleteAttribute("id",id,buttonText);
        checkAndCompleteAttribute("name",name,buttonText);
        checkAndCompleteAttribute("class",cssClass,buttonText);
        checkAndCompleteAttribute("style",cssStyle,buttonText);
        buttonText.append(">");
        if(!StringUtil.isEmpty(value)){
            buttonText.append(value+"</button>");
        }
        result.add(buttonText.toString());
        return result;
    }
    public static List<String> generateModalButton(String id,String value,String cssClass,String modalId){
        List<String> result = new ArrayList<>();
        if(StringUtil.isEmpty(modalId)){
            return result;
        }
        StringBuffer buttonText = new StringBuffer();
        buttonText.append("<button");
        checkAndCompleteAttribute("id",id,buttonText);
        checkAndCompleteAttribute("class",cssClass,buttonText);
        buttonText.append(" data-toggle=\"modal\" data-target=\"#"+modalId+"\"");
        buttonText.append(">"+value+"</button>");
        result.add(buttonText.toString());
        return result;
    }

    public static String generateButton(String value,String cssClass,String custom){
        StringBuffer buttonText = new StringBuffer();
        buttonText.append("<button");
        checkAndCompleteAttribute("class",cssClass,buttonText);
        buttonText.append(" "+custom+">"+value+"</button>");
        return buttonText.toString();
    }

    public static String generateHiddenInput(String id,String name,String value){
        StringBuffer txt = new StringBuffer();
        txt.append("<input type=\"hidden\"");
        checkAndCompleteAttribute("id",id,txt);
        checkAndCompleteAttribute("name",name,txt);
        checkAndCompleteAttribute("value",value,txt);
        txt.append("></input>");
        return txt.toString();
    }

    public static List<String> generateModel(int indentLevel,String modalId,String title,String submitBtnId,List<String> modalBody){
        List<String> result = new ArrayList<>();

        String baseIndent = StringUtil.generateSameChar(indentLevel,"\t");
        result.add(baseIndent + "<div class=\"modal fade\" id=\""+modalId+"\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\">");
        result.add(baseIndent + "\t<div class=\"modal-dialog\">");
        result.add(baseIndent + "\t\t<div class=\"modal-content\">");
        result.add(baseIndent + "\t\t\t<div class=\"modal-header\">");
        result.add(baseIndent + "\t\t\t\t<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">×</button>");
        result.add(baseIndent + "\t\t\t\t<h4 class=\"modal-title\" id=\"myModalLabel\">"+title+"</h4>");
        result.add(baseIndent + "\t\t\t</div>");
        result.add(baseIndent + "\t\t\t<div class=\"modal-body\">");
        result.addAll(modalBody);
        result.add(baseIndent+"\t\t\t</div>");
        result.add("\t\t\t<div class=\"modal-footer\">");
        result.add("\t\t\t\t<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">关闭</button>");
        result.add("\t\t\t\t<button id=\""+ submitBtnId +"\" type=\"button\" class=\"btn btn-primary\">提交更改</button>");
        result.add("\t\t\t</div>");
        result.add("\t\t</div>");
        result.add("\t</div>");
        result.add("</div>");
        return result;
    }
    public static List<String> generateTable(String tableDef,int indentLevel,String header,String footer,List<String> rows){
        List<String> result = new ArrayList<String>();

        String baseIndent = StringUtil.generateSameChar(indentLevel,"\t");
        result.add(baseIndent+tableDef);
        if(header!=null&&header.trim().length()>0){
            result.add(baseIndent+"\t<thead>");
            result.add(baseIndent+"\t\t<tr>");
            result.add(baseIndent+"\t\t\t"+header);
            result.add(baseIndent+"\t\t</tr>");
            result.add(baseIndent+"\t</thead>");
        }
        if(rows!=null&&rows.size()>0){
            result.add(baseIndent+"\t<tbody>");
            for (int i = 0; i < rows.size(); i++) {
                result.add(baseIndent+"\t\t<tr>");
                result.add(baseIndent+"\t\t\t"+rows.get(i));
                result.add(baseIndent+"\t\t</tr>");
            }
            result.add(baseIndent+"\t</tbody>");
        }
        if(footer!=null&&footer.trim().length()>0){
            result.add(baseIndent+"\t<thead>");
            result.add(baseIndent+"\t\t<tr>");
            result.add(baseIndent+"\t\t\t"+footer);
            result.add(baseIndent+"\t\t</tr>");
            result.add(baseIndent+"\t</thead>");
        }
        result.add(baseIndent+"</table>");
        return result;
    }
    public static List<String> generateIncludeScriptList(List<String> includeScript,int indentLevel){
        List<String> result = new ArrayList<String>();

        String baseIndent = StringUtil.generateSameChar(indentLevel,"\t");
        for (int i = 0; i < includeScript.size(); i++) {
            result.add(baseIndent+"<script type=\"text/javascript\" language=\"javascript\" src=\""+includeScript.get(i)+"\" >");
            result.add(baseIndent+"</script>");
        }
        return result;
    }
}
