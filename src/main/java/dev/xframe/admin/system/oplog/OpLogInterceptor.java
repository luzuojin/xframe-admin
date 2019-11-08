package dev.xframe.admin.system.oplog;

import dev.xframe.http.service.Request;
import dev.xframe.http.service.Response;
import dev.xframe.http.service.config.HttpInterceptor;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XLogger;
import dev.xframe.utils.XStrings;
import io.netty.handler.codec.http.HttpMethod;

@Bean
public class OpLogInterceptor implements HttpInterceptor {
    
    @Inject
    private OpLogRepo logRepo;
    
    @Override
    public void after(Request req, Response resp) {//succ ops
        String user = OpLogUser.clear();
        if(user != null) {
            HttpMethod method = req.method();
            if(!method.equals(HttpMethod.GET)) {
                String params = XStrings.newStringUtf8(req.content());
                String path = req.trimmedPath();
                String host = req.remoteHost();
                
                XLogger.info("[{}] [{}] [{}] [{}] [{}]", user, host, method.name(), path, params);
                
                logRepo.add(new OpLog(user, path, params, host, method.name()));
            }
        }
    }
    
}
