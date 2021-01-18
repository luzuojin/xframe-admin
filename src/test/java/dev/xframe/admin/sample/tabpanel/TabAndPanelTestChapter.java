package dev.xframe.admin.sample.tabpanel;

import java.util.Arrays;
import java.util.List;

import dev.xframe.admin.system.BasicContext;
import dev.xframe.admin.view.Navi;
import dev.xframe.admin.view.Padding;
import dev.xframe.admin.view.VEnum;
import dev.xframe.admin.view.XChapter;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

@Bean
@XChapter(name="面板测试",path="panel")
public class TabAndPanelTestChapter implements Padding, Loadable {
	
	@Inject
	private BasicContext basicCtx;

	@Override
	public List<Navi> get() {
		return Arrays.asList(new Navi("动态一一", "d1"), new Navi("动态一二", "d2"));
	}

	@Override
	public void load() {
		basicCtx.registEnumValue(PanelTest.PANEL_ROLE_KEY, ()->Arrays.asList(new VEnum("1001", "测试一"), new VEnum("1002", "测试二")));
	}

}
