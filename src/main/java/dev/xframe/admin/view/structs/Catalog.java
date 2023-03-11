package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Catalog {
	
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
	
	public Catalog copyBy(Predicate<String> predicate) {
	    Catalog s = new Catalog();
	    s.name = this.name;
	    s.icon = this.icon;
	    for (Chapter chapter : this.chapters) {
	        if(predicate.test(chapter.getPath())) {
	            s.chapters.add(chapter.copyBy(chapter.getPath(), predicate));
	        }
        }
	    return s;
	}
	
	public void parseFrom(List<Class<?>> classes) {
		Map<String, Chapter> chapters = new LinkedHashMap<>();
        for (Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(XChapter.class)) {
				Chapter chapter = new Chapter(clazz.getAnnotation(XChapter.class));
				chapters.put(chapter.getPath(), chapter);
            }
        }
        
        for (Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(XSegment.class)) {
                XSegment xseg = clazz.getAnnotation(XSegment.class);
                String[] paths = Service.findPath(clazz).split("/");
                Navigate parent = chapters.get(paths[0]);//第一个为chapter.path
				if(parent == null) {
					throw new IllegalStateException( String.format("Chapter[%s] of Segment[%s] not found", paths[0], xseg));
				}
				if(paths.length == 3) {//三级菜单
					parent = (Navigate) Symbol.unwrap(parent.findOrAdd(paths[1], Symbol.wrap(parent.path, new Navigate(paths[1]))));
				}
				parent.getNavis().add(Symbol.wrap(parent.path, new Segment(paths[paths.length-1], xseg, parseContent(xseg, clazz))));
            }
        }
        this.setChapters(chapters.values().stream().peek(c->Collections.sort(c.getNavis())).sorted().collect(Collectors.toList()));
	}

	Content parseContent(XSegment xseg, Class<?> declaring) {
		return xseg.type().fac.get().parseFrom(xseg, declaring);
	}
	
}
