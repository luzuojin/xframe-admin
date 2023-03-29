package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Catalog {
	
	private List<Chapter> chapters = new ArrayList<>();
	
	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public Catalog makeOrdered() {
		this.chapters.forEach(Chapter::makeOrdered);
		Collections.sort(this.chapters);
		return this;
	}

	public Catalog duplicate() {
		return new Catalog();
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
					parent = (Navigate) Wrapper.unwrap(parent.findOrAdd(paths[1], Wrapper.wrap(parent.path, new Navigate(paths[1]))));
				}
				parent.getNavis().add(Wrapper.wrap(parent.path, new Segment(paths[paths.length-1], xseg, parseContent(xseg, clazz))));
            }
        }
        this.setChapters(new ArrayList<>(chapters.values()));
	}

	Content parseContent(XSegment xseg, Class<?> declaring) {
		return EContent.Factory.newInstance(xseg.type()).parseFrom(xseg, declaring);
	}
	
}
