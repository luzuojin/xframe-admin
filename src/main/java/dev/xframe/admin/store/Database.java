package dev.xframe.admin.store;

import dev.xframe.jdbc.JdbcEnviron.EnvironConfigurator;
import dev.xframe.jdbc.JdbcTemplate;
import dev.xframe.jdbc.datasource.DBSource;
import dev.xframe.jdbc.datasource.DataSources;

import javax.sql.DataSource;

public interface Database {

    /**
     * setup jdbc environ
     */
    default void setup(EnvironConfigurator configurator) {
    }

    default JdbcTemplate makeJdbcTemplate(StoreKey key) {
        return JdbcTemplate.of(makeDataSource(key));
    }
    default DataSource makeDataSource(StoreKey key) {
        return DataSources.tomcatJdbc(makeDBSource(key));
    }

    /**
     * make dbsource
     */
    DBSource makeDBSource(StoreKey key);

    /**
     * try create database
     */
    default void tryCreate(StoreKey key) {
        //do nothing;
    }

}
