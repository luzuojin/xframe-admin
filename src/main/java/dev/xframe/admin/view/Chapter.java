package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.admin.system.privilege.Privileges;

public class Chapter implements Comparable<Chapter> {
	
	private String name;
	private String path;
	
	private int order;
	
	private List<Segment> segments = new ArrayList<>();
	
	public Chapter() {
	}

	public Chapter(XChapter xc) {
		this.name = xc.name();
		this.path = xc.path();
		this.order= xc.order();
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

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

    public Chapter copyBy(String path, Privileges privileges) {
        Chapter c = new Chapter();
        c.name = this.name;
        c.path = this.path;
        c.order = this.order;
        for (Segment seg : segments) {
            if(privileges.contains(path + "/" + seg.getPath())) {
                c.segments.add(seg);
            }
        }
        return c;
    }

	@Override
	public int compareTo(Chapter o) {
		return Integer.compare(o.order, this.order);
	}

}
