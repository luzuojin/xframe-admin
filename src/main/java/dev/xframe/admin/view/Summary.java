package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Summary {
	
	private String name;
	private String icon;
	
	private Map<String, List<VEnum>> enums = new HashMap<>();
	private List<Chapter> chapters = new ArrayList<>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public Map<String, List<VEnum>> getEnums() {
		return enums;
	}

	public void setEnums(Map<String, List<VEnum>> enums) {
		this.enums = enums;
	}
	
}
