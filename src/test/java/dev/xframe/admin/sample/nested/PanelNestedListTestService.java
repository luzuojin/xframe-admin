package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.details.Panel;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;


@Rest("nested/panellisttest")
@XSegment(name="panelnestedlist", detail= Panel.class, model=NestedListTest.class)
public class PanelNestedListTestService {
	
	@HttpMethods.GET("ini")
	public Object ini() {
		return new NestedListTest(1001, "tom", "nested001", new NestedObj(10, "b"), new NestedObj(11, "b"), new NestedObj(11, "b"));
	}
	
	@HttpMethods.PUT
	public Object put(@HttpArgs.Body NestedListTest nt) {
		return nt;
	}

}
