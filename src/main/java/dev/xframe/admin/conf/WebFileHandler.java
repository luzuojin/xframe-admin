package dev.xframe.admin.conf;

import dev.xframe.admin.system.BasicManager;
import dev.xframe.admin.system.auth.AuthManager;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.response.ContentType;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Service;
import dev.xframe.http.service.ServiceDirectory;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Eventual;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XCaught;
import dev.xframe.utils.XPaths;
import dev.xframe.utils.XProperties;
import dev.xframe.utils.XStrings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


@Bean
public class WebFileHandler implements Eventual {
    
    private static final String RootRedirect  = "xframe.admin.rootredirect";
    private static final String TitlePropKey  = "xframe.admin.title";
    private static final String IconPropKey   = "xframe.admin.icon";
    private static final String IndexFileName = "index.html";
    private static final String DefaultTitle  = "XFrameAdmin";
    private static final String DefaultIcon   = "xframe.png";
    
    @Inject
    private ServiceDirectory serviceDir;
    @Inject
    private AuthManager authMgr;
    @Inject
    private BasicManager basicMgr;
    
    private Map<String, Response> caches = new HashMap<>();

    private String root;
    
    private boolean isDirectory;
    
    static class WebFileService implements Service {
        final Supplier<Response> func;
        WebFileService(Supplier<Response> func) {
            this.func = func;
        }
        @Override
        public Response exec(Request req) throws Exception {
            return func.get();
        }
    }
    
    @Override
    public void eventuate() {
        try {
            String path = XPaths.toPath(WebFileHandler.class.getProtectionDomain().getCodeSource().getLocation());
            String xdir = "web/";
            if(path.endsWith(".jar")) {
                this.isDirectory = false;
                this.root = xdir;
                listRelativizeJarFiles(path, xdir).forEach(this::makeHandler);
            } else {
                this.isDirectory = true;
                this.root = new File(path, xdir).getPath();
                XPaths.listRelativizeFiles(root).forEach(this::makeHandler);
            }
            makeIndexHtmlHandler();
            makeExtensionsHandler();
        } catch (Exception e) {
            throw XCaught.throwException(e);
        }
    }

    private void makeExtensionsHandler() {
        basicMgr.getExtensions().forEach(extFile->this.makeHandler0(extFile, extFile, false));
    }

    private void makeIndexHtmlHandler() throws IOException {
        if(XProperties.get(TitlePropKey) != null || XProperties.get(IconPropKey) != null) {
            File file = new File(root, IndexFileName);
            InputStream input = isDirectory ? Files.newInputStream(file.toPath()) : WebFileHandler.class.getClassLoader().getResourceAsStream(toInsidePath(file.getPath()));
            byte[] indexBytes = parseIndexHtml(input);
            makeHandler1(IndexFileName, ()->new FileResponse.Binary(ContentType.HTML, indexBytes));
            if(XProperties.getAsBool(RootRedirect, true)) {
                makeHandler1("", ()->new FileResponse.Binary(ContentType.HTML, indexBytes));
            }
            //icon file handler
            String icon = XProperties.get(IconPropKey);
            if(icon != null) {
                makeHandler0(icon, icon, false);
            }
        }
    }
    private byte[] parseIndexHtml(InputStream input) {
        String content = XStrings.readFrom(input);
        content = content.replace(DefaultTitle, XProperties.get(TitlePropKey, DefaultTitle));
        content = content.replace(DefaultIcon, XProperties.get(IconPropKey, DefaultIcon));
        return content.getBytes();
    }
    //relative path
    private void makeHandler(String p) {
        makeHandler0(p, new File(root, p).getPath(), isDirectory);
    }
    //absolute path
    private void makeHandler0(String uriPath, String filePath, boolean isDirectory) {
        final String uri = toInsidePath(uriPath);  //windows path to url
        final String file = toInsidePath(filePath);//jar file used '/'
        makeHandler1(uri, isDirectory ? ()->makeRespFromDirectory(file) : ()->makeRespFromClassPath(file));
    }

    private String toInsidePath(String path) {
        return path.replace("\\", "/");
    }

    private void makeHandler1(String uriPath, Supplier<Response> func) {
        serviceDir.registService(uriPath, new WebFileService(func), (pp, s1, s2)->{});
        authMgr.addUnblockedPath(uriPath);
    }
    
    private Response makeRespFromDirectory(String path) {
        return new FileResponse.Sys(new File(path));
    }

    private Response makeRespFromClassPath(String path) {
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
                JarEntry entry = entries.nextElement();
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
