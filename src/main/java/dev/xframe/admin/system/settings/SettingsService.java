package dev.xframe.admin.system.settings;

import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.utils.JsonHelper;
import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.structs.Column;
import dev.xframe.admin.view.structs.Variant;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Rest("system/settings/")
@XSegment(name="全局配置", type = EContent.Panel, desc = "全局配置", order = 80)
public class SettingsService {

    @Inject
    private SystemRepo sysRepo;

    @HttpMethods.GET
    public Object ini() {
        return new Variant(new Variant.Struct(getSettingsStruct(), "settings"), getSettingsData());
    }
    private List<Column> getSettingsStruct() {
        return SettingColumns.settingColumns.values().stream().map(c->c.column).collect(Collectors.toList());
    }
    private Map<String, String> getSettingsData() {
        Map<String, Setting> settings = sysRepo.fetchSettings().stream().collect(Collectors.toMap(Setting::getKey, Function.identity()));
        Map<String, String> data = new HashMap<>();
        SettingColumns.settingColumns.forEach((key, cfg) -> {
            Setting value = settings.get(cfg.originKey);
            if(value == null) {//init
                value = new Setting(cfg.originKey, cfg.defaultValue);
                sysRepo.addSetting(value);
            }
            data.put(key, value.getVal());
        });
        return data;
    }

    @HttpMethods.PUT
    @XOption("保存")
    public Object edit(@HttpArgs.Body String jsonBody) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = JsonHelper.parseObject(jsonBody, Map.class);
        Map<String, String> ext = getSettingsData();
        Map<String, Setting> chagned = new HashMap<>();
        ext.forEach((k, v) -> {
            String nv = map.getOrDefault(k, v);
            if(!v.equals(nv)) {
                String  sk = SettingColumns.settingColumns.get(k).originKey;
                Setting ss = new Setting(sk, nv);
                sysRepo.saveSetting(ss);
                chagned.put(k, ss);
                ext.put(k, nv);
            }
        });
        //保证所有值都Save完成之后再通知
        chagned.forEach((k, v) -> {
            SettingColumns.settingColumns.get(k).listener.onChanged(v);
        });
        return ext;
    }

}
