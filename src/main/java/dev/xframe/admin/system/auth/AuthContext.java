package dev.xframe.admin.system.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import dev.xframe.admin.system.SystemContext;
import dev.xframe.http.Request;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.task.ScheduledContext;
import dev.xframe.utils.XStrings;
import io.netty.handler.codec.http.HttpMethod;

@Bean
public class AuthContext implements Loadable {
    
    private static final String TOKEN_KEY   = "x-token";
    private static final String REAL_IP_KEY = "X-Real-IP";

    @Inject
    private ScheduledContext scheduledCtx;
    
    @Inject
    private SystemContext sysCtx;
    
    private Map<String, UserPrivileges> tokenMap = new ConcurrentHashMap<>();
    
    private Map<String, UserPrivileges> userMap = new ConcurrentHashMap<>();
    
    private Map<String, Unblocked> unblockedMap = new ConcurrentHashMap<>();
    
    @Override
    public void load() {
        scheduledCtx.period(this::clearExpiryUser, 10, TimeUnit.MINUTES);
        
        addUnblockedPath(Unblocked.of("basic/profile", HttpMethod.POST));
    }
    
    public void clearExpiryUser() {
        tokenMap.keySet().forEach(key->{
            UserPrivileges p = tokenMap.get(key);
            if(System.currentTimeMillis() - p.getLastActiveTime() > 3600_000) {//1hours
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
		if(token == null)
		    token = req.getParam(TOKEN_KEY);
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
        if(!isPrivilegePath(path) && isLocalHost(req)) {
            return false;
        }
        return !hasPrivilege(req.method(), path);
    }
    
    private boolean isPrivilegePath(String path) {
        return sysCtx.getPrivileges().stream().filter(p->p.getPath().equals(path)).findAny().isPresent();
    }

    public boolean isLocalHost(Request req) {
        String remoteHost = getRemoteHost(req);
        if(remoteHost.startsWith("127.0.0.") ||
            remoteHost.startsWith("10.") ||
            remoteHost.startsWith("172.16.") ||
            remoteHost.startsWith("192.168.")) {//本地
            return true;
        }
        return false;
    }
    private String getRemoteHost(Request req) {//优先X-Real-IP (nginx?)
        return XStrings.orElse(req.getHeader(REAL_IP_KEY), req.remoteHost());
    }
    
	public boolean hasPrivilege(HttpMethod method, String path) {
		String username = OpUser.get();
        if(username == null) {
            return false;
        }
        UserPrivileges p = userMap.get(username);
        if(p != null) {
            p.setLastActiveTime(System.currentTimeMillis());
            if(HttpMethod.GET.equals(method))
                return p.readable(path);
            if(HttpMethod.DELETE.equals(method))
                return p.deletable(path);
            if(HttpMethod.PUT.equals(method))
                return p.editable(path);
            if(HttpMethod.POST.equals(method))
                return p.creatable(path);
        }
        return false;
	}

    public UserPrivileges getPrivileges(Request req) {
        return tokenMap.get(getXToken(req));
    }

}
