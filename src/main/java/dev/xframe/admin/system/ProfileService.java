package dev.xframe.admin.system;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.auth.AuthManager;
import dev.xframe.admin.system.auth.OpUser;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.system.user.UserInterface;
import dev.xframe.admin.system.user.UserInterfaces;
import dev.xframe.admin.view.values.VLogin;
import dev.xframe.admin.view.values.VResp;
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
            int type = UserInterfaces.Internal.tryValidate(data.getName(), data.getPassw());
            if(type == -1) {
                throw new LogicException("用户不存在");
            }
            //extended user, new for this system
            UserInterface ui = UserInterfaces.Internal.getInterface(type);
            user = new User(data.getName(), ui.makePhone(data.getName()), ui.makeEmail(data.getName()), type);
            sysRepo.addUser(user);
        } else if(user.getType() != UserInterfaces.TypeNormal) {
            UserInterfaces.Internal.getInterface(user.getType()).validate(data.getName(), data.getPassw());
        } else if(OpUser.isLocalUser(user.getName())) {//内网用户,只在内网ip访问时生效(admin权限),可删除该用户
            if(!authMgr.isLocalHost(req)){
                throw new LogicException("非内网访问");
            }
        } else if(!user.getPassw().equals(data.getPassw())) {
            throw new LogicException("密码错误");
        }
        //处理token/权限
        VUser vUser = new VUser(user.getName(), authMgr.regist(sysMgr.getPrivileges(user)));
        return user.roled() ? vUser : VResp.hint("未分配角色,请联系系统管理员!!!", vUser);
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
