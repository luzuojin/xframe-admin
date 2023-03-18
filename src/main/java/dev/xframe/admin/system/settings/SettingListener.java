package dev.xframe.admin.system.settings;

public interface SettingListener {

    void onChanged(Setting setting);

    SettingListener Ignored = s -> {};

}
