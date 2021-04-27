package dev.xframe.admin.system.role;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.ClrEnumKeys;
import dev.xframe.admin.system.SystemContext;
import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.system.XEnumKeys;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("system/role")
@XSegment(name="角色列表", model=Role.class)
public class RoleService {
	
	@Inject
	private SystemContext sysCtx;
	@Inject
	private SystemRepo sysRepo;
	
	//默认GET且空参数的方法为ini方法
	@HttpMethods.GET
	public Object get() {
		return sysCtx.getRoles();
	}
	
	@HttpMethods.POST
	public Object add(@HttpArgs.Body Role role) {
	    //check auth
		sysCtx.addRole(role);
		ClrEnumKeys.add(XEnumKeys.ROLE_LIST);
		return role;
	}
	
	@HttpMethods.DELETE
	public Object delete(@HttpArgs.Body Role role) {
		if(sysCtx.getRoles().remove(role)) {
		    sysRepo.deleteRole(role);
		    return role;
		}
		throw new LogicException("角色不存在");
	}
	
	@HttpMethods.PUT
	public Object edit(@HttpArgs.Body Role role) {
		Role ex = sysCtx.getRoles().stream().filter(r->r.getId()==role.getId()).findAny().orElse(null);
		if(ex != null) {
			ex.setAuthorities(role.getAuthorities());
			ex.setOptions(role.getOptions());
			sysRepo.saveRole(ex);
			return ex;
		}
		throw new LogicException("角色不存在");
	}
	
}
