package pers.lxf.wdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.lxf.wdk.EngineTrigger;
import pers.lxf.wdk.beans.DatabaseCreator;
import pers.lxf.wdk.database.DBSchemaLoader;
import pers.lxf.wdk.mvc.*;

@Configuration
public class WDKConfig {
    //todo: 这里需要变成配置
    public static String basePackageName ="com.hongtao.wdktest.";
    public static String daoPackageName = "dao.";
    public static String entityBeanPackageName = "pojo.entity.";
    public static String viewBeanPackageName = "pojo.viewBeans.";
    public static String daoStringName = "Dao";
    public static String projectDir = "d:/Idea/study/wdktest/";
    public static String resourceDir = "src/main/resources/";
    public static String codeDir = "src/main/java/";
    public static String entityBeanStringName = "";

    @Bean
    public DatabaseCreator databaseCreator(){
        return new DatabaseCreator();
    }
    @Bean
    public DBSchemaLoader dbSchemaLoader(){
        return new DBSchemaLoader();
    }
    @Bean
    public MybatisGenerator mybatisConfigure(){
        return new MybatisGenerator();
    }
    @Bean
    public ServiceGenerator serviceGenerator(){
        return new ServiceGenerator();
    }
    @Bean
    public ControllerGenerator controllerGenerator(){
        return new ControllerGenerator();
    }
    @Bean
    public MVCGenerateor mvcGenerateor(){
        return new MVCGenerateor();
    }
    @Bean
    public HtmlGenerator htmlGenerator(){
        return new HtmlGenerator();
    }
    @Bean
    public AppGenerator appGenerator(){
        return new AppGenerator();
    }
    @Bean
    public EngineTrigger engineTrigger(){
        return new EngineTrigger();
    }
}
