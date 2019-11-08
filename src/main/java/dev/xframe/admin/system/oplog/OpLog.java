package dev.xframe.admin.system.oplog;

import java.sql.Timestamp;

public class OpLog {
	
	private String name;//username
	private String path;
	private String params;
	private String opHost;
	private String opMethod;
	private Timestamp opTime;
	
	public OpLog() {
    }
	
    public OpLog(String name, String path, String params, String host, String method) {
        this.name = name;
        this.path = path;
        this.params = params;
        this.opHost = host;
        this.opMethod = method;
        this.opTime = new Timestamp(System.currentTimeMillis());
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public String getOpHost() {
        return opHost;
    }
    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }
    public String getOpMethod() {
        return opMethod;
    }
    public void setOpMethod(String opMethod) {
        this.opMethod = opMethod;
    }
    public Timestamp getOpTime() {
        return opTime;
    }
    public void setOpTime(Timestamp opTime) {
        this.opTime = opTime;
    }

}
