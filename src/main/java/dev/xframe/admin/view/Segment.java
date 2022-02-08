package dev.xframe.admin.view;

public class Segment extends Navi {
	
	protected Detail detail;

	public Segment(String path, XSegment xseg, Detail detail) {
		super(xseg.name(), path, xseg.order());
		this.detail = detail;
	}

    private Segment(Navi navi, Segment segment) {
        super(navi.name, navi.path, navi.order);
        this.detail = segment.detail;
    }

    @Override
    protected Navi duplicateBy(Navi navi) {
        return new Segment(navi, this);
    }

    public Detail getDetail() {
        return detail;
    }
    public void setDetail(Detail detail) {
        this.detail = detail;
    }
}
