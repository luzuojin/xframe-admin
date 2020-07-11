package dev.xframe.admin.system.auth;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.xframe.admin.system.oplog.OpLogUser;
import dev.xframe.http.Request;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.task.Task;
import dev.xframe.task.TaskContext;
import io.netty.handler.codec.http.HttpMethod;

@Configurator
public class AuthContext implements Loadable {
    
    @Inject
    private TaskContext taskCtx;
    
    private Map<String, UserPrivileges> tokenMap = new ConcurrentHashMap<>();
    
    private Set<String> unblockedPathes = new HashSet<>();
    
    @Override
    public void load() {
        taskCtx.setup(1);
        taskCtx.regist(Task.period("token-expiry", 10, this::clearExpiryUser));
        
        unblockedPathes.add("basic/summary");
        unblockedPathes.add("basic/enum");
        unblockedPathes.add("basic/profile");
    }
    
    public void clearExpiryUser() {
        tokenMap.keySet().forEach(key->{
            UserPrivileges p = tokenMap.get(key);
            if(System.currentTimeMillis() - p.getLastActiveTime() > 360_0_000) {//10hours
                tokenMap.remove(key);
            }
        });
    }
    
    public void addUnblockedPath(String p) {
        this.unblockedPathes.add(p);
    }

    public String regist(UserPrivileges privileges) {
        OpLogUser.set(privileges.getUsername());
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, privileges);
        return token;
    }

    public boolean isReqIllegal(Request req) {
        String path = req.xpath();
        if(unblockedPathes.contains(path)) {
            return false;
        }
        
        String token = req.getHeader("x-token");
        if(token == null) {
            return true;
        }
        
        UserPrivileges p = tokenMap.get(token);
        if(p != null) {
            OpLogUser.set(p.getUsername());
            if(req.method().equals(HttpMethod.GET)) {
                if(p.contains(path)) return false;
            } else {
                if(p.wholeContains(path)) return false;
            }
        }
        
        if(req.remoteHost().startsWith("127.")) {
            return false;
        }
        return true;
    }

    public UserPrivileges getPrivileges(Request req) {
        String token = req.getHeader("x-token");
        return tokenMap.get(token);
    }

}
