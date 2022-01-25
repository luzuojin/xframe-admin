package dev.xframe.admin.view;

public class Segment extends Navi {
	
	protected Detail detail;

    protected boolean canSort;
	
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

    public void setCanSort(boolean canSort) {
        this.canSort = canSort;
    }

    public boolean isCanSort() {
        return canSort;
    }
}
