package dev.xframe.admin.system.settings;

import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.structs.Column;

public class SettingConfig {
    public final String originKey;
    public final Column column;
    public final String defaultValue;
    public final SettingListener listener;
    public SettingConfig(String key, int type, String name, String hint, String defaultValue, SettingListener listener) {
        String columnKye = key.replace(".", "_");
        this.originKey = key;
        this.column = new Column(columnKye, type, name, hint);
        this.defaultValue = defaultValue;
        this.listener = listener;
    }
}
