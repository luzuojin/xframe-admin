package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.XSegment;

public class Segment extends Navi {
	
	protected Content content;

	public Segment(String path, XSegment xseg, Content content) {
		this(path, xseg.name(), xseg.order(), content);
	}
    public Segment(String path, String name, int order, Content content) {
        super(path, name, order);
        this.content = content;
    }

    @Override
    protected Navi duplicateBy(Navi navi) {
        return new Segment(navi.name, navi.path, navi.order, this.content);
    }

    public Content getContent() {
        return content;
    }
    public void setContent(Content content) {
        this.content = content;
    }
}
