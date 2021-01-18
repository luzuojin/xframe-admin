package dev.xframe.admin.sample.nested;

import java.util.Arrays;

import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;


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

}
