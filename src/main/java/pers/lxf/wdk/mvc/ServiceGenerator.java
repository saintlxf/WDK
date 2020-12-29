package pers.lxf.wdk.mvc;

import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.database.DBSchemaLoader;
import pers.lxf.wdk.util.FileUtil;
import pers.lxf.wdk.util.MybatisUtil;
import pers.lxf.wdk.util.SQLKit;
import pers.lxf.wdk.util.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ServiceGenerator {
    public static String servicePackageName = "services.";
    public static String serviceSuffix = "Service";

    private void writeInterfaceFile(BufferedWriter bw,String packageFullName,DBTableDefinition tableDefinition) throws IOException {
        String javaStatement = "";
        // 写包
        javaStatement += "package "+ packageFullName+";\r\n";
        // 写import
        // 得到实体bean的类名
        String entityBeanName = StringUtil.firstLittle2UpCase(tableDefinition.getTableName());
        List<DBTableColumnDefinition> primaryColumns = MybatisUtil.getPrimaryColumnList(tableDefinition);
        javaStatement += "import "+ WDKConfig.basePackageName+ WDKConfig.entityBeanPackageName+entityBeanName+";\r\n";
        javaStatement += "import java.util.List;\r\n";
        // 写class
        javaStatement +="public interface "+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+serviceSuffix+"{\r\n";
        // 写方法定义
        javaStatement += "\tpublic void add("+entityBeanName + " "+StringUtil.firstLittle2LowCase(entityBeanName)+");\r\n"; // add方法

        javaStatement += "\tpublic void deleteBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumns)+");\r\n"; // delete方法
        javaStatement += "\tpublic void updateBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+entityBeanName + " "+StringUtil.firstLittle2LowCase(entityBeanName)+");\r\n"; // update方法
        List<DBTableColumnDefinition> primaryColumnList = MybatisUtil.getPrimaryColumnList(tableDefinition);
        javaStatement += "\tpublic "+MybatisUtil.generateEntityBeanName(tableDefinition)+" "+MybatisUtil.generateMethodNameByPrimaryKey(primaryColumnList)+"(";
        for (DBTableColumnDefinition column :
                primaryColumnList) {
            javaStatement += SQLKit.tableDefinitionTypeMapJavaType.get(column.getDataType()) + " " + column.getColumnName() + ",";
        }
        javaStatement = StringUtil.dropLastChar(javaStatement);
        javaStatement+=");\r\n";

        javaStatement += "\tpublic List<"+MybatisUtil.generateEntityBeanName(tableDefinition)+"> "+MybatisUtil.generateQueryAllMethodName(tableDefinition)+"();\r\n";
        javaStatement += "}";
        bw.write(javaStatement);
    }

    private void writeInterfaceImplFile(BufferedWriter bw, String implPackageFullName, DBTableDefinition tableDefinition) throws IOException {
        String javaStatement = "";
        javaStatement += "package "+ implPackageFullName+";\r\n";
        List<DBTableColumnDefinition> primaryColumns = MybatisUtil.getPrimaryColumnList(tableDefinition);

        // 写import
        String entityBeanName = StringUtil.firstLittle2UpCase(tableDefinition.getTableName());
        javaStatement += "import "+WDKConfig.basePackageName+ WDKConfig.entityBeanPackageName+entityBeanName+";\r\n";
        javaStatement += "import "+WDKConfig.basePackageName+ WDKConfig.daoPackageName+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+ WDKConfig.daoStringName+";\r\n";
        javaStatement += "import "+WDKConfig.basePackageName+servicePackageName+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+serviceSuffix+";\r\n";
        javaStatement += "import org.springframework.beans.factory.annotation.Autowired;\r\n";
        javaStatement += "import org.springframework.stereotype.Service;\r\n";
        javaStatement += "import java.util.List;\r\n";
        // 写class定义
        javaStatement += "@Service\r\n";
        javaStatement += "public class "+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+serviceSuffix+"Impl implements "+StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+serviceSuffix+"{\r\n";
        // 写自动装配注解
        javaStatement += "\t@Autowired\r\n";
        String daoBeanName = StringUtil.firstLittle2LowCase(MybatisUtil.generateDaoClassName(tableDefinition));
        javaStatement += "\tprivate "+MybatisUtil.generateDaoClassName(tableDefinition)+" " + daoBeanName +";\r\n";

        // 写add方法
        javaStatement += "\t@Override\r\n";
        javaStatement += "\tpublic void add("+entityBeanName + " "+StringUtil.firstLittle2LowCase(entityBeanName)+"){\r\n";
        javaStatement += "\t\t"+daoBeanName+"."+MybatisUtil.generateInsertMethodName(tableDefinition)+"("+StringUtil.firstLittle2LowCase(entityBeanName)+");\r\n";
        javaStatement += "\t}\r\n";
        // 写update方法
        javaStatement += "\t@Override\r\n";
        javaStatement += "\tpublic void updateBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+entityBeanName + " "+StringUtil.firstLittle2LowCase(entityBeanName)+"){\r\n";
        javaStatement += "\t\t"+daoBeanName+"."+MybatisUtil.generateUpdateMethodName(tableDefinition)+"("+StringUtil.firstLittle2LowCase(tableDefinition.getTableName())+");\r\n";
        javaStatement += "\t}\r\n";
        // 写delete方法
        javaStatement += "\t@Override\r\n";
        javaStatement += "\tpublic void deleteBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumns)+"){\r\n";
        javaStatement += "\t\t"+daoBeanName+".deleteBy"+MybatisUtil.generateFunctionLastNameByPrimaryColumn(primaryColumns)+"("+MybatisUtil.generateFunctionParameter(primaryColumns)+");\r\n";
        javaStatement += "\t}\r\n";
        // 写查询方法
        javaStatement += "\t@Override\r\n";
        javaStatement += "\tpublic "+MybatisUtil.generateEntityBeanName(tableDefinition)+" "+MybatisUtil.generateMethodNameByPrimaryKey(primaryColumns)+"("+MybatisUtil.generateFunctionParameterByPrimaryColumn(primaryColumns)+"){\r\n";
        javaStatement += "\t\treturn "+daoBeanName+"."+MybatisUtil.generateMethodNameByPrimaryKey(primaryColumns)+"("+MybatisUtil.generateFunctionParameter(primaryColumns)+");\r\n";
        javaStatement += "\t}\r\n";
        // 写查询所有记录方法
        javaStatement += "\t@Override\r\n";
        javaStatement += "\tpublic List<"+MybatisUtil.generateEntityBeanName(tableDefinition)+"> "+MybatisUtil.generateQueryAllMethodName(tableDefinition)+"(){\r\n";
        javaStatement += "\t\treturn "+daoBeanName+"."+MybatisUtil.generateQueryAllMethodName(tableDefinition)+"();\r\n";
        javaStatement += "\t}\r\n";
        javaStatement += "}";

        bw.write(javaStatement);
    }

    public void generateService(List<DBTableDefinition> tableDefinitionList){
        // 得到包路径
        String packagePath = WDKConfig.projectDir+ WDKConfig.codeDir+ (WDKConfig.basePackageName+servicePackageName).replaceAll("\\.","/");
        System.out.println(packagePath);
        // 得到实现类的包路径
        String implPackagePath = packagePath+"impls/";
        // 得到包名
        String packageFullName = StringUtil.dropLastChar(WDKConfig.basePackageName+servicePackageName);
        // 得到实现类的包名
        String implPackageFullName = packageFullName+".impls";
        // 生成接口
        for (DBTableDefinition tableDefinition :
                tableDefinitionList) {
            // 类名
            String className = StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+serviceSuffix;
            // 生成接口文件
            File serviceClassFile = FileUtil.createFileWithString(packagePath,className+".java");
            // 得到写入器
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(serviceClassFile));
                // 写接口文件
                writeInterfaceFile(bw,packageFullName,tableDefinition);
                bw.close();
                // 写实现类文件
                File serviceImplClassFile = FileUtil.createFileWithString(implPackagePath,className+"Impl.java");
                bw = new BufferedWriter(new FileWriter(serviceImplClassFile));
                writeInterfaceImplFile(bw,implPackageFullName,tableDefinition);
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


}
