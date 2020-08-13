package dev.xframe.admin.system;

import java.io.File;
import java.io.IOException;

import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.admin.system.auth.OpUser;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.request.MultiPart;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;
import io.netty.handler.codec.http.multipart.FileUpload;

@Rest("basic")
public class BasicService {
	
	@Inject
	private BasicContext basicCtx;
	@Inject
	private AuthContext authCtx;
	@Inject
	private FileTransferHandler ftHandler;
	
	@HttpMethods.GET("summary")
	public Object summary(Request req) {
		return basicCtx.getSummary(authCtx.getPrivileges(req));
	}
	
	@HttpMethods.GET("enum")
	public Object getEnum(@HttpArgs.Param String key) {
		return basicCtx.getEnumValue(OpUser.get(), key);
	}
    
    @HttpMethods.POST("upload")
    public Object upload(@HttpArgs.Body MultiPart mp) throws IOException {
    	FileUpload fu = (FileUpload) mp.getBodyHttpData("file");
    	File target = ftHandler.upload(fu.getFilename(), fu.getFile());
    	return target.getName();
    }
    
    @HttpMethods.GET("preview")
    public Object preview(@HttpArgs.Param String name) {
    	return Response.of(ftHandler.preview(name));
    }

}
