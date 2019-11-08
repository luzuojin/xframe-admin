package dev.xframe.admin.conf;

import dev.xframe.admin.system.AuthContext;
import dev.xframe.http.service.Request;
import dev.xframe.http.service.Response;
import dev.xframe.http.service.config.HttpInterceptor;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XLogger;

@Configurator
public class AuthInterceptor implements HttpInterceptor {
    
    @Inject
    private AuthContext authCtx;
    
    public Response before(Request req) {
        if(XLogger.isDebugEnabled() && req.path().contains(".")) {//file??
            return null;
        }
        if(authCtx.isReqIllegal(req)) {
            throw new LogicException("Permission deny!");
        }
        return null;
    }

}
