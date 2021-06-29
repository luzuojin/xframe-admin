package dev.xframe.admin.conf;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.xframe.admin.system.auth.AuthContext;
import dev.xframe.admin.utils.JsonHelper;
import dev.xframe.admin.view.VResp;
import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.config.BodyDecoder;
import dev.xframe.http.config.ErrorHandler;
import dev.xframe.http.config.HttpConfigSetter;
import dev.xframe.http.config.RespEncoder;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;

@Bean
public class HttpConfigurator extends HttpConfigSetter {

    static final Logger logger = LoggerFactory.getLogger(HttpConfigurator.class);

    @Inject
    private AuthContext authCtx;

    @Override
    public void setErrorHandler(Consumer<ErrorHandler> setter) {
        setter.accept(this::throwableResp);
    }

    private Response throwableResp(Request request, Throwable ex) {
        if(!(ex instanceof LogicException)) {
            logger.error("Rest service throws:", ex);
        }
        return Response.of(JsonHelper.toJSONString(VResp.fail(ex.getMessage())));
    }

    @Override
    public void setBodyDecoder(Consumer<BodyDecoder> setter) {
        setter.accept(this::createObject);
    }

    private Object createObject(Class<?> type, byte[] data) {
        return JsonHelper.parseObject(data, type);
    }

    @Override
    public void setRespEncoder(Consumer<RespEncoder> setter) {
        setter.accept(obj -> {
            if (obj instanceof Response)
                return (Response) obj;
            if (obj instanceof VResp)
                return Response.of(JsonHelper.toJSONString(obj));

            return Response.of(JsonHelper.toJSONString(VResp.succ(obj)));
        });
    }

}
