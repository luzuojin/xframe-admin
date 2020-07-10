package dev.xframe.admin.system.privilege;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.admin.system.BasicContext;
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("system/privilege")
@XSegment(name="权限列表", model=Privilege.class)
public class PrivilegeService {
	
	@Inject
	private BasicContext viewCtx;
	
	@HttpMethods.GET()
	public Object get() {
		List<Privilege> privileges = new ArrayList<>();
		viewCtx.getChapters().forEach(c->{
			privileges.add(new Privilege(c.getName(), c.getPath()));
			for (Segment seg : c.getSegments()) {
				privileges.add(new Privilege("&nbsp;&nbsp;&nbsp;&nbsp;"+seg.getName(), c.getPath() + "/" + seg.getPath()));
			}
		});
		return privileges;
	}

}
