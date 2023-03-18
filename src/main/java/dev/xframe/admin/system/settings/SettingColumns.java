package dev.xframe.admin.system.settings;

import dev.xframe.admin.view.EColumn;

import java.util.TreeMap;

public class SettingColumns {

    static TreeMap<String, SettingConfig> settingColumns = new TreeMap<>();

    public static void reg(String key, EColumn type, String name) {
        reg(key, type, name, SettingListener.Ignored);
    }
    public static void reg(String key, EColumn type, String name, SettingListener listener) {
        reg(key, type, name, "", listener);
    }
    public static void reg(String key, EColumn type, String name, String defaultValue) {
        reg(key, type, name, defaultValue, SettingListener.Ignored);
    }
    public static void reg(String key, EColumn type, String name, String defaultValue, SettingListener listener) {
        SettingConfig config = new SettingConfig(key, type, name, defaultValue, listener);
        settingColumns.put(config.column.getKey(), config);
    }

}
