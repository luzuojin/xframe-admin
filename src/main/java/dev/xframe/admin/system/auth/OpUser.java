package dev.xframe.admin.system.auth;

import dev.xframe.utils.XThreadLocal;

public class OpUser {
    
    private static XThreadLocal<String> opuser = new XThreadLocal<>();
    
    public static void set(String username) {
        opuser.set(username);
    }
    
    public static String get() {
        return opuser.get();
    }

}
