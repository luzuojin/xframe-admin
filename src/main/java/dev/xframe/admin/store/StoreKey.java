package dev.xframe.admin.store;

import dev.xframe.jdbc.datasource.DBIdent;

public enum StoreKey implements DBIdent {

    DAT("dat_init.sql"),

    LOG("log_init.sql");

    //classpath:resources
    public final String script;
    private StoreKey(String script) {
        this.script = script;
    }

}
