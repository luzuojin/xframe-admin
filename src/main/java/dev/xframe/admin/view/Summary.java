package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.admin.system.auth.UserPrivileges;

public class Summary {
	
	private String name;
	private String icon;
	
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
	
	public Summary copyBy(UserPrivileges privileges) {
	    Summary s = new Summary();
	    s.name = this.name;
	    s.icon = this.icon;
	    for (Chapter chapter : this.chapters) {
	        if(privileges.contains(chapter.getPath())) {
	            s.chapters.add(chapter.copyBy(chapter.getPath(), privileges));
	        }
        }
	    return s;
	}
	
}
