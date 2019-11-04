package dev.xframe.admin.store;

import static dev.xframe.admin.store.StoreKey.DAT;
import static dev.xframe.admin.store.StoreKey.LOG;

import java.io.File;

import dev.xframe.injection.ApplicationContext;
import dev.xframe.injection.Configurator;
import dev.xframe.injection.Loadable;
import dev.xframe.jdbc.JdbcEnviron;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.datasource.DBSource;
import dev.xframe.jdbc.datasource.DataSources;

@Configurator
public class StoreConfigurator implements Loadable {
	
	@Override
	public void load() {
		JdbcEnviron
			.getConfigurator()
			.setInstupUsage(false, false)
			.setDatasource(DAT, DataSources.tomcatJdbc(getDBSource(DAT)))
			.setDatasource(LOG, DataSources.tomcatJdbc(getDBSource(LOG)));
		
		ApplicationContext.registBean(JdbcTemplate.class, JdbcEnviron.getJdbcTemplate(StoreKey.DAT));
	}

	private DBSource getDBSource(StoreKey key) {
		String dbpath = dbPath(key);
		String script = dbExists(key) ? "" : String.format("INIT=RUNSCRIPT FROM 'classpath:%s';", key.script);;
		String driver = "org.h2.Driver";
		String dburl = String.format("jdbc:h2:%s;%sDB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE;", dbpath, script);
		String user = "embed";
		String pass = "embed";
		return new DBSource(user, pass, driver, dburl, 1, 1);
	}

	private String dbPath(StoreKey key) {
		return new File(getDbDir(), "xadmin_" + key.name().toLowerCase()).getAbsolutePath();
	}

	private boolean dbExists(StoreKey key) {
		return new File(dbPath(key) + ".mv.db").exists();
	}

	private String getDbDir() {
		return System.getProperty("store.dir", System.getProperty("user.dir"));
	}
	
}
