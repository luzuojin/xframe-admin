package dev.xframe.admin.view;

public class Segment extends Navi {
	
	protected Detail detail;
	
	public Segment(String name, String path) {
		super(name, path);
	}
	public Segment(String name, String path, Detail detail) {
		this(name, path);
		this.detail = detail;
	}
	
    public Detail getDetail() {
        return detail;
    }
    public void setDetail(Detail detail) {
        this.detail = detail;
    }
    
}
