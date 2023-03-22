package dev.xframe.admin.system;

import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.structs.Catalog;
import dev.xframe.admin.view.structs.Chapter;
import dev.xframe.admin.view.structs.Navi;
import dev.xframe.admin.view.values.VEnum;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.code.Clazz;
import dev.xframe.inject.code.Codes;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


@Bean
public class BasicManager implements Loadable, XRegistrator {
    
	private Catalog catalog;

	private Map<String, Supplier<List<VEnum>>> enumValues = new HashMap<>();
	private Map<String, Supplier<List<Navi>>>  naviValues = new HashMap<>();

	private LinkedHashSet<String> extensions = new LinkedHashSet<>();

	@Override
	public void load() {
		catalog = new Catalog();
		catalog.setName("XFrameAdmin");
		catalog.setIcon("img/xframe.png");
		
		catalog.parseFrom(Codes.getScannedClasses(Clazz.filter(XChapter.class, XSegment.class)));
	}
	
	public List<Chapter> getChapters() {
		return catalog.getChapters();
	}

	public Catalog getCatalog() {
	    return catalog;
	}
	
	public Catalog getCatalog(UserPrivileges privileges) {
		return catalog.copyBy(privileges);
	}
	
    public List<VEnum> getEnumValue(String key) {
	    return enumValues.get(key).get();
	}
	public void registEnumValue(String key, Supplier<List<VEnum>> func) {
	    enumValues.put(key, func);
	}

	public void registNaviValue(String key, Supplier<List<Navi>> func) {
		naviValues.put(key, func);
	}
	public List<Navi> getNaviValue(String key) {
		return naviValues.get(key).get();
	}

	@Override
	public void registExtension(String extensionJsFile) {
		this.extensions.add(extensionJsFile);
	}
	public Collection<String> getExtensions() {
		return extensions;
	}
}
