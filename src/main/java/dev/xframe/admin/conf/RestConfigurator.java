package dev.xframe.admin.conf;

import java.io.File;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import dev.xframe.admin.system.AuthContext;
import dev.xframe.admin.view.VResp;
import dev.xframe.http.service.Request;
import dev.xframe.http.service.Response;
import dev.xframe.http.service.config.BodyDecoder;
import dev.xframe.http.service.config.ErrorHandler;
import dev.xframe.http.service.config.FileHandler;
import dev.xframe.http.service.config.RequestInteceptor;
import dev.xframe.http.service.config.RespEncoder;
import dev.xframe.http.service.config.ServiceConfigSetter;
import dev.xframe.injection.Configurator;
import dev.xframe.injection.Inject;

@Configurator
public class RestConfigurator extends ServiceConfigSetter {
	
    @Inject
    private AuthContext authCtx;
    
	static final Logger logger = LoggerFactory.getLogger(RestConfigurator.class);
	
	final SerializerFeature[] features = new SerializerFeature[] {
			SerializerFeature.WriteDateUseDateFormat,
			SerializerFeature.SkipTransientField,
			SerializerFeature.DisableCircularReferenceDetect };
	
	@Override
    public void setErrorHandler(Consumer<ErrorHandler> setter) {
        setter.accept(this::throwableResp);
    }
    
    private Response throwableResp(Request request, Throwable ex) {
        if(!(ex instanceof LogicException)) {
            logger.error("Rest service throws:", ex);
        }
    	return new Response(JSON.toJSONString(VResp.fail(ex.getMessage()), features));
    }
    
    @Override
    public void setBodyDecoder(Consumer<BodyDecoder> setter) {
        setter.accept(this::createObject);
    }
    
    private Object createObject(Class<?> type, byte[] data) {
        return JSON.parseObject(data, type);
    }

    @Override
    public void setRespEncoder(Consumer<RespEncoder> setter) {
        setter.accept(obj -> {
            if (obj instanceof Response)
            	return (Response) obj;
            if (obj instanceof VResp)
            	return new Response(JSON.toJSONString(obj, features));
            
            return new Response(JSON.toJSONString(VResp.succ(obj), features));
        });
    }

	@Override
    public void setIncepetor(Consumer<RequestInteceptor> setter) {
        setter.accept(req->{
            if(authCtx.isReqIllegal(req)) {
                return new Response(JSON.toJSONString(VResp.fail("Permission deny!")));
            }
            return null;
        });
    }

    @Override
	public void setFileHandler(Consumer<FileHandler> setter) {
		setter.accept(new FileHandler() {
			@Override
			public String getPath(String path) {
				return new File(System.getProperty("user.dir") + "/src/main/webapp", path).getPath();
			}
		});
	}
	
}
