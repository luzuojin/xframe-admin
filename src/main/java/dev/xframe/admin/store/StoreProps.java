package dev.xframe.admin.store;

import dev.xframe.utils.XProperties;

public class StoreProps {
    public static String getDir() {
        return XProperties.get("store.dir", XProperties.get("work.dir", XProperties.get("user.dir")));
    }
    public static String getDbUser() {
        return XProperties.get("db.user", "embed");
    }
    public static String getDbPass() {
        return XProperties.get("db.password", "embed");
    }
}
