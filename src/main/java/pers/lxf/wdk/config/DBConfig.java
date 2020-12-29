package pers.lxf.wdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DBConfig {
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://192.168.163.6:3306/myweb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&serverTimezone=Asia/Shanghai");
        ds.setUsername("root");
        ds.setPassword("Ab123456");

        return ds;
    }
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
