package dev.xframe.admin.system.settings;

import dev.xframe.admin.view.EColumn;

import java.util.TreeMap;

public class SettingColumns {

    static TreeMap<String, SettingConfig> settingColumns = new TreeMap<>();

    public static void reg(String key, int type, String name) {
        reg(key, type, name, "");
    }
    public static void reg(String key, int type, String name, String hint) {
        reg(key, type, name, hint, SettingListener.Ignored);
    }
    public static void reg(String key, int type, String name, SettingListener listener) {
        reg(key, type, name, "", SettingListener.Ignored);
    }
    public static void reg(String key, int type, String name, String hint,  SettingListener listener) {
        reg(key, type, name, hint, "", listener);
    }
    public static void reg(String key, int type, String name, String hint, String defaultValue) {
        reg(key, type, name, hint, defaultValue, SettingListener.Ignored);
    }
    public static void reg(String key, int type, String name, String hint, String defaultValue, SettingListener listener) {
        SettingConfig config = new SettingConfig(key, type, name, hint, defaultValue, listener);
        settingColumns.put(config.column.getKey(), config);
    }

}
