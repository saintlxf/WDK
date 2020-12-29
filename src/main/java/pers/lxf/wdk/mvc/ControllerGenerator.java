package pers.lxf.wdk.mvc;

import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.database.DBSchemaLoader;
import pers.lxf.wdk.util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ControllerGenerator {
    public static String controllerPackageName = "controllers.";
    public static String controllerClassSuffix = "Ctrl";
    public void generateController(List<DBTableDefinition> tableDefinitionList){
        // 得到包路径
        String packagePath = WDKConfig.projectDir+ WDKConfig.codeDir+ (WDKConfig.basePackageName+controllerPackageName).replaceAll("\\.","/");
        for (DBTableDefinition tableDefinition :
                tableDefinitionList) {
            // 生成Controller类文件
            String className = ControllerUtil.generateControllerClassName(tableDefinition);
            File controllerClassFile = FileUtil.createFileWithString(packagePath,className+".java");
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(controllerClassFile));
                writeControllClass(bw,tableDefinition);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void writeControllClass(BufferedWriter bw, DBTableDefinition tableDefinition) throws IOException {
        List<DBTableColumnDefinition> primaryColumns = MybatisUtil.getPrimaryColumnList(tableDefinition);
        // 写package
        String packageStatement = "package "+WDKConfig.basePackageName+ StringUtil.dropLastDot(controllerPackageName)+";\r\n";
        // 写import
        String importStatement = "import org.springframework.web.bind.annotation.RequestMapping;\r\n";
        importStatement += "import org.springframework.beans.factory.annotation.Autowired;\r\n";
        importStatement += "import org.springframework.web.bind.annotation.RestController;\r\n";
        importStatement += "import org.springframework.web.bind.annotation.RequestBody;\r\n";
        importStatement += "import "+WDKConfig.basePackageName+ServiceGenerator.servicePackageName+ServiceUtil.generateServiceClassName(tableDefinition)+";\r\n";
        importStatement += "import "+WDKConfig.basePackageName+ WDKConfig.entityBeanPackageName+MybatisUtil.generateEntityBeanName(tableDefinition)+";\r\n";
        importStatement += "import java.util.List;\r\n";
        // 写class定义
        String javaStatement = "";
        javaStatement += "@RestController\r\n";
        javaStatement += "@RequestMapping(\"/"+tableDefinition.getTableName()+"\")\r\n";
        javaStatement += "public class "+ControllerUtil.generateControllerClassName(tableDefinition)+"{\r\n";
        // 写注入的service
        String serviceObjectName = StringUtil.firstLittle2LowCase(ServiceUtil.generateServiceClassName(tableDefinition));
        javaStatement += "\t@Autowired\r\n";
        javaStatement += "\tprivate "+ ServiceUtil.generateServiceClassName(tableDefinition) + " "+ serviceObjectName+";\r\n";
        // 写添加方法
        javaStatement += "\t@RequestMapping(\"/add\")\r\n";
        String entityParameter = MybatisUtil.generateEntityBeanName(tableDefinition)+" "+StringUtil.firstLittle2LowCase(MybatisUtil.generateEntityBeanName(tableDefinition));
        javaStatement += "\tpublic void add(@RequestBody "+entityParameter+"){\r\n";
        javaStatement += "\t\t"+serviceObjectName+".add("+StringUtil.firstLittle2LowCase(MybatisUtil.generateEntityBeanName(tableDefinition))+");\r\n";
        javaStatement += "\t}\r\n";
        // 写update方法
        javaStatement += "\t@RequestMapping(\"/update\")\r\n";
        javaStatement += "\tpublic void updateBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"(@RequestBody "+entityParameter+"){\r\n";
        javaStatement += "\t\t"+serviceObjectName+".updateBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+StringUtil.firstLittle2LowCase(MybatisUtil.generateEntityBeanName(tableDefinition))+");\r\n";
        javaStatement += "\t}\r\n";

        // 写delete方法
        javaStatement += "\t@RequestMapping(\"/delete\")\r\n";
        javaStatement += "\tpublic void deleteBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumns)+"){\r\n";
        javaStatement += "\t\t"+serviceObjectName+".deleteBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+MybatisUtil.generateFunctionParameter(primaryColumns)+");\r\n";
        javaStatement += "\t}\r\n";
        // 写查询方法
        javaStatement += "\t@RequestMapping(\"/query\")\r\n";
        javaStatement += "\tpublic "+MybatisUtil.generateEntityBeanName(tableDefinition)+" findBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumns)+"){\r\n";
        javaStatement += "\t\treturn "+serviceObjectName+"."+MybatisUtil.generateMethodNameByPrimaryKey(primaryColumns)+"("+MybatisUtil.generateFunctionParameter(primaryColumns)+");\r\n";
        javaStatement += "\t}\r\n";
        // 写查询所有记录方法
        javaStatement += "\t@RequestMapping(\"/queryAll\")\r\n";
        javaStatement += "\tpublic List<"+MybatisUtil.generateEntityBeanName(tableDefinition)+"> "+MybatisUtil.generateQueryAllMethodName(tableDefinition)+"(){\r\n";
        javaStatement += "\t\treturn "+serviceObjectName+"."+MybatisUtil.generateQueryAllMethodName(tableDefinition)+"();\r\n";
        javaStatement += "\t}\r\n";

        javaStatement += "}";

        javaStatement = packageStatement+importStatement+javaStatement;
        bw.write(javaStatement);
    }
}
