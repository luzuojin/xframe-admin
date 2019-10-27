package dev.xframe.admin.system.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.utils.XStrings;

@Rest("system/user")
@XSegment(name="用户列表", model=User.class)//path use @http.path
public class UserService {
	
	private List<User> datas = new ArrayList<>();
	{
		datas.add(new User("xframe", "132", "xframe@x.com", "000"));
		datas.add(new User("admin", "133", "admin@x.com", "000"));
		datas.add(new User("luzj", "138", "luzj@x.com", "000"));
		datas.add(new User("test001", "139", "test@x.com", "000"));
		datas.add(new User("test002", "131", "user@x.com", "000"));
	}
	
	/**
	 * 点击侧边栏时调用该方法 可以返回空数组
	 */
	@HttpMethods.GET
	public Object get() {
		return datas;
	}
	
	@HttpMethods.POST("add")
	public Object add(@HttpArgs.Body User user) {
		user.newCTime();
		datas.add(user);
		return datas;
	}

	@HttpMethods.POST("edit")
	public Object edit(@HttpArgs.Body User user) {
		User ex = datas.stream().filter(u->u.getName().equals(user.getName())).findAny().orElse(null);
		if(ex != null) {
			ex.setEmail(user.getEmail());
			ex.setPhone(user.getPhone());
			ex.setPassw(user.getPassw());
		}
		return datas;
	}
	
	@HttpMethods.POST("delete")
	public Object delete(@HttpArgs.Body User user) {
		datas.remove(user);
		return datas;
	}

	@HttpMethods.GET("query")
	public Object query(
			@HttpArgs.Param @XColumn("姓名") String name,
			@HttpArgs.Param @XColumn("手机") String phone,
			@HttpArgs.Param @XColumn("邮箱") String email
			){
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
