package dev.xframe.admin.system.auth;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.service.config.HttpInterceptor;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Ordered;

@Configurator
@Ordered(Integer.MAX_VALUE)//保证AuthInterceptor第一个执行
public class AuthInterceptor implements HttpInterceptor {
    
    @Inject
    private AuthContext authCtx;
    
    public Response intercept(Request req) {
    	//设置ThreadLocal变量
    	OpUser.set(authCtx.getAuthUsername(req));
    	
        if(authCtx.isReqIllegal(req)) {
            throw new LogicException("Permission deny!");
        }
        return null;
    }

}
