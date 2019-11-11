package dev.xframe.admin.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.xframe.admin.conf.WebFileHandler;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.jdbc.JdbcEnviron;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.TypeQuery;
import dev.xframe.jdbc.tools.SQLScript;
import dev.xframe.utils.XStrings;

@Configurator
public class VersionContext implements Loadable  {
	
	@Inject
	StoreConfigurator _sc;//for dependence
	
	Map<StoreKey, TypeQuery<Version>> queries = new EnumMap<>(StoreKey.class);
	
	Map<StoreKey, JdbcTemplate> jdbcs = new EnumMap<>(StoreKey.class);

	@Override
	public void load() {
		Arrays.stream(StoreKey.values()).forEach(key->{
			queries.put(key,
					TypeQuery.newBuilder(Version.class).setTable(key, "T_VERSION").build());
			
			jdbcs.put(key, JdbcEnviron.getJdbcTemplate(key));
		});
	}
	
	List<Version> fetch(StoreKey key) {
		return queries.get(key).fetchAll();
	}
	
	void runScript(StoreKey key, String script) {
		jdbcs.get(key).execute(script);
	}
	
	
	public void update(StoreKey key, Version version) {
	    int v = fetch(key).stream().mapToInt(Version::getVersion).max().orElse(-1);
	    if(v == -1) {
	    	throw new IllegalArgumentException("History versions error");
	    }
	    
	    if(version.getVersion() > v) {
	    	readScripts(version.getSqlPath()).forEach(script->runScript(key, script));
	    }
	}

	List<String> readScripts(String path) {
		try {
            InputStream in = WebFileHandler.class.getClassLoader().getResourceAsStream(path);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            return SQLScript.parse(XStrings.newStringUtf8(bytes));
        } catch (IOException e) {
        	throw new IllegalArgumentException(e);
        }
	}

}