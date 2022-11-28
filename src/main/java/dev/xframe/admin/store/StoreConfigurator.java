package dev.xframe.admin.store;

import dev.xframe.inject.Configurator;
import dev.xframe.inject.Loadable;
import dev.xframe.inject.Providable;
import dev.xframe.jdbc.JdbcEnviron;
import dev.xframe.jdbc.JdbcEnviron.EnvironConfigurator;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.PSSetter;
import dev.xframe.jdbc.tools.SQLScript;
import dev.xframe.utils.XCaught;
import dev.xframe.utils.XProperties;
import dev.xframe.utils.XStrings;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Configurator
@Providable//可以替换 
public class StoreConfigurator implements Loadable {

    protected Database makeDatabase() {
        return XProperties.getAsBool("xframe.admin.db.mysql", false) ? new DBMysql() : new DBH2();
    }

    @Override
    public void load() {
        EnvironConfigurator configurator = JdbcEnviron.getConfigurator();
        Database database = makeDatabase();
        database.setup(configurator);
        for (StoreKey key : StoreKey.values()) {
            database.tryCreate(key);
            configurator.setJdbcTemplate(key, database.makeJdbcTemplate(key));
            tryCreateTables(key);
        }
    }

    protected void tryCreateTables(StoreKey key) {
        JdbcTemplate jdbc = JdbcEnviron.getJdbcTemplate(key);
        if(jdbc.fetchMany("SHOW TABLES;", PSSetter.NONE, rs->rs.getString(1)).stream().anyMatch(key.vTable::equals)) {
            return;
        }
        for (String script : readScripts(key.script)) {
            jdbc.execute(script);
        }
    }

    public static List<String> readScripts(String file) {
        try (InputStream input = StoreConfigurator.class.getClassLoader().getResourceAsStream(file)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b;
            while((b = input.read()) != -1) {
                out.write(b);
            }
            return SQLScript.parse(XStrings.newStringUtf8(out.toByteArray()));
        } catch (Exception e) {
            throw XCaught.throwException(e);
        }
    }
}
