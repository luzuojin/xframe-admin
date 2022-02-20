package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.utils.JsonHelper;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.details.Flex;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.utils.XProperties;

import java.io.File;

@Rest("panel/{tab}/test1")
@XSegment(type=XSegment.type_panel, model=PanelTest.class, desc="面板内容展示", name="PanelTest1")
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

	@HttpMethods.GET("dl2")
	@XOption(type = XOption.type_dlr)
	public Object dlr(@HttpArgs.Param @XColumn int roleId) {
		return new FileResponse.Sys(new File(XProperties.get("user.dir"), "src/main/resources/web/js/xview.js")).setFileName().forceDownload();
	}

}
