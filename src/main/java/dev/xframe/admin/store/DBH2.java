package dev.xframe.admin.store;

import dev.xframe.jdbc.JdbcEnviron.EnvironConfigurator;
import dev.xframe.jdbc.datasource.DBSource;
import dev.xframe.utils.XProperties;

import java.io.File;

public class DBH2 implements Database {

    @Override
    public void setup(EnvironConfigurator configurator) {
        configurator.setUpsertUsage(false, false);
    }

    @Override
    public DBSource makeDBSource(StoreKey key) {
        String dbpath = getDbPath(key);
        String driver = "org.h2.Driver";
        String dburl = String.format("jdbc:h2:%s;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE;MODE=MYSQL", dbpath);
        String user = getDbUser();
        String pass = getDbPass();
        return new DBSource(user, pass, driver, dburl, getDbConn(), getDbConn());
    }

    protected String getDbPath(StoreKey key) {
        return new File(getDbDir(), "xadmin_" + key.name().toLowerCase()).getAbsolutePath();
    }
    //via xframe.properites
    protected String getDbDir() {
        return XProperties.get("store.dir", XProperties.get("work.dir", XProperties.get("user.dir")));
    }
    protected String getDbUser() {
        return XProperties.get("db.user", "embed");
    }
    protected String getDbPass() {
        return XProperties.get("db.pass", "embed");
    }
    protected    int getDbConn() {
        return XProperties.getAsInt("db.conn", 2);
    }

}
