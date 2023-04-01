package dev.xframe.admin.system.user;

import dev.xframe.admin.system.SysEnumKeys;
import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.EShowcase;
import dev.xframe.admin.view.XColumn;
import dev.xframe.inject.Providable;

import java.sql.Timestamp;

@Providable
public class User {

	@XColumn(value="用户名", show= EShowcase.xorEdit, primary=true, required=true)
	protected String name;
	@XColumn(value="手机", required=true)
	protected String phone;
	@XColumn(value="邮箱", required=true)
	protected String email;
	@XColumn(value="密码", show= EShowcase.Add, type= EColumn.Pass, required=true)
	protected String passw;
	@XColumn(value="角色", enumKey= SysEnumKeys.ROLE_LIST, type= EColumn.Mult, required=true)
	protected int[] roles;
	@XColumn(value="类型", show= EShowcase.ListEdel, enumKey = SysEnumKeys.USER_TYPES)
	protected int type;
	@XColumn(value="创建时间", show= EShowcase.ListEdel, type= EColumn.Datetime)
	protected Timestamp ctime;
	
	public User() {
	}
	
	public User(String name, String phone, String email, int type) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.type = type;
		this.roles = new int[0];
		this.passw = "*";
		this.newCTime();
	}

	public void newCTime() {
		this.ctime = new Timestamp(System.currentTimeMillis());
	}

	public boolean roled() {
		return this.roles != null && this.roles.length > 0;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassw() {
		return passw;
	}
	public void setPassw(String passw) {
		this.passw = passw;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Timestamp getCtime() {
		return ctime;
	}
	public void setCtime(Timestamp ctime) {
		this.ctime = ctime;
	}
    public int[] getRoles() {
        return roles;
    }
    public void setRoles(int[] roles) {
        this.roles = roles;
    }

	//for extends
	public boolean readable(String path) {
		return accessable(path);
	}
	public boolean creatable(String path) {
		return accessable(path);
	}
	public boolean editable(String path) {
		return accessable(path);
	}
	public boolean deletable(String path) {
		return accessable(path);
	}
	public boolean accessable(String path) {
		return false;
	}

	public boolean isTrusted() {
		return isTrusted(name);
	}
	public static boolean isTrusted(String name) {
		return "local".equals(name);
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
