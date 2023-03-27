package dev.xframe.admin.system.auth;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.config.HttpInterceptor;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Ordered;

@Bean
@Ordered(Integer.MAX_VALUE)//保证AuthInterceptor第一个执行
public class AuthInterceptor implements HttpInterceptor {

    @Inject
    private AuthManager authMgr;

    public Response intercept(Request req) {
        //设置ThreadLocal变量
        OpUser.set(authMgr.getAuthUser(req));
        OpHost.set(AuthManager.getRemoteHost(req));

        if(authMgr.isReqIllegal(req)) {
            throw new LogicException("Permission deny!");
        }
        return null;
    }

}
