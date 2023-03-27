package dev.xframe.admin.store;

import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.jdbc.JdbcEnviron;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.TypeQuery;
import dev.xframe.utils.XLogger;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static dev.xframe.admin.store.StoreConfigurator.readScripts;

@Configurator
public class VersionManager implements Loadable  {
	
	@Inject
	StoreConfigurator _sc;//for dependence
	
	Map<StoreKey, TypeQuery<Version>> queries = new EnumMap<>(StoreKey.class);
	
	Map<StoreKey, JdbcTemplate> jdbcs = new EnumMap<>(StoreKey.class);

	@Override
	public void load() {
		Arrays.stream(StoreKey.values()).forEach(key->{
            queries.put(key, TypeQuery.newBuilder(Version.class).setTable(key, key.vTable).build());
            
			jdbcs.put(key, JdbcEnviron.getJdbcTemplate(key));
		});
	}
	
	List<Version> fetch(StoreKey key) {
		return queries.get(key).fetchAll();
	}
	
	void addVersion(StoreKey key, Version version) {
	    queries.get(key).insert(version);
	}
	
	void runScript(StoreKey key, String script) {
		jdbcs.get(key).execute(script);
	}
	
	public void update(StoreKey key, Version version) {
	    int intv = fetch(key).stream().filter(v->version.getComponent().equals(v.getComponent())).mapToInt(Version::getVersion).max().orElse(0);
	    if(version.getVersion() > intv) {
			readScripts(version.getSqlPath()).forEach(script->runScript(key, script));
	    	addVersion(key, version);
	    	XLogger.info("Updated [{}] version {} to {}", version.getComponent(), Version.toStr(intv), Version.toStr(version.getVersion()));
	    }
	}

}
