package dev.xframe.admin.system.auth;

import dev.xframe.utils.XThreadLocal;

public class OpHost {
    
    private static XThreadLocal<String> OpHost = new XThreadLocal<>();
    
    public static void set(String host) {
        OpHost.set(host);
    }
    
    public static String get() {
        return OpHost.get();
    }
    
}
