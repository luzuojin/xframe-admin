package dev.xframe.admin.system;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.auth.AuthManager;
import dev.xframe.admin.system.auth.OpUser;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.view.values.VLogin;
import dev.xframe.admin.view.values.VUser;
import dev.xframe.http.Request;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;

@Rest("basic/profile")
public class ProfileService {
    
	@Inject
	private AuthManager authMgr;
	@Inject
	private SystemRepo sysRepo;
	@Inject
	private SystemManager sysMgr;
	
    @HttpMethods.POST
    public Object login(Request req, @HttpArgs.Body VLogin data) {
        User user = sysRepo.fetchUser(data.getName());
        if(user == null) {
            throw new LogicException("用户不存在");
        } else if(OpUser.isLocalUser(user.getName())) {//内网用户,只在内网ip访问时生效(admin权限),可删除该用户
            if(!authMgr.isLocalHost(req)){
                throw new LogicException("非内网访问");
            }
        } else if(!user.getPassw().equals(data.getPassw())) {
            throw new LogicException("密码错误");
        }
        //处理token/权限
        return new VUser(user.getName(), authMgr.regist(sysMgr.getPrivileges(user)));
    }

    @HttpMethods.DELETE
    public Object logout(Request req, @HttpArgs.Body VLogin data) {
    	authMgr.unregist(req, data.getName());
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
