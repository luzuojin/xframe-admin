package dev.xframe.admin.system.role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.xframe.admin.basic.BasicContext;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.injection.Inject;

@Rest("system/role")
@XSegment(name="角色列表", model=Role.class)
public class RoleService {
	
	@Inject
	private BasicContext basicCtx;
	
	private List<Role> datas = new ArrayList<>();
	
	{
		Role e = new Role();
		e.setName("admin");
		e.setAuthorities(Arrays.asList("system", "centra"));
		e.setAuthoritiesDesc(Arrays.asList("系统管理","中控"));
		datas.add(e);
	}
	
	@HttpMethods.GET
	public Object get() {
		return datas;
	}
	
	@HttpMethods.POST("add")
	public Object add(@HttpArgs.Body Role role) {
		setRoleDesc(role);
		datas.add(role);
		return datas;
	}
	
	@HttpMethods.POST("delete")
	public Object delete(@HttpArgs.Body Role role) {
		datas.remove(role);
		return datas;
	}
	
	@HttpMethods.POST("edit")
	public Object edit(@HttpArgs.Body Role role) {
		Role ex = datas.stream().filter(r->r.getName().equals(role.getName())).findAny().orElse(null);
		if(ex != null) {
			ex.setAuthorities(role.getAuthorities());
			ex.setReadOnly(role.getReadOnly());
			setRoleDesc(ex);
		}
		return datas;
	}

	private void setRoleDesc(Role role) {
		role.setAuthoritiesDesc(role.getAuthorities().stream().map(a->basicCtx.getPrivilegeDesc().get(a)).collect(Collectors.toList()));
	}
	
}
