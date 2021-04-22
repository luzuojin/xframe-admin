package dev.xframe.admin.system.auth;

import java.util.HashSet;
import java.util.Set;

import dev.xframe.admin.system.privilege.Privilege;
import dev.xframe.admin.system.role.Role;

public class RolePrivileges {
    
    private int option;
    
    private Set<Privilege> privileges = new HashSet<>();
    
    public void setOptions(int[] options) {
        if(options != null)
            for (int opt : options) option |= opt;
    }
    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
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
        return (option & op) == op && privileges.stream().filter(p->match(p, path)).findAny().isPresent();
    }
    public static boolean match(Privilege p, String path) {
        return p.getPath().equals(Privilege.WHOLE_PATH) || p.getPath().equals(path) || p.getPath().startsWith(path + "/") || path.startsWith(p.getPath() + "/");
    }
    
}
