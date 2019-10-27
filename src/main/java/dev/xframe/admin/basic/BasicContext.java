package dev.xframe.admin.basic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.xframe.admin.system.privilege.Privilege;
import dev.xframe.admin.view.Chapter;
import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.Summary;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.rest.ArgParsers;
import dev.xframe.injection.Bean;
import dev.xframe.injection.Eventual;
import dev.xframe.injection.Loadable;
import dev.xframe.injection.code.Codes;
import dev.xframe.utils.XStrings;


@Bean
public class BasicContext implements Loadable, Eventual {
	
	private Summary summary;

	private List<Privilege> privileges = new ArrayList<>();
	
	private Map<String, String> privilegeDesc = new HashMap<>();
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public void load() {
		summary = new Summary();
		summary.setName("xframe-admin");
		summary.setIcon("");
		
		ArgParsers.offer(Timestamp.class, s->XStrings.isEmpty(s) ? null : Timestamp.valueOf(LocalDate.parse(s, formatter).atTime(0, 0)));
	}

	@Override
	public void eventuate() {
		Map<String, Chapter> chapters = new LinkedHashMap<>();
		for (Class<?> clazz : Codes.getDeclaredClasses()) {
			if(clazz.isAnnotationPresent(XChapter.class)) {
				XChapter chapter = clazz.getAnnotation(XChapter.class);
				chapters.put(chapter.path(), new Chapter(chapter.name(), chapter.path()));
			}
		}
		for (Class<?> clazz : Codes.getDeclaredClasses()) {
			if(clazz.isAnnotationPresent(XSegment.class)) {
				XSegment xseg = clazz.getAnnotation(XSegment.class);
				String[] pathes = Service.findPath(clazz).split("/");
				Chapter chapter = chapters.get(pathes[0]);
				Segment segment = new Segment(xseg.name(), pathes[1]);
				parseColumns(segment.getColumns(), xseg.model());
				parseOptions(segment.getOptions(), clazz);
				chapter.getSegments().add(segment);
			}
		}
		summary.setChapters(chapters.values().stream().collect(Collectors.toList()));
		
		summary.getEnums().put("testenum", Arrays.asList(
				new VEnum("luzj"),
				new VEnum("xframe"),
				new VEnum("admin")
				));
		
		privileges = new ArrayList<>();
		summary.getChapters().forEach(c->{
			addPrivilege(new Privilege(c.getName(), c.getPath()));
			for (Segment seg : c.getSegments()) {
				addPrivilege(new Privilege(seg.getName(), c.getPath() + "/" + seg.getPath()));
			}
		});
		
		summary.getEnums().put("privileges", privileges.stream().map(p->new VEnum(p.getPath(), p.getName())).collect(Collectors.toList()));
	}
	
	void addPrivilege(Privilege p) {
		privileges.add(p);
		privilegeDesc.put(p.getPath(), p.getName());
	}
	
	String orElse(String src, String val) {
		return XStrings.isEmpty(src) ? val : src;
	}
	
	void parseOptions(List<Option> options, Class<?> declaring) {
		Method[] methods = declaring.getDeclaredMethods();
		for (Method method : methods) {
			if("edit".equals(method.getName())) {
				options.add(Option.edit);
			} else if("delete".equals(method.getName())) {
				options.add(Option.del);
			} else if("add".equals(method.getName())) {
				options.add(Option.add);
			} else if("query".equals(method.getName())) {
				Option qur = Option.qur();
				Parameter[] params = method.getParameters();
				for (Parameter p : params) {
					XColumn xi = p.getAnnotation(XColumn.class);
					if(xi != null) {
						qur.getInputs().add(new Column(p.getName(), orElse(xi.value(), p.getName()), xi.type(), xi.enumKey()));
					}
				}
				options.add(qur);
			}
		}
		Collections.sort(options);
	}

	void parseColumns(List<Column> columns, Class<?> model) {
		Field[] fields = model.getDeclaredFields();
		for (Field field : fields) {
			XColumn xf = field.getAnnotation(XColumn.class);
			String name = field.getName();
			if(xf == null) {
				columns.add(new Column(name, name, XColumn.type_text, "", XColumn.full));
			} else if(xf.show() > 0) {
				int xtype = xf.type();
				if(xtype == 0 && (field.getType() == boolean.class || field.getType() == Boolean.class))
					xtype = XColumn.type_bool;
				columns.add(new Column(name, orElse(xf.value(), name), xtype, xf.enumKey(), xf.show()));
			}
		}
	}
	
	public void setEnums(String key, List<VEnum> data) {
		summary.getEnums().put(key, data);
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public Map<String, String> getPrivilegeDesc() {
		return privilegeDesc;
	}

	public List<Chapter> getChapters() {
		return summary.getChapters();
	}

	public Summary getSummary() {
		return summary;
	}

}
