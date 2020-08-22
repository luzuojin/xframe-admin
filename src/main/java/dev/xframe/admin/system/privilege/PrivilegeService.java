package dev.xframe.admin.system.privilege;

import java.util.stream.Collectors;

import dev.xframe.admin.system.SystemContext;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("system/privilege")
@XSegment(name="权限列表", model=Privilege.class)
public class PrivilegeService {
	
	@Inject
	private SystemContext sysCtx;
	
	@HttpMethods.GET()
	public Object get() {
		return sysCtx.getPrivileges().stream().filter(p->p!=Privilege.WHOLE).collect(Collectors.toList());
	}

}
