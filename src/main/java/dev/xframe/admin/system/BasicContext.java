package dev.xframe.admin.system;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.view.Chapter;
import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.Padding;
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.Summary;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.rest.ArgParsers;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.beans.BeanHelper;
import dev.xframe.inject.code.Codes;
import dev.xframe.utils.XCaught;
import dev.xframe.utils.XStrings;


@Bean
public class BasicContext implements Loadable {
    
	private Summary summary;

	private DateTimeFormatter dataTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private Map<String, Object> enumValues = new HashMap<>();
	
	@Override
	public void load() {
		summary = new Summary();
		summary.setName("XframeAdmin");
		summary.setIcon("img/xframe.png");
		
		ArgParsers.offer(Timestamp.class, s->XStrings.isEmpty(s) ? null : Timestamp.valueOf(LocalDateTime.parse(s, dataTimeformatter)));
		ArgParsers.offer(LocalDateTime.class, s->XStrings.isEmpty(s) ? null : LocalDateTime.parse(s, dataTimeformatter));
		ArgParsers.offer(LocalDate.class, s->XStrings.isEmpty(s) ? null : LocalDate.parse(s, dateformatter));
		ArgParsers.offer(LocalTime.class, s->XStrings.isEmpty(s) ? null : LocalTime.parse(s, timeformatter));
		
		Map<String, Chapter> chapters = new LinkedHashMap<>();
        for (Class<?> clazz : Codes.getDeclaredClasses()) {
            if(clazz.isAnnotationPresent(XChapter.class)) {
                XChapter chapter = clazz.getAnnotation(XChapter.class);
                Chapter value = new Chapter(chapter);
				chapters.put(chapter.path(), value);
				parsePadding(clazz, value);
            }
        }
        
        for (Class<?> clazz : Codes.getDeclaredClasses()) {
            if(clazz.isAnnotationPresent(XSegment.class)) {
                XSegment xseg = clazz.getAnnotation(XSegment.class);
                String[] pathes = Service.findPath(clazz).split("/");
                Chapter chapter = chapters.get(pathes[0]);//第一个为chapter.path
                //中间若有为flexable path, 动态列表栏
                String segPath = pathes[pathes.length-1];//最后一个为segment.path
				Segment segment = new Segment(xseg.name(), segPath, parseDetail(xseg, clazz));
                chapter.getSegments().add(segment);
            }
        }
        
        summary.setChapters(chapters.values().stream().sorted().collect(Collectors.toList()));
	}

	void parsePadding(Class<?> clazz, Chapter chapter) {
		if(Padding.class.isAssignableFrom(clazz)) {
			chapter.fix((Padding) BeanHelper.inject(clazz));
		}
	}

	Detail parseDetail(XSegment xseg, Class<?> declaring) {
	    try {
            return xseg.detail().newInstance().parseFrom(xseg, declaring);
        } catch (Exception e) {
            return XCaught.throwException(e);
        }
	}
	
	public List<Chapter> getChapters() {
		return summary.getChapters();
	}

	public Summary getSummary() {
	    return summary;
	}
	
	public Summary getSummary(UserPrivileges privileges) {
		return summary.copyBy(privileges);
	}
	
	@SuppressWarnings("unchecked")
    public List<VEnum> getEnumValue(String username, String key) {
	    Object func = enumValues.get(key);
	    if(func instanceof Supplier) {
	        return ((Supplier<List<VEnum>>) func).get();
	    }
        return ((Function<String, List<VEnum>>)func).apply(username);
	}
	
	public void registEnumValue(String key, Supplier<List<VEnum>> func) {
	    enumValues.put(key, func);
	}
	/**
	 * supply by username
	 */
	public void registEnumValue(String key, Function<String, List<VEnum>> func) {
	    enumValues.put(key, func);
	}

}
