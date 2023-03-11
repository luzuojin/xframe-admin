package dev.xframe.admin.view.entity;

import dev.xframe.admin.view.XSegment;

public class Segment extends Navi {
	
	protected Content content;

	public Segment(String path, XSegment xseg, Content content) {
		super(xseg.name(), path, xseg.order());
		this.content = content;
	}

    private Segment(Navi navi, Segment segment) {
        super(navi.name, navi.path, navi.order);
        this.content = segment.content;
    }

    @Override
    protected Navi duplicateBy(Navi navi) {
        return new Segment(navi, this);
    }

    public Content getContent() {
        return content;
    }
    public void setContent(Content content) {
        this.content = content;
    }
}
