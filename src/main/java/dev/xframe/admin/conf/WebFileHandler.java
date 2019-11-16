package dev.xframe.admin.conf;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.ServiceContext;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Eventual;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XPaths;


@Bean
public class WebFileHandler implements Eventual, Service {
    
    @Inject
    private ServiceContext serviceCtx;
    
    private Map<String, Response> caches = new HashMap<>();
    
    private Function<Request, Response> func;
    
    private String root;
    
    @Override
    public Response exec(Request req) throws Throwable {
        return func.apply(req);
    }
    
    @Override
    public void eventuate() {
        String path = XPaths.toPath(WebFileHandler.class.getProtectionDomain().getCodeSource().getLocation());
        String xdir = "web/";
        if(path.endsWith(".jar")) {
            this.root = xdir;
            listRelativizeJarFiles(path, xdir).forEach(p->serviceCtx.registService(p, this));
            this.func = this::makeRespFromClassPath;
        } else {
            this.root = new File(path, xdir).getPath();
            XPaths.listRelativizeFiles(root).forEach(p->serviceCtx.registService(p, this));
            this.func = this::makeRespFromDirectory;
        }
    }
    
    private Response makeRespFromDirectory(Request req) {
        return new FileResponse.Sys(getReqFile(req));
    }

    private File getReqFile(Request req) {
        return new File(root, req.path());
    }
    
    private Response makeRespFromClassPath(Request req) {
    	String path = getReqFile(req).getPath();
    	Response resp = caches.get(path);
    	if(resp == null) {
    		synchronized (this) {
    			if((resp = caches.get(path)) == null) {
    				resp = new FileResponse.ClassPath(path);
    				caches.put(path, resp);
    			}
    		}
    	}
    	return resp;
    }

    private List<String> listRelativizeJarFiles(String path, String xdir) {
        List<String> files = new LinkedList<>();
        try (JarFile jarFile = new JarFile(path);) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                String file = entry.getName();
                if(file.startsWith(xdir) && !entry.isDirectory()) {
                    files.add(file.substring(xdir.length()));
                }
            }
        } catch (IOException e) {
            //ignore
        }
        return files;
    }
    
}
