package dev.xframe.admin.system;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.admin.system.oplog.OpLogUser;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.view.VLogin;
import dev.xframe.admin.view.VUser;
import dev.xframe.http.Request;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("basic")
public class BasicService {
	
	@Inject
	private BasicContext basicCtx;
	@Inject
	private AuthContext authCtx;
	@Inject
	private SystemRepo sysRepo;
	@Inject
	private SystemContext sysCtx;
	
	@HttpMethods.GET("summary")
	public Object summary(Request req) {
		return basicCtx.getSummary(authCtx.getPrivileges(req));
	}
	
	@HttpMethods.GET("enum")
	public Object getEnum(@HttpArgs.Param String key) {
		return basicCtx.getEnumValue(OpLogUser.get(), key);
	}
    
    @HttpMethods.POST("profile")
    public Object login(@HttpArgs.Body VLogin data) {
        User user = sysRepo.fetchUser(data.getName());
        if(user == null)
            throw new LogicException("用户不存在");
        if(!user.getPassw().equals(data.getPassw()))
            throw new LogicException("密码错误");

        return new VUser(user.getName(), authCtx.regist(sysCtx.getPrivileges(user)));
    }

    @HttpMethods.DELETE("profile")
    public Object logout(@HttpArgs.Body VLogin data) {
        return "{}";
    }

    @HttpMethods.PUT("profile")
    public Object profile(@HttpArgs.Body VLogin data) {
        User user = sysRepo.fetchUser(data.getName());
        user.setPassw(data.getPassw());
        sysRepo.saveUser(user);
        return "{}";
    }

}
