package dev.xframe.admin.system.role;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.admin.system.XEnumKeys;
import dev.xframe.admin.view.XColumn;

public class Role {

    @XColumn(value="#", show=XColumn.list_edel, primary=true)
    private int id;
    
	@XColumn(value="角色")
	private String name;
	
	@XColumn(value="权限", enumKey=XEnumKeys.PRIVILEGES, type=XColumn.type_mult)
	private List<String> authorities = new ArrayList<>();
	
	@XColumn(value="只读")
	private boolean readOnly;
	
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}
	public boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
