package dev.xframe.admin.conf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.response.ContentType;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.ServiceContext;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Eventual;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XCaught;
import dev.xframe.utils.XPaths;
import dev.xframe.utils.XProperties;


@Bean
public class WebFileHandler implements Eventual, Service {
    
    private static final String TitlePropKey  = "xframe.admin.title";
    private static final String IconPropKey   = "xframe.admin.icon";
    private static final String IndexFileName = "index.html";
    private static final String DefaultTitle  = "XFrameAdmin";
    private static final String DefaultIcon   = "xframe.png";
    
    @Inject
    private ServiceContext serviceCtx;
    @Inject
    private AuthContext authCtx;
    
    private Map<String, Response> caches = new HashMap<>();
    
    private Function<Request, Response> func;
    
    private String root;
    
    
    private Response index;
    
    @Override
    public Response exec(Request req) {
        return func.apply(req);
    }
    
    private boolean isIndexHtml(Request req) {
        return IndexFileName.equals(req.xpath());
    }

    private Response indexResp() {
        return index;
    }
    
    @Override
    public void eventuate() {
        try {
            String path = XPaths.toPath(WebFileHandler.class.getProtectionDomain().getCodeSource().getLocation());
            String xdir = "web/";
            if(path.endsWith(".jar")) {
                this.root = xdir;
                listRelativizeJarFiles(path, xdir).forEach(this::makeHandler);
                this.func = this::makeRespFromClassPath;
                this.index = new FileResponse.Binary(ContentType.HTML, readIndexHtml(WebFileHandler.class.getClassLoader().getResourceAsStream(new File(root, IndexFileName).getPath())));
            } else {
                this.root = new File(path, xdir).getPath();
                XPaths.listRelativizeFiles(root).forEach(this::makeHandler);
                this.func = this::makeRespFromDirectory;
                this.index = new FileResponse.Binary(ContentType.HTML, readIndexHtml(new FileInputStream(new File(root, IndexFileName))));
            }
        } catch (Exception e) {
            XCaught.throwException(e);
        }
    }

    private byte[] readIndexHtml(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while((b = input.read()) != -1) {
            out.write(b);
        }
        String content = out.toString();
        content = content.replace(DefaultTitle, XProperties.get(TitlePropKey, DefaultTitle));
        content = content.replace(DefaultIcon, XProperties.get(IconPropKey, DefaultIcon));
        return content.getBytes();
    }

    private void makeHandler(String p) {
        String path = p.replace("\\", "/");//windows path to url
        serviceCtx.registService(path, this);
        authCtx.addUnblockedPath(path);
    }
    
    private Response makeRespFromDirectory(Request req) {
        if(isIndexHtml(req)) {
            return indexResp();
        }
        return new FileResponse.Sys(getReqFile(req));
    }

    private File getReqFile(Request req) {
        return new File(root, req.path());
    }
    
    private Response makeRespFromClassPath(Request req) {
        if(isIndexHtml(req)) {
            return indexResp();
        }
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
