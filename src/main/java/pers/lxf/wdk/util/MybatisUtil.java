package pers.lxf.wdk.util;

import pers.lxf.wdk.beans.DBTableColumnDefinition;
import pers.lxf.wdk.beans.DBTableDefinition;
import pers.lxf.wdk.config.WDKConfig;
import pers.lxf.wdk.mvc.MybatisGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis操作相关工具
 */
public class MybatisUtil {
    public static String InsertMethodPrefix = "add";
    public static String UpdateMethodPrefix = "update";
    public static String DeleteMethodPrefix = "delete";
    public static String QueryMethodPrefix = "query";
    /**
     * 根据主键生成查询方法名
     * @param primaryColumnList 主键列表
     * @return 查询方法名
     */
    public static String generateMethodNameByPrimaryKey(List<DBTableColumnDefinition> primaryColumnList) {
        String ret = null;
        if (primaryColumnList.size() > 0) {
            ret = "findBy";
            for (int i = 0; i < primaryColumnList.size(); i++) {
                if (i == 0) {
                    ret += StringUtil.firstLittle2UpCase(primaryColumnList.get(i).getColumnName());
                } else {
                    ret += "And" + StringUtil.firstLittle2UpCase(primaryColumnList.get(i).getColumnName());
                }
            }
        }
        return ret;
    }

    /**
     * 根据主键列表生成查询where从句
     * @param primaryColumnList 主键列表
     * @return where 从句
     */
    public static String generateQueryWhereStatement(List<DBTableColumnDefinition> primaryColumnList) {
        String ret = "";
        if(primaryColumnList.size()>0){
            for (int i = 0; i < primaryColumnList.size(); i++) {
                if(i==0) {
                    ret += primaryColumnList.get(i).getColumnName() + " = #{" + primaryColumnList.get(i).getColumnName() + "}";
                }else{
                    ret += " AND "+primaryColumnList.get(i).getColumnName() + " = #{" + primaryColumnList.get(i).getColumnName() + "}";
                }
            }
        }
        return ret;
    }
    public static String generateInsertMethodName(DBTableDefinition tableDefinition){
        return InsertMethodPrefix + StringUtil.firstLittle2UpCase(tableDefinition.getTableName());
    }
    public static String generateUpdateMethodName(DBTableDefinition tableDefinition){
        return UpdateMethodPrefix + StringUtil.firstLittle2UpCase(tableDefinition.getTableName());
    }
    public static String generateDelteMethodName(DBTableDefinition tableDefinition){
        return DeleteMethodPrefix+StringUtil.firstLittle2UpCase((tableDefinition.getTableName()));
    }

    public static String generateQueryAllMethodName(DBTableDefinition tableDefinition){
        return QueryMethodPrefix+"All"+StringUtil.firstLittle2UpCase(tableDefinition.getTableName());
    }

    /**
     * 生成实体bean的名称，可以提供修改的方法，由使用者决定实体bean名称规则
     * @param tableDefinition 表定义
     * @return 实体bean名称
     */
    public static String generateEntityBeanName(DBTableDefinition tableDefinition){
        return StringUtil.firstLittle2UpCase(tableDefinition.getTableName());
    }

    /**
     * 生成实体bean的属性名
     * @param columnName 列名
     * @return 属性名
     */
    public static String generateEntityAttributeName(String columnName){
        if(columnName.contains("_")){
            return StringUtil.replaceAllDownLineAndUpCaseFirstLittle(columnName);
        }else{
            return columnName;
        }
    }

    public static String generateDaoClassName(DBTableDefinition tableDefinition){
        return StringUtil.firstLittle2UpCase(tableDefinition.getTableName())+ WDKConfig.daoStringName;
    }

    public static List<DBTableColumnDefinition> getPrimaryColumnList(DBTableDefinition tableDefinition){
        List<DBTableColumnDefinition> columnList = tableDefinition.getColumns();
        List<DBTableColumnDefinition> primaryColumnList = new ArrayList<DBTableColumnDefinition>();
        if(columnList.size()>0) {
            for (DBTableColumnDefinition column :
                    columnList) {
                if (column.isPrimaryKey()) {
                    primaryColumnList.add(column);
                }
            }
        }
        return primaryColumnList;
    }

    /**
     * 根据主键列生成函数的后半段名字
     * @param primaryColumns 主键列表
     * @return 后半段名字
     */
    public static String generateFunctionLastNameByPrimaryColumn(List<DBTableColumnDefinition> primaryColumns){
        String ret = "";
        if(primaryColumns.size()>0){
            for (int i = 0; i < primaryColumns.size(); i++) {
                if(i>0){
                    ret +="And";
                }
                ret += StringUtil.firstLittle2UpCase(primaryColumns.get(i).getColumnName());
            }
        }
        return ret;
    }
    public static String generateFunctionParameterByPrimaryColumn(List<DBTableColumnDefinition> primaryColumn){
        String ret = "";
        if(primaryColumn.size()>0){
            for (int i = 0; i < primaryColumn.size(); i++) {
                if(i>0){
                    ret +=", ";
                }
                ret += SQLKit.tableDefinitionTypeMapJavaType.get(primaryColumn.get(i).getDataType())+" "+StringUtil.firstLittle2LowCase(primaryColumn.get(i).getColumnName());
            }
        }
        return ret;
    }
    public static String generateFunctionParameter(List<DBTableColumnDefinition> primaryColumns){
        String ret = "";
        if(primaryColumns.size()>0){
            for (int i = 0; i < primaryColumns.size(); i++) {
                if(i>0){
                    ret += ", ";
                }
                ret += StringUtil.firstLittle2LowCase(primaryColumns.get(i).getColumnName());
            }
        }
        return ret;
    }

}
