package dev.xframe.admin.system;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.xframe.admin.system.privilege.Privileges;
import dev.xframe.http.service.Request;
import dev.xframe.injection.Configurator;
import io.netty.handler.codec.http.HttpMethod;

@Configurator
public class AuthContext {
    
    private Map<String, Privileges> tokenPrivileges = new HashMap<>();

    public String regist(Privileges privileges) {
        String token = UUID.randomUUID().toString();
        tokenPrivileges.put(token, privileges);
        return token;
    }

    public boolean isReqIllegal(Request req) {
        String path = req.trimmedPath();
        if(path.startsWith("basic/")) {
            return false;
        }
        
        String token = req.getHeader("x-token");
        Privileges p = tokenPrivileges.get(token);
        if(p != null) {
            if(req.method().equals(HttpMethod.GET)) {
                if(p.contains(path)) return false;
            } else {
                if(p.wholeContains(path)) return false;
            }
        }
        return true;
    }

    public Privileges getPrivileges(Request req) {
        String token = req.getHeader("x-token");
        return tokenPrivileges.get(token);
    }

}
