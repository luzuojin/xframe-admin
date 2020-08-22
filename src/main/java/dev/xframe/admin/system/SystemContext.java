package dev.xframe.admin.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.system.privilege.Privilege;
import dev.xframe.admin.system.role.Role;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.view.Segment;
import dev.xframe.admin.view.VEnum;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

@Bean
public class SystemContext implements Loadable {
    
    @Inject
    private SystemRepo sysRepo;
    @Inject
    private BasicContext basicCtx;
    
    private List<Role> roles;
    
    private List<Privilege> privileges = new ArrayList<>();
    
    @Override
    public void load() {
        privileges.add(Privilege.WHOLE);
        
        basicCtx.getSummary().getChapters().forEach(c->{
            privileges.add(new Privilege(c.getName(), c.getPath()));
            for (Segment seg : c.getSegments()) {
                privileges.add(new Privilege("・"+seg.getName(), c.getPath() + "/" + seg.getPath()));
            }
        });
        
        List<VEnum> privilegesEnum = privileges.stream().map(p->new VEnum(p.getPath(), p.getName())).collect(Collectors.toList());
        basicCtx.registEnumValue(XEnumKeys.PRIVILEGES, ()->privilegesEnum);
        
        roles = sysRepo.fetchRoles();
        
        basicCtx.registEnumValue(XEnumKeys.ROLE_LIST, ()->{
            return roles.stream().map(role->new VEnum(String.valueOf(role.getId()), role.getName())).collect(Collectors.toList());
        });
        
        basicCtx.registEnumValue(XEnumKeys.USER_LIST, ()->{
            return sysRepo.fetchUsers().stream().map(user->new VEnum(user.getName())).collect(Collectors.toList());
        });
    }
    
    void addPrivilege(Privilege p) {
        privileges.add(p);
    }
    
    public Privilege getPrivilege(String path) {
        return privileges.stream().filter(p->p.getPath().equals(path)).findAny().orElse(null);
    }
    
    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public UserPrivileges getPrivileges(User user) {
        UserPrivileges p = new UserPrivileges(user.getName());
        for (int role : user.getRoles()) {
            Role x = this.roles.stream().filter(r->r.getId() == role).findAny().orElse(null);
            if(x != null) {
                x.getAuthorities().forEach(a->p.add(getPrivilege(a), x.getReadOnly()));
            }
        }
        return p;
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
        roles.add(role);
        sysRepo.addRole(role);
    }

}
