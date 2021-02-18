package dev.xframe.admin.system;

import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.service.config.HttpInterceptor;
import dev.xframe.inject.Bean;
import dev.xframe.utils.XStrings;
import dev.xframe.utils.XThreadLocal;

@Bean
public class ClrEnumKeys implements HttpInterceptor {
    
    private static final XThreadLocal<String> dat = new XThreadLocal<>(); 
    public static void add(String clrKey) {
        dat.set(compact(clrKey, get()));
    }
    public static String rm() {
        String ex = get();
        dat.remove();
        return ex;
    }
    public static String get() {
        return dat.get();
    }
    private static String compact(String n, String or) {
        return XStrings.isEmpty(or) ? n : (n + "," + or);
    }
    
    @Override
    public Response intercept(Request req) {
        //do nothing
        return null;
    }
    @Override
    public void afterHandle(Request req, Response resp) {
        String clrEnumKeys;
        if(resp != null && (clrEnumKeys = ClrEnumKeys.rm()) != null) {
            resp.setHeader("Access-Control-Expose-Headers", "*");
            resp.setHeader("ClrEnumKeys", clrEnumKeys);
        }
    }
    
}
