package dev.xframe.admin.system.user;

import java.util.List;
import java.util.stream.Collectors;

import dev.xframe.admin.system.SystemContext;
import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XStrings;

@Rest("system/user")
@XSegment(name="用户列表", model=User.class)//path use @http.path
public class UserService {
	
    @Inject
    private SystemRepo sysRepo;
    @Inject
    private SystemContext sysCtx;
    
	/**
	 * 点击侧边栏时调用该方法(GET且参数为空) 可以返回空数组
	 */
	@HttpMethods.GET("ini")
	public Object get() {
		return clearPass(sysRepo.fetchUsers());
	}
	
	@HttpMethods.POST
	public Object add(@HttpArgs.Body User user) {
	    validateUser(user);
		user.newCTime();
		sysRepo.addUser(user);//sync
		return clearPass(user);
	}

	private void validateUser(User user) {
    }
	
	private List<User> clearPass(List<User> users) {
	    users.forEach(this::clearPass);
	    return users;
	}
	private User clearPass(User user) {
	    user.setPassw("");
	    return user;
	}

    @HttpMethods.PUT
	public Object edit(@HttpArgs.Body User user) {
		User ex = sysRepo.fetchUser(user.getName());
		ex.setEmail(user.getEmail());
		ex.setPhone(user.getPhone());
		ex.setRoles(user.getRoles());
		sysRepo.saveUser(ex);//sync
		return clearPass(user);
	}
	
	@HttpMethods.DELETE
	public Object delete(@HttpArgs.Body User user) {
		sysRepo.deleteUser(user);
		return user;
	}

	@HttpMethods.GET
	public Object query(
			@HttpArgs.Param @XColumn("姓名") String name,
			@HttpArgs.Param @XColumn("手机") String phone,
			@HttpArgs.Param @XColumn("邮箱") String email
			){
	    List<User> datas = clearPass(sysRepo.fetchUsers());
		if(!XStrings.isEmpty(name)) {
			return datas.stream().filter(u->u.getName().contains(name)).collect(Collectors.toList());
		}
		if(!XStrings.isEmpty(phone)) {
			return datas.stream().filter(u->u.getPhone().contains(phone)).collect(Collectors.toList());
		}
		if(!XStrings.isEmpty(email)) {
			return datas.stream().filter(u->u.getEmail().contains(email)).collect(Collectors.toList());
		}
		return datas;
	}
	
}
