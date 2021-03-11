package dev.xframe.admin.store;

import dev.xframe.jdbc.datasource.DBIdent;

public enum StoreKey implements DBIdent {

    DAT("dat_init.sql"),

    LOG("log_init.sql");

    //classpath:resources
    public final String script;
    public final String vTable;
    private StoreKey(String script) {
        this.script = script;
        this.vTable = String.format("T_VERSION_%S", this.name());
    }

}
