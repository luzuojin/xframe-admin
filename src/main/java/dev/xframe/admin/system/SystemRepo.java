package dev.xframe.admin.system;

import dev.xframe.admin.store.StoreKey;
import dev.xframe.admin.system.role.Role;
import dev.xframe.admin.system.settings.Setting;
import dev.xframe.admin.system.user.User;
import dev.xframe.inject.Repository;
import dev.xframe.jdbc.TypeQuery;
import dev.xframe.utils.XOptional;

import java.util.List;

@Repository
public class SystemRepo {
	
	private TypeQuery<User> userQuery = 
			TypeQuery.newBuilder(User.class).setTable(StoreKey.DAT, "T_USER").build();
	
	private TypeQuery<Role> roleQuery =
			TypeQuery.newBuilder(Role.class).setTable(StoreKey.DAT, "T_ROLE").build();

	private TypeQuery<Setting> settingQuery =
			TypeQuery.newBuilder(Setting.class).setTable(StoreKey.DAT, "T_VALUES").build();

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


	public List<Setting> fetchSettings() {
		return settingQuery.fetchAll();
	}
	public Setting fetchSetting(String key) {
		return settingQuery.fetchOne(key);
	}
	public void addSetting(Setting settting) {
		settingQuery.insert(settting);
	}
	public void saveSetting(Setting setting) {
		settingQuery.update(setting);
	}
	public void deleteSetting(Setting setting) {
		settingQuery.delete(setting);
	}
	public String getSetting(String key) {
		return getSetting(key, null);
	}
	public String getSetting(String key, String defaultVal) {
		return XOptional.map(fetchSetting(key), Setting::getVal, defaultVal);
	}

}
