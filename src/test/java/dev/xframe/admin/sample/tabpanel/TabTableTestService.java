package dev.xframe.admin.sample.tabpanel;

import java.util.Arrays;
import java.util.List;

import dev.xframe.admin.view.XAdapter;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.details.Table;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

@Rest("panel/{tab}/test2")
@XSegment(detail=Table.class, model=PanelTest1.class, desc="面板内容展示", name="PanelTest2")
public class TabTableTestService {

	@HttpMethods.GET("ini")
	public List<PanelTest1> get(@HttpArgs.Path String tab) {
		return Arrays.asList(new PanelTest1(tab), new PanelTest1("BBBB"));
	}
	
	@HttpMethods.GET
	public List<PanelTest1> get(@HttpArgs.Path String tab, @HttpArgs.Param @XColumn(enumKey=PanelTest.PANEL_ROLE_KEY) int roleId) {
		return get(tab);
	}
	
	@HttpMethods.DELETE
	public PanelTest del(@HttpArgs.Body @XAdapter PanelTest pt) {
		return null;
	}
	
}
