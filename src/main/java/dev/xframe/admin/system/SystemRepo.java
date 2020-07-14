package dev.xframe.admin.system;

import java.util.List;


import dev.xframe.admin.store.StoreKey;
import dev.xframe.admin.system.role.Role;
import dev.xframe.admin.system.server.Server;
import dev.xframe.admin.system.user.User;
import dev.xframe.inject.Repository;
import dev.xframe.jdbc.TypeQuery;

@Repository
public class SystemRepo {
	
	private TypeQuery<User> userQuery = 
			TypeQuery.newBuilder(User.class).setTable(StoreKey.DAT, "T_USER").build();
	
	private TypeQuery<Role> roleQuery =
			TypeQuery.newBuilder(Role.class).setTable(StoreKey.DAT, "T_ROLE").build();
	
	private TypeQuery<Server> serverQuery =
			TypeQuery.newBuilder(Server.class).setTable(StoreKey.DAT, "T_SERVER").build();
	
	public List<User> fetchUsers() {
		return userQuery.fetchAll();
	}
	
	public User fetchUser(String userName) {
		return userQuery.fetchOne(userName);
	}
	
	public void addUser(User user) {
		userQuery.insert(user);
	}
	
	public void saveUser(User user) {
		userQuery.update(user);
	}
	
	public void deleteUser(User user) {
        userQuery.delete(user);
    }
	
	
	public List<Role> fetchRoles() {
		return roleQuery.fetchAll();
	}
	
	public Role fetchRole(int roleId) {
		return roleQuery.fetchOne(roleId);
	}
	
	public void addRole(Role role) {
		roleQuery.insert(role);
	}
	
	public void saveRole(Role role) {
		roleQuery.update(role);
	}

    public void deleteRole(Role role) {
        roleQuery.delete(role);
    }
    
    public List<Server> fetchServers(){
    	return serverQuery.fetchAll();
    }
    
    public Server fetchServer(int serverId){
    	return serverQuery.fetchOne(serverId);
    }
    
    public void addServer(Server server) {
    	serverQuery.insert(server);
    }
    
    public void delServer(Server server) {
    	serverQuery.delete(server);
    }
    
    public void saveServer(Server server) {
    	serverQuery.update(server);
    }

}
