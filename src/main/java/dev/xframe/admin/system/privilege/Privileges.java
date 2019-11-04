package dev.xframe.admin.system.privilege;

import java.util.HashSet;
import java.util.Set;

public class Privileges {
    
    private Set<Privilege> wholePrivileges = new HashSet<>();
    
    private Set<Privilege> privileges = new HashSet<>();
    
    public Privileges add(Privilege privilege, boolean readOnly) {
        if(privilege != null) {
            privileges.add(privilege);
            if(!readOnly) {
                wholePrivileges.add(privilege);
            }
        }
        return this;
    }
    
    public boolean contains(String path) {
        return privileges.stream().filter(p->match(p, path)).findAny().isPresent();
    }
    
    public boolean wholeContains(String path) {
        return wholePrivileges.stream().filter(p->match(p, path)).findAny().isPresent();
    }

    private boolean match(Privilege p, String path) {
        return p.getPath().equals("_") || p.getPath().equals(path) || p.getPath().startsWith(path + "/") || path.startsWith(p.getPath() + "/");
    }

}
