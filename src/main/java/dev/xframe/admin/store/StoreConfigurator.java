package dev.xframe.admin.store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import dev.xframe.admin.conf.SysProperties;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.Providable;
import dev.xframe.jdbc.JdbcEnviron;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.datasource.DBSource;
import dev.xframe.jdbc.datasource.DataSources;
import dev.xframe.jdbc.tools.SQLScript;
import dev.xframe.utils.XStrings;

@Configurator
@Providable//可以替换 
public class StoreConfigurator implements Loadable {
	
	@Override
	public void load() {
		JdbcEnviron.getConfigurator().setUpsertUsage(false, false);
		
		for (StoreKey storeKey : StoreKey.values()) {
            JdbcEnviron.getConfigurator().setDatasource(storeKey, DataSources.tomcatJdbc(getDBSource(storeKey)));
		    if(!isInitialized(storeKey)) initialDatabase(storeKey);
        }
	}

    protected void initialDatabase(StoreKey storeKey) {
        JdbcTemplate jdbc = JdbcEnviron.getJdbcTemplate(storeKey);
        List<String> scrtips = SQLScript.parse(XStrings.newStringUtf8(readBytes(storeKey.script)));
        for (String script : scrtips) {
            jdbc.execute(script);
        }
    }
	
	protected DBSource getDBSource(StoreKey key) {
		String dbpath = dbPath(key);
		String driver = "org.h2.Driver";
		String dburl = String.format("jdbc:h2:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE;", dbpath);
		String user = SysProperties.get("db.user", "embed");
		String pass = SysProperties.get("db.password", "embed");
		return new DBSource(user, pass, driver, dburl, 2, 4);
	}
	
	protected boolean isInitialized(StoreKey key) {
        return new File(dbPath(key) + ".mv.db").exists();
    }

	private String dbPath(StoreKey key) {
		return new File(getDbDir(), "xadmin_" + key.name().toLowerCase()).getAbsolutePath();
	}

	private String getDbDir() {
		return SysProperties.getStoreDir();
	}
	
    private byte[] readBytes(String file) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(file)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b;
            while((b = input.read()) != -1) {
                out.write(b);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
}
