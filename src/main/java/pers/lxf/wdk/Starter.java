package pers.lxf.wdk;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pers.lxf.wdk.config.DBConfig;
import pers.lxf.wdk.config.WDKConfig;

public class Starter {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(WDKConfig.class, DBConfig.class);
        ctx.refresh();
    }
}
