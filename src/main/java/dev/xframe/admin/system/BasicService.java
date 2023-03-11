package dev.xframe.admin.system;

import dev.xframe.admin.system.auth.AuthManager;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.request.MultiPart;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XCaught;
import io.netty.handler.codec.http.multipart.FileUpload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Rest("basic")
public class BasicService {
	
	@Inject
	private BasicManager basicMgr;
	@Inject
	private AuthManager authMgr;
	@Inject
	private FileTransferHandler ftHandler;
	
	@HttpMethods.GET("catalog")
	public Object catalog(Request req) {
		return basicMgr.getCatalog(authMgr.getPrivileges(req));
	}
	
	@HttpMethods.GET("enum")
	public Object getEnum(@HttpArgs.Param String key) {
		return basicMgr.getEnumValue(key);
	}
    
    @HttpMethods.POST("upload")
    public Object upload(@HttpArgs.Body MultiPart mp) throws IOException {
    	FileUpload fu = (FileUpload) mp.getBodyHttpData("file");
    	File tmpFile = fu.isInMemory() ? createTmpFile(fu) : fu.getFile();
    	File target = ftHandler.upload(fu.getFilename(), tmpFile);
    	return target.getName();
    }
    
    private File createTmpFile(FileUpload fu) {
        try {
            File file = Files.createTempFile(fu.getFilename(), ".tmp").toFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(fu.get());
            out.flush();
            out.close();
            return file;
        } catch (IOException e) {
            throw XCaught.throwException(e);
        }
    }

    @HttpMethods.GET("preview")
    public Object preview(@HttpArgs.Param String name) {
    	return Response.of(ftHandler.preview(name));
    }

}
