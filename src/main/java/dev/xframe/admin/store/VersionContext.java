package dev.xframe.admin.store;

import dev.xframe.inject.Bean;
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

@Bean
public class VersionContext implements Loadable  {
	
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
	    int v = fetch(key).stream().mapToInt(Version::getVersion).max().orElse(-1);
	    if(v == -1) {
	    	throw new IllegalArgumentException("History versions error");
	    }
	    XLogger.info("Current {} version: {}", key, Version.toStr(v));
	    if(version.getVersion() > v) {
			readScripts(version.getSqlPath()).forEach(script->runScript(key, script));
	    	addVersion(key, version);
	    	XLogger.info("Updated version: {}", Version.toStr(version.getVersion()));
	    }
	}

}
