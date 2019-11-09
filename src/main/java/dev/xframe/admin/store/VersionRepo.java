package dev.xframe.admin.store;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.xframe.inject.Loadable;
import dev.xframe.inject.Repository;
import dev.xframe.jdbc.JdbcEnviron;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.TypeQuery;

@Repository
public class VersionRepo implements Loadable {
	
	Map<StoreKey, TypeQuery<Version>> queries;
	
	Map<StoreKey, JdbcTemplate> jdbcs;

	@Override
	public void load() {
		queries = new EnumMap<>(StoreKey.class);
		Arrays.stream(StoreKey.values()).forEach(key->{
			queries.put(key,
					TypeQuery.newBuilder(Version.class).setTable(key, "T_VERSION").build());
			
			jdbcs.put(key, JdbcEnviron.getJdbcTemplate(key));
		});
	}
	
	public List<Version> fetch(StoreKey key) {
		return queries.get(key).fetchAll();
	}
	
	public void runScript(StoreKey key, String script) {
		jdbcs.get(key).execute(script);
	}
	
}
