package dev.xframe.admin.system;

import java.sql.Timestamp;
import java.time.LocalDate;
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
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.Summary;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.rest.ArgParsers;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.code.Codes;
import dev.xframe.utils.XCaught;
import dev.xframe.utils.XStrings;


@Bean
public class BasicContext implements Loadable {
    
	private Summary summary;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private Map<String, Object> enumValues = new HashMap<>();
	
	@Override
	public void load() {
		summary = new Summary();
		summary.setName("xframe-admin");
		summary.setIcon("");
		
		ArgParsers.offer(Timestamp.class, s->XStrings.isEmpty(s) ? null : Timestamp.valueOf(LocalDate.parse(s, formatter).atTime(0, 0)));
		
		Map<String, Chapter> chapters = new LinkedHashMap<>();
        for (Class<?> clazz : Codes.getDeclaredClasses()) {
            if(clazz.isAnnotationPresent(XChapter.class)) {
                XChapter chapter = clazz.getAnnotation(XChapter.class);
                chapters.put(chapter.path(), new Chapter(chapter));
            }
        }
        for (Class<?> clazz : Codes.getDeclaredClasses()) {
            if(clazz.isAnnotationPresent(XSegment.class)) {
                XSegment xseg = clazz.getAnnotation(XSegment.class);
                String[] pathes = Service.findPath(clazz).split("/");
                Chapter chapter = chapters.get(pathes[0]);
                Segment segment = new Segment(xseg.name(), pathes[1], parseDetail(xseg, clazz));
                chapter.getSegments().add(segment);
            }
        }
        
        summary.setChapters(chapters.values().stream().sorted().collect(Collectors.toList()));
	}

	String orElse(String src, String val) {
		return XStrings.isEmpty(src) ? val : src;
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
