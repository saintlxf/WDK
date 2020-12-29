package pers.lxf.wdk.beans;

import java.util.List;

/**
 * 表结构定义
 */
public class DBTableDefinition {
    private String tableName; // 表名
    private String engine; // 引擎
    private String defaultCharset; // 默认字符集
    private boolean maintainPage; // 是否生成维护页面
    private boolean dictionary; // 是否是字典表

    public boolean isDictionary() {
        return dictionary;
    }

    public void setDictionary(boolean dictionary) {
        this.dictionary = dictionary;
    }

    private List<DBTableColumnDefinition> columns; // 列定义

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getDefaultCharset() {
        return defaultCharset;
    }

    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public boolean isMaintainPage() {
        return maintainPage;
    }

    public void setMaintainPage(boolean maintainPage) {
        this.maintainPage = maintainPage;
    }

    public List<DBTableColumnDefinition> getColumns() {
        return columns;
    }

    public void setColumns(List<DBTableColumnDefinition> columns) {
        this.columns = columns;
    }
}
