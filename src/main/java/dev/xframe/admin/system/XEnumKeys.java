package dev.xframe.admin.system;

import dev.xframe.http.Request;
import dev.xframe.http.Response;
import dev.xframe.http.config.HttpListener;
import dev.xframe.inject.Bean;
import dev.xframe.utils.XStrings;
import dev.xframe.utils.XThreadLocal;

@Bean
public class XEnumKeys implements HttpListener {
    
    static final XThreadLocal<String> Keys = new XThreadLocal<>();

    public static void clear(String key) {
        Keys.set(concat(key, Keys.get()));
    }
    static String concat(String n, String or) {
        return XStrings.isEmpty(or) ? n : (n + "," + or);
    }

    static String rm() {
        String ex = Keys.get();
        Keys.remove();
        return ex;
    }

    @Override
    public void onAccessComplete(Request req, Response resp) {
        String clearKeys;
        if(resp != null && (clearKeys = rm()) != null) {
            resp.setHeader("ClrEnumKeys", clearKeys);
        }
    }
    
}
