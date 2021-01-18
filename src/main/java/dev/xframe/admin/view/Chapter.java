package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Chapter extends Navi implements Comparable<Chapter> {
	
	private int order;
	
	private Navigable navigable = Navigable.NIL;
	private List<Segment> segments = new ArrayList<>();
	private List<Navi> navis;
	
	public Chapter(XChapter xc) {
		this(xc.name(), xc.path(), xc.order());
	}
	
	public Chapter(String name, String path, int order) {
		super(name, path);
		this.order = order;
	}
	
	public Chapter(XChapter xc, Navigable navigable) {
		this(xc);
		this.navigable = navigable;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	public List<Navi> getNavis() {
		return navis;
	}

	public void setNavis(List<Navi> navis) {
		this.navis = navis;
	}

	public Chapter copyBy(String path, Predicate<String> predicate) {
        Chapter c = new Chapter(name, path, order);
        if(navigable != Navigable.NIL) {
        	c.navis = navigable.get();
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

	public void fix(Navigable navigable) {
		this.navigable = navigable;
	}

}
