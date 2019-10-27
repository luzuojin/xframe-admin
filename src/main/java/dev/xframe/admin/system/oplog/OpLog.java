package dev.xframe.admin.system.oplog;

import java.sql.Timestamp;

public class OpLog {
	
	private long id;//autoincrement
	private String name;//username
	private String path;
	private String params;
	private Timestamp opTime;

}
