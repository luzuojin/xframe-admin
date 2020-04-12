package dev.xframe.admin.conf;

import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.service.config.HttpInterceptor;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Ordered;

@Configurator
@Ordered(Integer.MAX_VALUE)
public class AuthInterceptor implements HttpInterceptor {
    
    @Inject
    private AuthContext authCtx;
    
    public Response intercept(Request req) {
        if(authCtx.isReqIllegal(req)) {
            throw new LogicException("Permission deny!");
        }
        return null;
    }

}
