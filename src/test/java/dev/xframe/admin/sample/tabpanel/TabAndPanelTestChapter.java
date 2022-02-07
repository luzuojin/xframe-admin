package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.system.BasicContext;
import dev.xframe.admin.view.Navi;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.Arrays;

@Bean
@XChapter(name="面板测试",path="panel")
public class TabAndPanelTestChapter implements Loadable {
	
	@Inject
	private BasicContext basicCtx;

	@Override
	public void load() {
		basicCtx.registNaviValue("panel", ()->Arrays.asList(new Navi("动态一一", "d1"), new Navi("动态一二", "d2")));
		basicCtx.registEnumValue(PanelTest.PANEL_ROLE_KEY, ()->Arrays.asList(new VEnum("1001", "测试一"), new VEnum("1002", "测试二")));
	}

}
