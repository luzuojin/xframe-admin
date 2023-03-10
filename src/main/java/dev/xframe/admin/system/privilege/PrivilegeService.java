package dev.xframe.admin.system.privilege;

import dev.xframe.admin.system.SystemManager;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

import java.util.stream.Collectors;

@Rest("system/privilege")
@XSegment(name="权限列表", model=Privilege.class)
public class PrivilegeService {
	
	@Inject
	private SystemManager sysMgr;
	
	@HttpMethods.GET()
	public Object get() {
		return sysMgr.getPrivileges().stream().filter(p->p!=Privilege.WHOLE).collect(Collectors.toList());
	}

}
