package dev.xframe.admin.system.auth;

import dev.xframe.admin.system.role.Role;

import java.util.HashSet;
import java.util.Set;

public class RolePrivileges {
    
    private int option;
    
    private Set<Privilege> privileges = new HashSet<>();

    private boolean reversed;

    /**
     * 所以路径中最外层的Parent路径.  反选根据该层路径进行.
     * 例:
     *  x/y/a x/y/b reversed
     *      x/y/c authed
     *      x/z/c not authed
     */
    private String reversedPath;

    public void setOptions(int[] options) {
        if(options != null)
            for (int opt : options) option |= opt;
    }
    public void addPrivilege(Privilege privilege) {
        if(privilege != null) {
            privileges.add(privilege);
            setReversedPath(privilege.getPath());
        }
    }
    private void setReversedPath(String path) {
        if(this.reversedPath == null) {
            String[] pathSubs = path.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pathSubs.length - 1; i++) {
                sb.append(pathSubs[i]).append("/");
            }
            this.reversedPath = sb.toString();
        }
    }
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
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
        if((option & op) != op) {
            return false;
        }
        if(reversed){
            return path.startsWith(reversedPath) && privileges.stream().noneMatch(p->p.match(path));
        }
        return privileges.stream().anyMatch(p->p.match(path));
    }

}
