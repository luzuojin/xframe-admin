package dev.xframe.admin.system.auth;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class UserPrivileges implements Predicate<String> {
    
    private String username;
    
    private Set<RolePrivileges> rolePrivileges = new HashSet<>();
    
    private long lastActiveTime;
    
    private String token;
    
    public UserPrivileges(String username) {
        this.username = username;
        this.lastActiveTime = System.currentTimeMillis();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public UserPrivileges add(RolePrivileges rolePrivilege) {
        if(rolePrivilege != null) {
            rolePrivileges.add(rolePrivilege);
        }
        return this;
    }
    
    public boolean readable(String path) {
        return rolePrivileges.stream().filter(rp->rp.readable(path)).findAny().isPresent();
    }
    public boolean creatable(String path) {
        return rolePrivileges.stream().filter(rp->rp.creatable(path)).findAny().isPresent();
    }
    public boolean editable(String path) {
        return rolePrivileges.stream().filter(rp->rp.editable(path)).findAny().isPresent();
    }
    public boolean deletable(String path) {
        return rolePrivileges.stream().filter(rp->rp.deletable(path)).findAny().isPresent();
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	@Override
	public boolean test(String path) {//read
		return readable(path);
	}

}
