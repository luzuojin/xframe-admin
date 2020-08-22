package dev.xframe.admin.system.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.xframe.http.Request;
import dev.xframe.inject.Configurator;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.task.Task;
import dev.xframe.task.TaskContext;
import io.netty.handler.codec.http.HttpMethod;

@Configurator
public class AuthContext implements Loadable {
    
    private static final String TOKEN_KEY = "x-token";

	@Inject
    private TaskContext taskCtx;
    
    private Map<String, UserPrivileges> tokenMap = new ConcurrentHashMap<>();
    
    private Map<String, UserPrivileges> userMap = new ConcurrentHashMap<>();
    
    private Map<String, Unblocked> unblockedMap = new ConcurrentHashMap<>();
    
    @Override
    public void load() {
        taskCtx.setup(1);
        taskCtx.regist(Task.period("token-expiry", 10, this::clearExpiryUser));
        
        addUnblockedPath(Unblocked.of("basic/profile", HttpMethod.POST));
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
    	addUnblockedPath(Unblocked.of(p));
    }
    public void addUnblockedPath(Unblocked u) {
    	this.unblockedMap.put(u.path, u);
    }

    public String regist(UserPrivileges privileges) {
        OpUser.set(privileges.getUsername());
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, privileges);
        userMap.put(privileges.getUsername(), privileges);
        return token;
    }
    
    public void unregist(Request req, String name) {
    	tokenMap.remove(getXToken(req));
    	userMap.remove(name);
	}
    
    public String getAuthUsername(Request req) {
    	String token = getXToken(req);
        if(token != null) {
            UserPrivileges p = tokenMap.get(token);
            if(p != null) return p.getUsername();
        }
        return null;
    }

	private String getXToken(Request req) {
		String token = req.getHeader(TOKEN_KEY);
		if(token == null) token = req.getParam(TOKEN_KEY);
		return token;
	}
    
    public boolean unblockedMatch(HttpMethod method, String path) {
    	Unblocked unblocked = unblockedMap.get(path);
		return unblocked != null && unblocked.match(method);
	}
    
    //判断请求是否非法
    public boolean isReqIllegal(Request req) {
        String path = req.xpath();
        if(unblockedMatch(req.method(), path)) {
            return false;
        }
        if(req.remoteHost().startsWith("127.")) {//本地
            return false;
        }
        return !hasPrivilege(req.method(), path);
    }

	public boolean hasPrivilege(HttpMethod method, String path) {
		String username = OpUser.get();
        if(username == null) {
            return false;
        }
        UserPrivileges p = userMap.get(username);
        if(p != null) {
            if(method.equals(HttpMethod.GET)) {
                if(p.readContains(path)) return true;
            } else {
                if(p.wholeContains(path)) return true;
            }
        }
        return false;
	}

    public UserPrivileges getPrivileges(Request req) {
        return tokenMap.get(getXToken(req));
    }

}
