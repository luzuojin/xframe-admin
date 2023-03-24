package dev.xframe.admin.system;

import dev.xframe.admin.system.auth.Privilege;
import dev.xframe.admin.system.auth.RolePrivileges;
import dev.xframe.admin.system.auth.UserPrivileges;
import dev.xframe.admin.system.role.Role;
import dev.xframe.admin.system.user.User;
import dev.xframe.admin.system.user.UserInterfaces;
import dev.xframe.admin.view.structs.Navi;
import dev.xframe.admin.view.structs.Wrapper;
import dev.xframe.admin.view.values.VEnum;
import dev.xframe.admin.view.values.VTree;
import dev.xframe.inject.Bean;
import dev.xframe.inject.Eventual;
import dev.xframe.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Bean
public class SystemManager implements Eventual {
    
    @Inject
    private SystemRepo sysRepo;
    @Inject
    private BasicManager basicMgr;

    private Map<Integer, Role> roles = new ConcurrentHashMap<>();
    
    private List<Privilege> privileges = new ArrayList<>();
    
    private RolePrivileges basicRole;
    
    @Override
    public void eventuate() {
        privileges.add(Privilege.Admin);

        List<VEnum> ptrees = new ArrayList<>();
        basicMgr.getChapters().forEach(c->{
            VTree ptree = new VTree(c.getPath(), c.getName());
            privileges.add(new Privilege(c.getPath(), c.getName()));
            for (Navi navi : c.getNavis()) {
                if(!(navi instanceof Wrapper)) {
                    privileges.add(new Privilege(c.getPath() + "/" + navi.getPath(), "・"+navi.getName()));
                    ptree.child(new VTree(c.getPath() + "/" + navi.getPath(), navi.getName()));
                }
            }
            ptrees.add(ptree);
        });
        
        List<VEnum> privilegesEnum = privileges.stream().map(p->new VEnum(p.getPath(), p.getName())).collect(Collectors.toList());
        basicMgr.registEnumValue(SysEnumKeys.PRIVILEGES, ()->privilegesEnum);

        sysRepo.fetchRoles().forEach(role->roles.put(role.getId(), role));
        
        basicMgr.registEnumValue(SysEnumKeys.ROLE_LIST, ()->{
            return roles.values().stream().map(role->new VEnum(String.valueOf(role.getId()), role.getName())).collect(Collectors.toList());
        });
        
        basicMgr.registEnumValue(SysEnumKeys.USER_LIST, ()->{
            return sysRepo.fetchUsers().stream().map(user->new VEnum(user.getName())).collect(Collectors.toList());
        });
        
        List<VEnum> roleOptions = Arrays.asList(
                    new VEnum(String.valueOf(Role.op_all), "全部"),
                    new VEnum(String.valueOf(Role.op_add), "新增"),
                    new VEnum(String.valueOf(Role.op_edt), "修改"),
                    new VEnum(String.valueOf(Role.op_del), "删除"),
                    new VEnum(String.valueOf(Role.op_qry), "查询"));
        basicMgr.registEnumValue(SysEnumKeys.ROLE_OPTIONS, ()-> roleOptions);
        
        basicRole = new RolePrivileges();
        basicRole.setOptions(new int[] {Role.op_all});
        basicRole.addPrivilege(new Privilege("basic", "PUBLIC"));

        basicMgr.registEnumValue(SysEnumKeys.PRIVILEGE_TREE, () -> ptrees);

        basicMgr.registEnumValue(SysEnumKeys.USER_TYPES, UserInterfaces::get);
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
            Optional.ofNullable(this.roles.get(role)).ifPresent(r -> p.add(toRolePrivileges(r)));
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


    public List<Role> getRoles() {
        return new ArrayList<>(roles.values());
    }
    public void setRole(Role role) {
        roles.put(role.getId(), role);
    }
    public Role getRole(int role) {
        return roles.get(role);
    }
    public boolean delRole(int role) {
        return roles.remove(role) != null;
    }
    public void addRole(Role role) {
        int id = roles.keySet().stream().mapToInt(Integer::intValue).max().orElse(1000);
        role.setId(++id);
        roles.put(id, role);
        sysRepo.addRole(role);
    }

}
