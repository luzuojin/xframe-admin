package dev.xframe.admin.sample.data;

import dev.xframe.admin.system.XRegistrator;
import dev.xframe.admin.view.XChapter;
import dev.xframe.admin.view.structs.Navi;
import dev.xframe.inject.Component;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.Arrays;

@Component
@XChapter(name="data&chart", path="chart")
public class DataChapter implements Loadable {

    @Inject
    private XRegistrator xReg;

    @Override
    public void load() {
        xReg.registNaviValue("chart", () -> Arrays.asList(new Navi("tabs1", "图表测试1"),new Navi("tabs2", "图表测试2")));
    }

}
