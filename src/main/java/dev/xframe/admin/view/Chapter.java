package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.admin.system.privilege.Privileges;

public class Chapter {
	
	private String name;
	private String path;
	
	private List<Segment> segments = new ArrayList<>();
	
	public Chapter() {
	}

	public Chapter(String name, String path) {
		this.name = name;
		this.path = path;
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
        for (Segment seg : segments) {
            if(privileges.contains(path + "/" + seg.getPath())) {
                c.segments.add(seg);
            }
        }
        return c;
    }

}
