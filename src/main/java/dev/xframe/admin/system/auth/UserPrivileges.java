package dev.xframe.admin.system.auth;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import dev.xframe.admin.system.privilege.Privilege;

public class UserPrivileges implements Predicate<String> {
    
    private String username;
    
    private Set<Privilege> wholePrivileges = new HashSet<>();
    
    private Set<Privilege> readPrivileges = new HashSet<>();
    
    private long lastActiveTime;
    
    public UserPrivileges(String username) {
        this.username = username;
        this.lastActiveTime = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public UserPrivileges add(Privilege privilege, boolean readOnly) {
        if(privilege != null) {
            readPrivileges.add(privilege);
            if(!readOnly) {
                wholePrivileges.add(privilege);
            }
        }
        return this;
    }
    
    public boolean readContains(String path) {
        return readPrivileges.stream().filter(p->match(p, path)).findAny().isPresent();
    }
    
    public boolean wholeContains(String path) {
        return wholePrivileges.stream().filter(p->match(p, path)).findAny().isPresent();
    }

    private boolean match(Privilege p, String path) {
        return p.getPath().equals(Privilege.WHOLE_PATH) || p.getPath().equals(path) || p.getPath().startsWith(path + "/") || path.startsWith(p.getPath() + "/");
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

	@Override
	public boolean test(String path) {
		return readContains(path);
	}

}
