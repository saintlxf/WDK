package pers.lxf.wdk.mvc;

import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MVCGenerateor {
    public void generateMVCClass(List<DBTableDefinition> tableDefinitionList){
        // 生成ViewBean
        generateViewBean(tableDefinitionList);
    }

    private void generateGetterSetterFunction(List<String> stringList,String attributeName,String attributeType){
        stringList.add("\tpublic "+attributeType+" get"+StringUtil.firstLittle2UpCase(attributeName)+"(){");
        stringList.add("\t\treturn this."+attributeName+";");
        stringList.add("\t}");
        stringList.add("\tpublic void set"+StringUtil.firstLittle2UpCase(attributeName)+"("+attributeType +" "+attributeName+"){");
        stringList.add("\t\tthis."+attributeName+"="+attributeName+";");
        stringList.add("\t}");
    }

    public List<String> generateViewBeanStringList(DBTableDefinition tableDefinition){
        List<String> result = new ArrayList<>();
        List<String> getterSetterList = new ArrayList<>();
        String className = StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+"View";
        // 包名
        result.add("package "+StringUtil.dropLastDot(WDKConfig.basePackageName+WDKConfig.viewBeanPackageName)+";");
        // import
        // 类定义
        result.add("public class "+className+"{");
        // 属性
        tableDefinition.getColumns().forEach(columnDefinition -> {
            String attributeName = MybatisUtil.generateEntityAttributeName(columnDefinition.getColumnName());
            String attributeType = SQLKit.tableDefinitionTypeMapJavaType.get(columnDefinition.getDataType());
            result.add("\tprivate "+ attributeType+" "+attributeName+";");
            generateGetterSetterFunction(getterSetterList,attributeName,attributeType);
            if(StringUtil.notEmpty(columnDefinition.getLinkOtherTable())){
                result.add("\tprivate String "+attributeName+"Value;");
                generateGetterSetterFunction(getterSetterList,attributeName+"Value","String");
            }
        });
        // getter、setter
        result.addAll(getterSetterList);
        result.add("}");
        return result;
    }

    private void generateViewBean(List<DBTableDefinition> tableDefinitionList) {
        // 代码目录
        String codePath = WDKConfig.projectDir+WDKConfig.codeDir;
        // 包名
        String packageName = WDKConfig.basePackageName+WDKConfig.viewBeanPackageName;
        // viewBeans 目录
        String viewBeansPath = codePath+packageName.replaceAll("\\.","/");

        FileUtil.createDirWithCheck(viewBeansPath);
        tableDefinitionList.stream().filter(tableDefinition -> tableDefinition.getColumns().stream().anyMatch(columnDefinition -> StringUtil.notEmpty(columnDefinition.getLinkOtherTable()))).forEach(tableDefinition -> {
            File viewBean = FileUtil.createFileWithString(viewBeansPath,StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+"View.java");
            (new FileOperator(viewBean)).writeFile(tableDefinition,table->generateViewBeanStringList(tableDefinition));
        });
    }
}
