package pers.lxf.wdk.beans;

public class DBTableOpration {
    public static int OPRATION_TYPE_CREATE = 1;
    public static int OPRATION_TYPE_MODIFY = 2;
    public static int OPRATION_TYPE_DROP = 3;
    private int oprationType;
    private String oprationSql;

    public DBTableOpration(int oprationType, String oprationSql) {
        this.oprationType = oprationType;
        this.oprationSql = oprationSql;
    }

    public int getOprationType() {

        return oprationType;
    }

    public void setOprationType(int oprationType) {
        this.oprationType = oprationType;
    }

    public String getOprationSql() {
        return oprationSql;
    }

    public void setOprationSql(String oprationSql) {
        this.oprationSql = oprationSql;
    }
}
