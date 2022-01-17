package dev.xframe.admin.system.auth;

import dev.xframe.utils.XThreadLocal;

public class OpUser {
    
    public static final String LocalUserName = "local";
    
    private static XThreadLocal<String> OpUser = new XThreadLocal<>();
    
    public static void set(String username) {
        OpUser.set(username);
    }
    
    public static String get() {
        return OpUser.get();
    }
    
    public static boolean isLocalUser(String username) {
        return LocalUserName.equals(username);
    }

}
