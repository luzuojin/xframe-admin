package dev.xframe.admin.system.auth;

import dev.xframe.utils.XThreadLocal;

public class OpUser {
    
    public static final String LocalUserName = "local";
    
    private static XThreadLocal<String> opuser = new XThreadLocal<>();
    
    public static void set(String username) {
        opuser.set(username);
    }
    
    public static String get() {
        return opuser.get();
    }
    
    public static boolean isLocalUser(String username) {
        return LocalUserName.equals(username);
    }

}
