package dev.xframe.admin.system.privilege;

import dev.xframe.admin.view.XColumn;

public class Privilege {
	
	@XColumn("模块名")
	private String name;
	@XColumn("访问路径")
	private String path;
	
	public Privilege(String name, String path) {
		this.name = name;
		this.path = path;
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
	
}
