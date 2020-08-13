package dev.xframe.admin.system;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.view.VLogin;
import dev.xframe.admin.view.VUser;
import dev.xframe.http.Request;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("basic/profile")
public class ProfileService {
	
	@Inject
	private AuthContext authCtx;
	@Inject
	private SystemRepo sysRepo;
	@Inject
	private SystemContext sysCtx;
	
    @HttpMethods.POST
    public Object login(@HttpArgs.Body VLogin data) {
        User user = sysRepo.fetchUser(data.getName());
        if(user == null)
            throw new LogicException("用户不存在");
        if(!user.getPassw().equals(data.getPassw()))
            throw new LogicException("密码错误");

        return new VUser(user.getName(), authCtx.regist(sysCtx.getPrivileges(user)));
    }

    @HttpMethods.DELETE
    public Object logout(Request req, @HttpArgs.Body VLogin data) {
    	authCtx.unregist(req, data.getName());
        return "{}";
    }

    @HttpMethods.PUT
    public Object profile(@HttpArgs.Body VLogin data) {
        User user = sysRepo.fetchUser(data.getName());
        user.setPassw(data.getPassw());
        sysRepo.saveUser(user);
        return "{}";
    }
    
}
