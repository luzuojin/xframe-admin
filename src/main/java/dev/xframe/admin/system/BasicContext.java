package dev.xframe.admin.system;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.view.Chapter;
import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.Summary;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.rest.ArgParsers;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.code.Codes;
import dev.xframe.utils.XStrings;


@Bean
public class BasicContext implements Loadable {
    
	private Summary summary;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private Map<String, Supplier<List<VEnum>>> enumValues = new HashMap<>();
	
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
                Segment segment = new Segment(xseg.name(), pathes[1], xseg.padding());
                segment.setColumns(parseModelColumns(xseg.model()));
                parseOptions(segment, clazz, xseg.model());
                chapter.getSegments().add(segment);
            }
        }
        
        summary.setChapters(chapters.values().stream().sorted().collect(Collectors.toList()));
	}

	String orElse(String src, String val) {
		return XStrings.isEmpty(src) ? val : src;
	}
	
	void parseOptions(Segment seg, Class<?> declaring, Class<?> model) {
	    boolean listable = false;
		Method[] methods = declaring.getDeclaredMethods();
		for (Method method : methods) {
		    if(method.isAnnotationPresent(HttpMethods.GET.class)) {
		        if(XOption.listing.equals(method.getAnnotation(HttpMethods.GET.class).value())) {
                    listable = true;
                    continue;
                }
		        parseOptionTo(Option.qry, model, method, seg);
		    } else if(method.isAnnotationPresent(HttpMethods.PUT.class)) {
		        parseOptionTo(Option.edt, model, method, seg);
    		} else if(method.isAnnotationPresent(HttpMethods.DELETE.class)) {
    		    parseOptionTo(Option.del, model, method, seg);
    		} else if(method.isAnnotationPresent(HttpMethods.POST.class)) {
    		    parseOptionTo(Option.add, model, method, seg);
    		}
		}
		seg.setListable(listable);
		Collections.sort(seg.getOptions());
	}
    void parseOptionTo(Option op, Class<?> model, Method method, Segment seg) {
        seg.getOptions().add(op.copy(method.getAnnotation(XOption.class)).with(parseParamColumns(model, method)));
    }

    List<Column> parseParamColumns(Class<?> model, Method method) {
        List<Column> columns = new ArrayList<>();
        Parameter[] params = method.getParameters();
        //只有一个参数而且由HttpBody(post)解析.(edit/delete/add)
        if(params.length == 1 && params[0].isAnnotationPresent(HttpArgs.Body.class)) {
            if(!params[0].getType().equals(model)) {//与seg.model不同
                columns = parseModelColumns(params[0].getType());
            }
        } else {
            for (Parameter p : params) {
                XColumn xi = p.getAnnotation(XColumn.class);
                if(xi != null) {
                    columns.add(new Column(p.getName(), orElse(xi.value(), p.getName()), xi.type(), xi.enumKey(), xi.indep()));
                }
            }
        }
        return columns;
    }

	List<Column> parseModelColumns(Class<?> model) {
        return parseModelColumns0(model, new ArrayList<>());
	}
	List<Column> parseModelColumns0(Class<?> model, List<Column> columns) {
        if(!Object.class.equals(model.getSuperclass())) {
            parseModelColumns0(model.getSuperclass(), columns);
        }
        Field[] fields = model.getDeclaredFields();
        for (Field field : fields) {
            XColumn xf = field.getAnnotation(XColumn.class);
            String name = field.getName();
            if(xf == null) {
                columns.add(new Column(name, name, XColumn.type_text, "", XColumn.full, false));
            } else if(xf.show() > 0) {
                int xtype = xf.type();
                if(xtype == 0 && (field.getType() == boolean.class || field.getType() == Boolean.class))
                    xtype = XColumn.type_bool;
                columns.add(new Column(name, orElse(xf.value(), name), xtype, xf.enumKey(), xf.show(), xf.primary()));
            }
        }
        return columns;
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
	
	public List<VEnum> getEnumValue(String key) {
	    return enumValues.get(key).get();
	}
	
	public void registEnumValue(String key, Supplier<List<VEnum>> supplier) {
	    enumValues.put(key, supplier);
	}

}
