package dev.xframe.admin.system.auth;

import dev.xframe.admin.system.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class UserPrivileges implements Predicate<String> {
    
    private User user;
    
    private Set<RolePrivileges> rolePrivileges = new HashSet<>();
    
    private long lastActiveTime;
    
    private String token;
    
    public UserPrivileges(User user) {
        this.user = user;
        this.lastActiveTime = System.currentTimeMillis();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getUserName() {
        return user.getName();
    }

    public UserPrivileges add(RolePrivileges rolePrivilege) {
        if(rolePrivilege != null) {
            rolePrivileges.add(rolePrivilege);
        }
        return this;
    }
    
    public boolean readable(String path) {
        return rolePrivileges.stream().anyMatch(rp->rp.readable(path)) || user.readable(path);
    }
    public boolean creatable(String path) {
        return rolePrivileges.stream().anyMatch(rp->rp.creatable(path)) || user.creatable(path);
    }
    public boolean editable(String path) {
        return rolePrivileges.stream().anyMatch(rp->rp.editable(path)) || user.editable(path);
    }
    public boolean deletable(String path) {
        return rolePrivileges.stream().anyMatch(rp->rp.deletable(path)) || user.deletable(path);
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
