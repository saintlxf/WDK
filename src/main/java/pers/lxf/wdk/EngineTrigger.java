package pers.lxf.wdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.database.DBSchemaLoader;
import pers.lxf.wdk.mvc.*;

import java.util.List;

public class EngineTrigger implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private DBSchemaLoader dbSchemaLoader;
    @Autowired
    private MybatisGenerator mybatisGenerator;
    @Autowired
    private ServiceGenerator serviceGenerator;
    @Autowired
    private ControllerGenerator controllerGenerator;
    @Autowired
    private MVCGenerateor mvcGenerateor;
    @Autowired
    private HtmlGenerator htmlGenerator;
    @Autowired
    private AppGenerator appGenerator;

    //private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("EngineTrigger start......");
        // 加载schema
        List<DBTableDefinition> tableDefinitionList = dbSchemaLoader.loadSchema();

        if(tableDefinitionList!=null&&tableDefinitionList.size()>0) {
            // 生成启动运行类
            appGenerator.generateApplicationRunner(tableDefinitionList);
            // 生成mapper
            mybatisGenerator.setTableDefinitionList(tableDefinitionList);
            mybatisGenerator.generateMapperAndInterface();
            // 生成service
            serviceGenerator.generateService(tableDefinitionList);
            // 生成controller
            controllerGenerator.generateController(tableDefinitionList);
            // 生成其他mvc类
            mvcGenerateor.generateMVCClass(tableDefinitionList);
            // 生成页面
            htmlGenerator.generateHtml(tableDefinitionList);
        }
    }
}
