package dev.xframe.admin.conf;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import dev.xframe.http.service.Request;
import dev.xframe.http.service.Response;
import dev.xframe.http.service.Response.ContentType;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.ServiceContext;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Eventual;
import dev.xframe.inject.Inject;
import dev.xframe.utils.Mimetypes;
import dev.xframe.utils.XPaths;


@Bean
public class WebFileHandler implements Eventual, Service {
    
    @Inject
    private ServiceContext serviceCtx;
    
    private Map<String, byte[]> caches = new HashMap<>();
    
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
        return new Response(ContentType.FILE, getReqFilePath(req));
    }

    private String getReqFilePath(Request req) {
        return new File(root, req.path()).getPath();
    }
    
    
    private String getFileName(String path) {
        return new File(path).getName();
    }
    
    private Response makeRespFromClassPath(Request req) {
        try {
            String path = getReqFilePath(req);
            byte[] bytes = caches.get(path);
            if(bytes == null) {
                synchronized (this) {
                    if((bytes = caches.get(path)) == null) {
                        InputStream input = this.getClass().getClassLoader().getResourceAsStream(path);
                        bytes = readBytes(input);
                        caches.put(path, bytes);
                    }
                }
            }
            return new Response(()->Mimetypes.get(getFileName(path)), bytes);
        } catch (IOException e) {
            return Response.NOT_FOUND.retain();
        }
    }

    private byte[] readBytes(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while((b = input.read()) != -1) {
            out.write(b);
        }
        return out.toByteArray();
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
