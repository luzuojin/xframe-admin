package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.structs.Variant;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

import java.util.Arrays;


@Rest("nested/test")
@XSegment(name="nested", model=NestedTest.class)
public class NestedTestService {
	
	@HttpMethods.GET("ini")
	public Object ini() {
		return Arrays.asList(
				new NestedTest(1001, "tom", "001", new NestedObj(1, "b")),
				new NestedTest(1002, "cat", "002", new NestedObj2(2, "c", "des"))
				);
	}
	
	@HttpMethods.GET @XOption(type= EOption.Var)
	public Object get(@HttpArgs.Param @XColumn String type) {
		if("001".equals(type)) {
			return Variant.struct(NestedTest.class);
		}
		return Variant.struct(NestedTest2.class);
	}
	
	@HttpMethods.POST
	public Object edit(@HttpArgs.Body NestedTest nt) {
		return nt;
	}
	
	@HttpMethods.PUT
	public Object put(@HttpArgs.Body NestedTest nt) {
		return nt;
	}

}
