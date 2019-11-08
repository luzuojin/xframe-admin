package dev.xframe.admin.conf;

import java.io.File;

import dev.xframe.http.service.Request;
import dev.xframe.http.service.Response;
import dev.xframe.http.service.Response.ContentType;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Eventual;
import dev.xframe.inject.Inject;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.ServiceContext;
import dev.xframe.utils.XLogger;
import dev.xframe.utils.XPaths;


@Bean
public class TmpFileServer implements Eventual, Service {
    
    @Inject
    private ServiceContext serviceCtx;
    
    private File root = new File(System.getProperty("user.dir"), "src/main/webapp");
    
    @Override
    public void eventuate() {
        if(XLogger.isDebugEnabled()) {
            XPaths.listRelativizeFiles(root).stream().filter(f->!f.startsWith(".")).forEach(path->serviceCtx.registService(path, this));
        }
    }
     
    @Override
    public Response exec(Request req) throws Throwable {
        return new Response(ContentType.FILE, new File(root, req.path()).getPath());
    }

}
