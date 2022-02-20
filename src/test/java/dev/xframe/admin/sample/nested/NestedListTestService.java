package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.utils.XProperties;

import java.io.File;
import java.util.Arrays;


@Rest("nested/listtest")
@XSegment(name="nestedlist", model=NestedListTest.class)
public class NestedListTestService {
	
	@HttpMethods.GET("ini")
	public Object ini() {
		return Arrays.asList(
				new NestedListTest(1001, "tom", "nested001", new NestedObj(10, "b"), new NestedObj(11, "b")),
				new NestedListTest(1002, "cat", "nested002", new NestedObj(20, "c"), new NestedObj(21, "c"))
				);
	}
	
	@HttpMethods.POST
	public Object edit(@HttpArgs.Body NestedListTest nt) {
		return nt;
	}
	
	@HttpMethods.PUT
	public Object put(@HttpArgs.Body NestedListTest nt) {
		return nt;
	}

	@HttpMethods.GET("dl1")
	@XOption(type = XOption.type_dlh)
	public Object dlh() {
		return new FileResponse.Sys(new File(XProperties.get("user.dir"), "src/main/resources/web/js/xadmin.js")).setFileName().forceDownload();
	}
	@HttpMethods.GET("dl2")
	@XOption(type = XOption.type_dlr)
	public Object dlr(@HttpArgs.Param @XColumn int id) {
		return new FileResponse.Sys(new File(XProperties.get("user.dir"), "src/main/resources/web/js/xview.js")).setFileName().forceDownload();
	}
}
