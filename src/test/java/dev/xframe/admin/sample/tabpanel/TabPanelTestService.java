package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.utils.JsonHelper;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.details.Flex;
import dev.xframe.admin.view.details.Panel;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

@Rest("panel/{tab}/test1")
@XSegment(detail=Panel.class, model=PanelTest.class, desc="面板内容展示", name="PanelTest1")
public class TabPanelTestService {

	@HttpMethods.GET("ini")
	public PanelTest get() {
		return new PanelTest();
	}
	
	@HttpMethods.GET
	public Flex get(@HttpArgs.Param @XColumn int roleId) {
		if(roleId == 1001) {
			return Flex.struct(PanelTest1.class);
		}
		return Flex.struct(PanelTest2.class);
	}
	
	@HttpMethods.PUT
	public PanelTest put(@HttpArgs.Header(Flex.HEADER_KEY) String flexName, @HttpArgs.Body String json) throws Exception {
		PanelTest pt = (PanelTest) JsonHelper.parseObject(json, Class.forName(flexName));
		System.out.println(pt);
		return pt;
	}

	@HttpMethods.DELETE
	public PanelTest del(@HttpArgs.Body String json) {
		return null;
	}
	
}
