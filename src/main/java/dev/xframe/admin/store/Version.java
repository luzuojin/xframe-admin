package dev.xframe.admin.store;

import dev.xframe.utils.XReflection;

import java.sql.Timestamp;
import java.util.regex.Pattern;

public class Version {

    private String component;
    /**
     * format: 0.0
     */
    private int version;
    
    private Timestamp upTime;
    
    private String sqlPath;
    
    public Version() {
    }
    
    public Version(String version, String sqlPath) {
        this.component = XReflection.getCallerClass().getName();
        this.version = toInt(version);
        this.sqlPath = sqlPath;
        this.upTime = new Timestamp(System.currentTimeMillis());
    }

    public String getComponent() {
        return component;
    }
    public void setComponent(String component) {
        this.component = component;
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

    public static String toStr(int version) {
    	return (version / 1000) + "." + (version % 1000);
    }
    public static int toInt(String version) {
    	String[] args = version.split(Pattern.quote("."));
    	return Integer.parseInt(args[0]) * 1000 + Integer.parseInt(args[1]);
    }

}
