package dev.xframe.admin.system;

import dev.xframe.admin.system.auth.RolePrivileges;
import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.system.privilege.Privilege;
import dev.xframe.admin.system.role.Role;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.view.structs.Navi;
import dev.xframe.admin.view.structs.Symbol;
import dev.xframe.admin.view.values.VEnum;
import dev.xframe.admin.view.values.VTree;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class SystemManager implements Loadable {
    
    @Inject
    private SystemRepo sysRepo;
    @Inject
    private BasicManager basicMgr;

    private List<Role> roles;
    
    private List<Privilege> privileges = new ArrayList<>();
    
    private RolePrivileges basicRole;
    
    @Override
    public void load() {
        privileges.add(Privilege.WHOLE);

        List<VEnum> ptrees = new ArrayList<>();
        ptrees.add(new VTree("_", "全部"));
        basicMgr.getCatalog().getChapters().forEach(c->{
            VTree ptree = new VTree(c.getPath(), c.getName());
            privileges.add(new Privilege(c.getName(), c.getPath()));
            for (Navi navi : c.getNavis()) {
                if(!(navi instanceof Symbol)) {
                    privileges.add(new Privilege("・"+navi.getName(), c.getPath() + "/" + navi.getPath()));
                    ptree.child(new VTree(c.getPath() + "/" + navi.getPath(), navi.getName()));
                }
            }
            ptrees.add(ptree);
        });
        
        List<VEnum> privilegesEnum = privileges.stream().map(p->new VEnum(p.getPath(), p.getName())).collect(Collectors.toList());
        basicMgr.registEnumValue(SysEnumKeys.PRIVILEGES, ()->privilegesEnum);

        roles = sysRepo.fetchRoles();
        
        basicMgr.registEnumValue(SysEnumKeys.ROLE_LIST, ()->{
            return roles.stream().map(role->new VEnum(String.valueOf(role.getId()), role.getName())).collect(Collectors.toList());
        });
        
        basicMgr.registEnumValue(SysEnumKeys.USER_LIST, ()->{
            return sysRepo.fetchUsers().stream().map(user->new VEnum(user.getName())).collect(Collectors.toList());
        });
        
        List<VEnum> roleOptions = Arrays.asList(
                    new VEnum(String.valueOf(Role.op_all), "全"),
                    new VEnum(String.valueOf(Role.op_add), "写"),
                    new VEnum(String.valueOf(Role.op_edt), "改"),
                    new VEnum(String.valueOf(Role.op_del), "删"),
                    new VEnum(String.valueOf(Role.op_qry), "读"));
        basicMgr.registEnumValue(SysEnumKeys.ROLE_OPTIONS, ()-> roleOptions);
        
        basicRole = new RolePrivileges();
        basicRole.setOptions(new int[] {Role.op_all});
        basicRole.addPrivilege(new Privilege("Basic", "basic"));

        basicMgr.registEnumValue(SysEnumKeys.PRIVILEGE_TREE, () -> ptrees);
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
            this.roles.stream()
                    .filter(r -> r.getId() == role)
                    .findAny()
                    .ifPresent(r -> p.add(toRolePrivileges(r)));
        }
        p.add(basicRole);
        return p;
    }
    private RolePrivileges toRolePrivileges(Role x) {
        RolePrivileges p = new RolePrivileges();
        p.setOptions(x.getOptions());
        x.getAuthorities().forEach(a->p.addPrivilege(getPrivilege(a)));
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
