package dev.xframe.admin.system.auth;

import dev.xframe.admin.system.SystemManager;
import dev.xframe.admin.system.user.User;
import dev.xframe.http.Request;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;
import dev.xframe.task.scheduled.ScheduledExecutor;
import dev.xframe.utils.XProperties;
import dev.xframe.utils.XStrings;
import io.netty.handler.codec.http.HttpMethod;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Bean
public class AuthManager implements Loadable {

    private static final String TOKEN_KEY   = "X-Token";
    private static final String REAL_IP_KEY = "X-Real-IP";
    //转发了之后才使用x-real-ip代替remotehost
    private static final boolean REDIRECTED = XProperties.getAsBool("xframe.admin.redirected", false);

    @Inject
    private SystemManager sysMgr;

    private Map<String, UserPrivileges> tokenMap = new ConcurrentHashMap<>();

    private Map<String, UserPrivileges> userMap = new ConcurrentHashMap<>();

    private Map<String, Unblocked> unblockedMap = new ConcurrentHashMap<>();

    @Override
    public void load() {
        ScheduledExecutor.period(this::clearExpiryUser, 10, TimeUnit.MINUTES);

        addUnblockedPath(Unblocked.of("basic/profile", HttpMethod.POST));
        addUnblockedPath(Unblocked.of("basic/extensions", HttpMethod.GET));
    }

    public void clearExpiryUser() {
        tokenMap.keySet().forEach(key->{
            UserPrivileges p = tokenMap.get(key);
            if(!p.getUser().isTrusted() && isInactive(p.getLastActiveTime())) {//1hours
                tokenMap.remove(key);
            }
        });
    }

    public boolean isInactive(long lastActiveTime) {
        return System.currentTimeMillis() - lastActiveTime > TimeUnit.HOURS.toMillis(12);
    }

    public void addUnblockedPath(String p) {
        addUnblockedPath(Unblocked.of(p));
    }
    public void addUnblockedPath(Unblocked u) {
        this.unblockedMap.put(u.path, u);
    }

    public String regist(UserPrivileges privileges) {
        User user = privileges.getUser();
        OpUser.set(user);
        //del old token 
        UserPrivileges ex = userMap.put(user.getName(), privileges);
        if(ex != null && ex.getToken() != null && !user.isTrusted())//local user不顶号
            tokenMap.remove(ex.getToken());
        //add new token
        String token = UUID.randomUUID().toString();
        privileges.setToken(token);
        privileges.setHost(OpHost.get());
        tokenMap.put(token, privileges);
        return token;
    }

    public void unregist(Request req, String name) {
        tokenMap.remove(getXToken(req));
        userMap.remove(name);
    }

    public User getAuthUser(Request req) {
        String token = getXToken(req);
        if(token != null) {
            UserPrivileges p = tokenMap.get(token);
            if(p != null) return p.getUser();
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
        //if(!isPrivilegePath(path) && isLocalHost(req)) {
        //    return false;
        //}
        return !hasPrivilege(req.method(), path);
    }

    private boolean isPrivilegePath(String path) {
        return sysMgr.getPrivileges().stream().anyMatch(p->p.getPath().equals(path));
    }

    public boolean isLocalHost(Request req) {
        try {
            return isLocalAddress(req.address()) && isLocalAddress(InetAddress.getByName(getRemoteHost(req)));
        } catch (UnknownHostException e) {//ignore
            return false;
        }
    }
    private boolean isLocalAddress(InetAddress addr) {
        return addr.isSiteLocalAddress() || addr.isLoopbackAddress();
    }
    public static String getRemoteHost(Request req) {//优先X-Real-IP (nginx?)
        return REDIRECTED ? XStrings.orElse(req.getHeader(REAL_IP_KEY), req.remoteHost()) : req.remoteHost();
    }
    public boolean hasPrivilege(HttpMethod method, String path) {
        String username = OpUser.getName();
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
