package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

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

}
