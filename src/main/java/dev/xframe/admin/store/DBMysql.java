package dev.xframe.admin.store;

import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.PSSetter;
import dev.xframe.jdbc.datasource.DBSource;
import dev.xframe.jdbc.datasource.DataSources;
import dev.xframe.utils.XProperties;
import org.apache.tomcat.jdbc.pool.DataSource;

public class DBMysql implements Database {

    private static final String Driver = "com.mysql.jdbc.Driver";
    private static final String Url0 = "jdbc:mysql://%s:%s?characterEncoding=utf-8";//for create database
    private static final String Url1 = "jdbc:mysql://%s:%s/%s?characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false&useServerPrepStmts=true&rewriteBatchedStatements=true";

    @Override
    public void tryCreate(StoreKey key) {
        DBSource conf = new DBSource(getDbUser(), getDbPass(), Driver, String.format(Url0, getDbHost(), getDbPort()), 1, 1);
        DataSource source = (DataSource) DataSources.tomcatJdbc(conf);
        JdbcTemplate jdbc = JdbcTemplate.of(source);
        if(jdbc.fetchMany("show databases;", PSSetter.NONE, rs->rs.getString(1)).stream().noneMatch(getDbName()::equalsIgnoreCase)) {
            jdbc.execute(String.format("CREATE DATABASE IF NOT EXISTS %s;", getDbName()));
        }
        source.close();
    }

    @Override
    public DBSource makeDBSource(StoreKey key) {
        return new DBSource(getDbUser(), getDbPass(), Driver, String.format(Url1, getDbHost(), getDbPort(), getDbName()), getDbConn(), getDbConn());
    }

    protected String getDbUser() {
        return XProperties.get("db.user", "root");
    }
    protected String getDbPass() {
        return XProperties.get("db.pass", "root");
    }
    protected String getDbHost() {
        return XProperties.get("db.host", "127.0.0.1");
    }
    protected String getDbPort() {
        return XProperties.get("db.port", "3306");
    }
    protected String getDbName() {
        return XProperties.get("db.name", "db_xframe_admin");
    }
    protected    int getDbConn() {
        return XProperties.getAsInt("db.conn", 2);
    }
}
