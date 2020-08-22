package dev.xframe.admin.system.oplog;

import dev.xframe.admin.system.auth.OpUser;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
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
    public Response intercept(Request req) {
        return null;
    }
    
	@Override
	public void afterHandle(Request req, Response resp) {//succ ops
		String user = OpUser.get();
        if(user == null) return;
        HttpMethod method = req.method();
        if(!method.equals(HttpMethod.GET) && !method.equals(HttpMethod.OPTIONS)) {
        	String path = req.xpath();
        	if(!path.startsWith("basic/upload")) {
        		String params = XStrings.newStringUtf8(req.content());
        		String host = XStrings.orElse(req.getHeader("x-host"), req.remoteHost());

        		XLogger.info("[{}] [{}] [{}] [{}] [{}]", user, host, method.name(), path, params);
        		logRepo.add(new OpLog(user, path, params, host, method.name()));
        	}
        }
	}
    
}
