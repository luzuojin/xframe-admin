package dev.xframe.admin.sample.custom;

import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.system.settings.Setting;
import dev.xframe.admin.utils.JsonHelper;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Rest("chart/custom")
@XSegment(name = "自定义管理", model = CustomTablet.class)
public class CustomConfigService {

    public static final String KeyPrefix = "custom@";

    @Inject
    private SystemRepo sysRepo;
    @Inject
    private CustomComponent customComponent;

    @HttpMethods.GET
    public Object ini() {
        return getTablets(sysRepo);
    }

    static List<CustomTablet> getTablets(SystemRepo sysRepo) {
        return sysRepo.fetchSettings().stream().filter(s->s.getKey().startsWith(KeyPrefix)).map(Setting::getVal).map(v->JsonHelper.parseObject(v, CustomTablet.class)).collect(Collectors.toList());
    }

    @HttpMethods.POST()
    public Object add(@HttpArgs.Body CustomTablet tablet) {
        sysRepo.addSetting(new Setting(KeyPrefix + tablet.path, JsonHelper.toJSONString(tablet)));
        customComponent.onTabletChanged();
        return tablet;
    }
    @HttpMethods.PUT()
    public Object edit(@HttpArgs.Body CustomTablet tablet) {
        sysRepo.saveSetting(new Setting(KeyPrefix + tablet.path, JsonHelper.toJSONString(tablet)));
        customComponent.onTabletChanged();
        return tablet;
    }
    @HttpMethods.DELETE()
    public Object delete(@HttpArgs.Body CustomTablet tablet) {
        sysRepo.deleteSetting(new Setting(KeyPrefix + tablet.path, ""));
        customComponent.onTabletChanged();
        return tablet;
    }
}
