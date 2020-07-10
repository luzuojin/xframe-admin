package dev.xframe.admin.view;

public class Segment {
	
	private String name;
	private String path;
	private Detail detail;
	
	public Segment(String name, String path, Detail detail) {
		this.name = name;
		this.path = path;
		this.detail = detail;
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
    public Detail getDetail() {
        return detail;
    }
    public void setDetail(Detail detail) {
        this.detail = detail;
    }
	
}
