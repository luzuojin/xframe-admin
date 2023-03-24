package dev.xframe.admin.system.role;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.SysEnumKeys;
import dev.xframe.admin.system.SystemManager;
import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.system.XEnumKeys;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("xsystem/role")
@XSegment(name="角色列表", model=Role.class, order = 100)
public class RoleService {
	
	@Inject
	private SystemManager sysMgr;
	@Inject
	private SystemRepo sysRepo;
	
	//默认GET且空参数的方法为ini方法
	@HttpMethods.GET
	public Object get() {
		return sysMgr.getRoles();
	}
	
	@HttpMethods.POST
	public Object add(@HttpArgs.Body Role role) {
	    //check auth
		sysMgr.addRole(role);
		XEnumKeys.clear(SysEnumKeys.ROLE_LIST);
		return role;
	}
	
	@HttpMethods.DELETE
	public Object delete(@HttpArgs.Body Role role) {
		if(sysMgr.delRole(role.getId())) {
		    sysRepo.deleteRole(role);
			XEnumKeys.clear(SysEnumKeys.ROLE_LIST);
		    return role;
		}
		throw new LogicException("角色不存在");
	}
	
	@HttpMethods.PUT
	public Object edit(@HttpArgs.Body Role role) {
		if(sysMgr.getRole(role.getId()) != null) {
			sysMgr.setRole(role);
			sysRepo.saveRole(role);
			return role;
		}
		throw new LogicException("角色不存在");
	}
	
}
