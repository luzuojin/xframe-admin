package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.system.XRegistrator;
import dev.xframe.admin.view.structs.Navi;
import dev.xframe.admin.view.values.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.Arrays;

@Bean
@XChapter(name="面板测试",path="panel")
public class TabAndPanelTestChapter implements Loadable {
	
	@Inject
	private XRegistrator xReg;

	@Override
	public void load() {
		xReg.registNaviValue("panel", ()->Arrays.asList(new Navi("d1", "动态一一"), new Navi("d2", "动态一二")));
		xReg.registEnumValue(PanelTest.PANEL_ROLE_KEY, ()->Arrays.asList(new VEnum("1001", "测试一"), new VEnum("1002", "测试二")));
	}

}
