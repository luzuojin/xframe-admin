package dev.xframe.admin.system;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.view.Chapter;
import dev.xframe.admin.view.Summary;
import dev.xframe.admin.view.VEnum;
import dev.xframe.http.service.rest.ArgParsers;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.code.Codes;
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
		
		ArgParsers.offer(Timestamp.class, s->XStrings.isEmpty(s) ? null : Timestamp.valueOf(LocalDateTime.parse(s, dataTimeformatter)));
		ArgParsers.offer(LocalDateTime.class, s->XStrings.isEmpty(s) ? null : LocalDateTime.parse(s, dataTimeformatter));
		ArgParsers.offer(LocalDate.class, s->XStrings.isEmpty(s) ? null : LocalDate.parse(s, dateformatter));
		ArgParsers.offer(LocalTime.class, s->XStrings.isEmpty(s) ? null : LocalTime.parse(s, timeformatter));
		
		summary = new Summary();
		summary.setName("XFrameAdmin");
		summary.setIcon("img/xframe.png");
		
		summary.parseFrom(Codes.getDeclaredClasses());
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
