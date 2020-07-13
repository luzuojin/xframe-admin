package dev.xframe.admin.system.user;

import java.sql.Timestamp;

import dev.xframe.admin.system.XEnumKeys;
import dev.xframe.admin.view.XColumn;

public class User {

	@XColumn(value="用户名", show=XColumn.xor_edit, primary=true)
	private String name;
	@XColumn("手机")
	private String phone;
	@XColumn("邮箱")
	private String email;
	@XColumn(value="密码", show=XColumn.add, type=XColumn.type_pass)
	private String passw;
	@XColumn(value="角色", enumKey=XEnumKeys.ROLE_LIST, type=XColumn.type_mult)
	private int[] roles;
	@XColumn(value="创建时间", show=XColumn.list_edel, type=XColumn.type_datetime)
	private Timestamp ctime;
	
	public User() {
	}
	
	public User(String name, String phone, String email, String passw) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.passw = passw;
		this.newCTime();
	}

	public void newCTime() {
		this.ctime = new Timestamp(System.currentTimeMillis());
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
