package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Chapter extends Navi implements Comparable<Chapter> {
	
	private int order;
	
	private Padding padding = Padding.NIL;
	private List<Segment> segments = new ArrayList<>();
	private List<Navi> padded;
	
	public Chapter(XChapter xc) {
		this(xc.name(), xc.path(), xc.order());
	}
	
	public Chapter(String name, String path, int order) {
		super(name, path);
		this.order = order;
	}
	
	public Chapter(XChapter xc, Padding flexable) {
		this(xc);
		this.padding = flexable;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	public List<Navi> getPadded() {
		return padded;
	}

	public void setPadded(List<Navi> padded) {
		this.padded = padded;
	}

	public Chapter copyBy(String path, Predicate<String> predicate) {
        Chapter c = new Chapter(name, path, order);
        if(padding != Padding.NIL) {
        	c.padded = padding.get();
        }
        for (Segment seg : segments) {
            if(predicate.test(path + "/" + seg.getPath())) {
                c.segments.add(seg);
            }
        }
        return c;
    }

	@Override
	public int compareTo(Chapter o) {
		return Integer.compare(o.order, this.order);
	}

	public void fix(Padding padding) {
		this.padding = padding;
	}

}
