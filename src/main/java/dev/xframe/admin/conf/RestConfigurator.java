package dev.xframe.admin.conf;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.admin.view.VResp;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.service.config.BodyDecoder;
import dev.xframe.http.service.config.ErrorHandler;
import dev.xframe.http.service.config.HttpInterceptor;
import dev.xframe.http.service.config.RespEncoder;
import dev.xframe.http.service.config.ServiceConfigSetter;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;

@Configurator
public class RestConfigurator extends ServiceConfigSetter {
	
    @Inject
    private AuthContext authCtx;
    @Inject
    private HttpInterceptor httpInterceptor;
    
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
    	return Response.of(JSON.toJSONString(VResp.fail(ex.getMessage()), features));
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
            	return Response.of(JSON.toJSONString(obj, features));
            
            return Response.of(JSON.toJSONString(VResp.succ(obj), features));
        });
    }

	@Override
    public void setInterceptor(Consumer<HttpInterceptor> setter) {
        setter.accept(httpInterceptor);
    }

}
