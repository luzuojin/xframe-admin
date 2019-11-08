package dev.xframe.admin.system.oplog;

public class OpLogUser {
    
    private static ThreadLocal<String> opuser = new ThreadLocal<>();
    
    public static void set(String username) {
        opuser.set(username);
    }
    
    public static String clear() {
        String u = opuser.get();
        opuser.remove();
        return u;
    }

}
