package dev.xframe.admin.view;

import dev.xframe.admin.view.details.Markd;
import dev.xframe.admin.view.details.Panel;
import dev.xframe.admin.view.details.Table;
import dev.xframe.http.service.Service;
import dev.xframe.inject.beans.BeanHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dev.xframe.admin.view.details.Table;
import dev.xframe.http.service.Service;
import dev.xframe.inject.beans.BeanHelper;
import dev.xframe.utils.XCaught;

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
	
	public Summary copyBy(Predicate<String> predicate) {
	    Summary s = new Summary();
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
                XChapter chapter = clazz.getAnnotation(XChapter.class);
                Chapter value = new Chapter(chapter);
				chapters.put(chapter.path(), value);
				parseNavigable(clazz, value);
            }
        }
        
        for (Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(XSegment.class)) {
                XSegment xseg = clazz.getAnnotation(XSegment.class);
                String[] pathes = Service.findPath(clazz).split("/");
                Chapter chapter = chapters.get(pathes[0]);//第一个为chapter.path
                //中间若有为flexable path, 动态列表栏
                String segPath = pathes[pathes.length-1];//最后一个为segment.path
				Segment segment = new Segment(xseg.name(), segPath, xseg.order(), parseDetail(xseg, clazz));
				if(xseg.detail() == Table.class){
					segment.setCanSort(xseg.canSort());
				}
                chapter.getSegments().add(segment);
            }
        }
        
        this.setChapters(chapters.values().stream().peek(c->Collections.sort(c.getSegments())).sorted().collect(Collectors.toList()));
	}

	void parseNavigable(Class<?> clazz, Chapter chapter) {
		if(Navigable.class.isAssignableFrom(clazz)) {
			chapter.fix((Navigable) BeanHelper.inject(clazz));
		}
	}

	Detail parseDetail(XSegment xseg, Class<?> declaring) {
		int type = xseg.type();
		if(type == XSegment.type_table) {
			if(xseg.detail() == Panel.class) type = XSegment.type_panel;
			if(xseg.detail() == Markd.class) type = XSegment.type_markd;
		}
		Detail detail;
		switch (type) {
			case XSegment.type_panel:
				detail = new Panel();
				break;
			case XSegment.type_markd:
				detail = new Markd();
				break;
			default:
				detail = new Table();
				break;
		}
		return detail.parseFrom(xseg, declaring);
	}
	
}
