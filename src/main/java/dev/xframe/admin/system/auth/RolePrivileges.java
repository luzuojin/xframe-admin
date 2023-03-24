package dev.xframe.admin.system.auth;

import dev.xframe.admin.system.role.Role;

import java.util.HashSet;
import java.util.Set;

public class RolePrivileges {
    
    private int option;
    
    private Set<Privilege> privileges = new HashSet<>();

    public void setOptions(int[] options) {
        if(options != null)
            for (int opt : options) option |= opt;
    }
    public void addPrivilege(Privilege privilege) {
        if(privilege != null) {
            privileges.add(privilege);
        }
    }
    public boolean creatable(String path) {
        return match(Role.op_add, path);
    }
    public boolean deletable(String path) {
        return match(Role.op_del, path);
    }
    public boolean editable(String path) {
        return match(Role.op_edt, path);
    }
    public boolean readable(String path) {
        return match(Role.op_qry, path);
    }
    public boolean match(int op, String path) {
        return (option & op) == op && privileges.stream().anyMatch(p->p.match(path));
    }

}
