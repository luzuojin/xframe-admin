package dev.xframe.admin.system.oplog;

import dev.xframe.admin.system.auth.OpUser;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.config.HttpListener;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XLogger;
import dev.xframe.utils.XStrings;
import io.netty.handler.codec.http.HttpMethod;

@Bean
public class OpLogListener implements HttpListener {

    @Inject
    private OpLogRepo logRepo;

    @Override
    public void onAccessComplete(Request req, Response resp) {//succ ops
        String user = OpUser.getName();
        if(user == null) return;
        HttpMethod method = req.method();
        if(!method.equals(HttpMethod.GET) && !method.equals(HttpMethod.OPTIONS)) {
            String path = req.xpath();
            if(!path.startsWith("basic/upload")) {
                byte[] content = req.content();
                String params = content.length > 1024 ? "long message" : XStrings.newStringUtf8(content);
                String host = XStrings.orElse(req.getHeader("x-host"), req.remoteHost());

                XLogger.info("[{}] [{}] [{}] [{}] [{}]", user, host, method.name(), path, params);
                logRepo.add(new OpLog(user, path, params, host, method.name()));
            }
        }
    }

}
