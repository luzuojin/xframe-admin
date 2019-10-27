package dev.xframe.admin.basic;

import java.util.Collections;

import dev.xframe.admin.view.VLogin;
import dev.xframe.admin.view.VUser;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.injection.Inject;

@Rest("basic")
public class BasicService {
	
	@Inject
	private BasicContext basic;
	
	@HttpMethods.GET("summary")
	public Object summary() {
		return basic.getSummary();
	}
	
	@HttpMethods.GET("logout")
	public Object logout() {
		return "{}";
	}
	
	@HttpMethods.POST("profile")
	public Object profile(@HttpArgs.Body VLogin data) {
		return "{}";
	}
	
	/**
	 * @see BasicUser
	 */
	@HttpMethods.POST("login")
	public Object login(@HttpArgs.Body VLogin data) {
		VUser u = new VUser();
		u.setContact("luzj@xframe.dev");
		u.setName("luzj");
		u.setRole("Administrator");
		u.setIcon("dist/img/xfavicon.png");
		u.setToken("xframe-test-token");
		return u;
	}
	
	@HttpMethods.GET("enum")
	public Object getEnum(@HttpArgs.Param String key) {
		return Collections.EMPTY_LIST;
	}

}
