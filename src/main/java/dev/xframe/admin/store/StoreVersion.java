package dev.xframe.admin.store;

import java.sql.Timestamp;

import dev.xframe.admin.view.XColumn;

public class StoreVersion {
    
    private int version;
    private Timestamp upTime;
    @XColumn(show=0)
    private String sqlPath;
    
    public StoreVersion() {
    }
    
    public StoreVersion(int version, String sqlPath) {
        this.version = version;
        this.sqlPath = sqlPath;
        this.upTime = new Timestamp(System.currentTimeMillis());
    }
    
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public Timestamp getUpTime() {
        return upTime;
    }
    public void setUpTime(Timestamp upTime) {
        this.upTime = upTime;
    }
    public String getSqlPath() {
        return sqlPath;
    }
    public void setSqlPath(String sqlPath) {
        this.sqlPath = sqlPath;
    }

}
