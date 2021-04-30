package dev.xframe.admin.view;

public class Segment extends Navi {
	
	protected Detail detail;
	
	public Segment(String name, String path, int order, Detail detail) {
		super(name, path, order);
		this.detail = detail;
	}
	
    public Detail getDetail() {
        return detail;
    }
    public void setDetail(Detail detail) {
        this.detail = detail;
    }
    
}
