package dev.xframe.admin.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.xframe.admin.system.privilege.Privilege;
import dev.xframe.admin.system.privilege.Privileges;
import dev.xframe.admin.system.role.Role;
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.VEnum;
import dev.xframe.injection.Bean;
import dev.xframe.injection.Inject;
import dev.xframe.injection.Loadable;

@Bean
public class SystemContext implements Loadable {
    
    @Inject
    private SystemRepo sysRepo;
    @Inject
    private BasicContext basicCtx;
    
    private List<Role> roles;
    
    private List<Privilege> privileges = new ArrayList<>();
    
    private Map<String, String> privilegeDesc = new HashMap<>();
    
    @Override
    public void load() {
        addPrivilege(new Privilege("全部", "_"));
        basicCtx.getSummary().getChapters().forEach(c->{
            addPrivilege(new Privilege(c.getName(), c.getPath()));
            for (Segment seg : c.getSegments()) {
                addPrivilege(new Privilege(seg.getName(), c.getPath() + "/" + seg.getPath()));
            }
        });
        
        List<VEnum> privilegesEnum = privileges.stream().map(p->new VEnum(p.getPath(), p.getName())).collect(Collectors.toList());
        basicCtx.registEnumValue(EnumKeys.PRIVILEGES, ()->privilegesEnum);
        
        roles = sysRepo.fetchRoles();
        roles.forEach(this::setRoleDesc);
        
        basicCtx.registEnumValue(EnumKeys.ROLE_LIST, ()->{
            return roles.stream().map(role->new VEnum(String.valueOf(role.getId()), role.getName())).collect(Collectors.toList());
        });
    }
    
    void addPrivilege(Privilege p) {
        privileges.add(p);
        privilegeDesc.put(p.getPath(), p.getName());
    }
    
    public Privilege getPrivilege(String path) {
        return privileges.stream().filter(p->p.getPath().equals(path)).findAny().orElse(null);
    }
    
    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public Map<String, String> getPrivilegeDesc() {
        return privilegeDesc;
    }
    
    public Privileges getPrivileges(int[] roles) {
        Privileges p = new Privileges();
        for (int role : roles) {
            Role x = this.roles.stream().filter(r->r.getId() == role).findAny().orElse(null);
            if(x != null) {
                x.getAuthorities().forEach(a->p.add(getPrivilege(a), x.getReadOnly()));
            }
        }
        return p;
    }
    
    public void setRoleDesc(Role role) {
        role.setAuthoritiesDesc(role.getAuthorities().stream().map(a->getPrivilegeDesc().get(a)).collect(Collectors.toList()));
    }
    
    public Role getRole(int role) {
        return roles.stream().filter(r->r.getId() == role).findAny().orElse(null);
    }

    public List<Role> getRoles() {
        return roles;
    }
    
    public void addRole(Role role) {
        int id = roles.stream().mapToInt(Role::getId).max().orElse(1000);
        role.setId(++id);
        setRoleDesc(role);
        roles.add(role);
        sysRepo.addRole(role);
    }

}
