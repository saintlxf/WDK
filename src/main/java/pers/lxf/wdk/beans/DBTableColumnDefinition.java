package pers.lxf.wdk.beans;

/**
 * 数据库表中列的定义
 */
public class DBTableColumnDefinition {
    private String columnName;
    private String dataType;
    private int dataLength;
    private boolean primaryKey;
    private boolean notNULL;
    private boolean increment;
    private boolean beMaintain;
    private String comment;
    private String displayTxt;
    private String linkOtherTable;
    private String pageControl;

    public String getPageControl() {
        return pageControl;
    }

    public void setPageControl(String pageControl) {
        this.pageControl = pageControl;
    }

    public String getLinkOtherTable() {
        return linkOtherTable;
    }

    public void setLinkOtherTable(String linkOtherTable) {
        this.linkOtherTable = linkOtherTable;
    }

    public String getDisplayTxt() {
        return displayTxt;
    }

    public void setDisplayTxt(String displayTxt) {
        this.displayTxt = displayTxt;
    }

    public DBTableColumnDefinition(){

    }

    public DBTableColumnDefinition(String columnName, String dataType, int dataLength, boolean primaryKey, boolean notNULL, boolean incremen,String comment) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.dataLength = dataLength;
        this.primaryKey = primaryKey;
        this.notNULL = notNULL;
        this.increment = incremen;
        this.comment = comment;
    }

    public String getColumnName() {

        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isNotNULL() {
        return notNULL;
    }

    public void setNotNULL(boolean notNULL) {
        this.notNULL = notNULL;
    }

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isBeMaintain() {
        return beMaintain;
    }

    public void setBeMaintain(boolean beMaintain) {
        this.beMaintain = beMaintain;
    }
}
