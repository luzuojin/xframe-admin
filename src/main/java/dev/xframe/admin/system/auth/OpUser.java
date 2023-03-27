package dev.xframe.admin.system.auth;

import dev.xframe.admin.system.user.User;
import dev.xframe.utils.XThreadLocal;

import java.util.Optional;

public class OpUser {
    
    private static final XThreadLocal<User> Scoped = new XThreadLocal<>();
    
    public static void set(User user) {
        Scoped.set(user);
    }
    public static User get() {
        return Scoped.get();
    }
    public static String getName() {
        return Optional.ofNullable(Scoped.get()).map(User::getName).orElse(null);
    }

}
