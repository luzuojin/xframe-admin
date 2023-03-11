package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.utils.JsonHelper;
import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.structs.Variant;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.utils.XProperties;

import java.io.File;

@Rest("panel/{tab}/test1")
@XSegment(type= EContent.Panel, model=PanelTest.class, desc="面板内容展示", name="PanelTest1")
public class TabPanelTestService {

	@HttpMethods.GET("ini")
	public PanelTest get() {
		return new PanelTest();
	}
	
	@HttpMethods.GET
	public Variant get(@HttpArgs.Param @XColumn int roleId) {
		if(roleId == 1001) {
			return Variant.struct(PanelTest1.class);
		}
		return Variant.struct(PanelTest2.class);
	}
	
	@HttpMethods.PUT
	public PanelTest put(@HttpArgs.Header(Variant.HEADER_KEY) String flexName, @HttpArgs.Body String json) throws Exception {
		PanelTest pt = (PanelTest) JsonHelper.parseObject(json, Class.forName(flexName));
		System.out.println(pt);
		return pt;
	}

	@HttpMethods.DELETE
	public PanelTest del(@HttpArgs.Body String json) {
		return null;
	}

	@HttpMethods.GET("dl2")
	@XOption(type = EOption.Dlr)
	public Object dlr(@HttpArgs.Param @XColumn int roleId) {
		return new FileResponse.Sys(new File(XProperties.get("user.dir"), "src/main/resources/web/js/xview.js")).setFileName().forceDownload();
	}

}
