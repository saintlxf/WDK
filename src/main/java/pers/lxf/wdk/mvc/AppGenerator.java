package pers.lxf.wdk.mvc;

import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.database.DBSchemaLoader;
import pers.lxf.wdk.util.FileUtil;
import pers.lxf.wdk.util.MybatisUtil;
import pers.lxf.wdk.util.ServiceUtil;
import pers.lxf.wdk.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AppGenerator {
    public static String appPackageName = "wdkApp.";
    public static String appRunnerClassName = "WdkRunner";
    public void generateApplicationRunner(List<DBTableDefinition> tableDefinitionList){
        String packagePath = WDKConfig.projectDir+ WDKConfig.codeDir+ (WDKConfig.basePackageName+appPackageName).replaceAll("\\.","/");
        File AppRunnerClassFile = FileUtil.createFileWithString(packagePath,appRunnerClassName+".java");
        List<DBTableDefinition> dictionaryTables =tableDefinitionList.stream().filter(DBTableDefinition::isDictionary).collect(Collectors.toList());

        List<String> contentList = new ArrayList<>();
        // 包名
        contentList.add("package "+StringUtil.dropLastDot(WDKConfig.basePackageName+appPackageName)+";");
        // 引用包
        addImport(contentList,dictionaryTables);
        // 定义类
        contentList.add("@Component");
        contentList.add("public class "+appRunnerClassName+" implements ApplicationRunner {");
        // 装配service
        wiredService(contentList,dictionaryTables);
        // 定义Map
        defineMap(contentList,dictionaryTables);
        // 实现方法
        implementMethod(contentList,dictionaryTables);
        contentList.add("}");
        FileUtil.writeFile(AppRunnerClassFile,contentList);
    }

    private void defineMap(List<String> contentList, List<DBTableDefinition> dictionaryTables) {
        dictionaryTables.stream().forEach(tableDefinition -> {
            String contentLine = "public static Map<Integer,String> MapDic"+ StringUtil.firstLittle2UpCase(tableDefinition.getTableName()) +"= new HashMap<>();";
            contentList.add(contentLine);
        });
    }

    public void addImport(List<String> contentList,List<DBTableDefinition> dictionaryTables){
        contentList.add("import org.springframework.beans.factory.annotation.Autowired;");
        contentList.add("import org.springframework.boot.ApplicationArguments;");
        contentList.add("import org.springframework.boot.ApplicationRunner;");
        contentList.add("import org.springframework.stereotype.Component;");
        contentList.add("import java.util.HashMap;");
        contentList.add("import java.util.List;");
        contentList.add("import java.util.Map;");
        dictionaryTables.stream().forEach(tableDefinition->{
            contentList.add("import " + WDKConfig.basePackageName + ServiceGenerator.servicePackageName + ServiceUtil.generateServiceClassName(tableDefinition)+";");
            contentList.add("import " + WDKConfig.basePackageName + WDKConfig.entityBeanPackageName + MybatisUtil.generateEntityBeanName(tableDefinition)+";");
        });
    }
    private void wiredService(List<String> contentList,List<DBTableDefinition> dictionaryTables){
        dictionaryTables.stream().forEach(tableDefinition->{
            contentList.add("@Autowired");
            String serviceName = ServiceUtil.generateServiceClassName(tableDefinition);
            contentList.add("private " + serviceName + " " +StringUtil.firstLittle2LowCase(serviceName)+";");
        });
    }
    private void implementMethod(List<String> contentList,List<DBTableDefinition> dictionaryTables){
        contentList.add("@Override");
        contentList.add("public void run(ApplicationArguments args) throws Exception {");
        // 读字典表
        dictionaryTables.forEach(tableDefinition -> {
            String serviceAttribute = StringUtil.firstLittle2LowCase(ServiceUtil.generateServiceClassName(tableDefinition));
            String tableName = tableDefinition.getTableName();
            contentList.add("\tList<"+MybatisUtil.generateEntityBeanName(tableDefinition)+"> all"+MybatisUtil.generateEntityBeanName(tableDefinition) +"="+serviceAttribute+"."+MybatisUtil.generateQueryAllMethodName(tableDefinition)+"();");
            contentList.add("\tall"+MybatisUtil.generateEntityBeanName(tableDefinition)+".forEach("+tableName+"->{");
            contentList.add("\t\t"+appRunnerClassName+".MapDic"+StringUtil.firstLittle2UpCase(tableName)+".put("+tableName+".getId(),"+tableName+".getValue());");
            contentList.add("});");
        });
        contentList.add("}");
    }
}
