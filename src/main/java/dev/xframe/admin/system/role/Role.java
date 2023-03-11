package dev.xframe.admin.system.role;

import dev.xframe.admin.system.SysEnumKeys;
import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.EShowcase;
import dev.xframe.admin.view.XColumn;

import java.util.ArrayList;
import java.util.List;

public class Role {
    public static final int op_qry = 0;
    public static final int op_add = 1;
    public static final int op_edt = 2;
    public static final int op_del = 4;
    public static final int op_all = 7;

    @XColumn(value="#", show= EShowcase.ListEdel, primary=true)
    private int id;
    
	@XColumn(value="角色", required=true)
	private String name;
	
	@XColumn(value="权限", enumKey= SysEnumKeys.PRIVILEGE_TREE, type= EColumn.Tree, required=true)
	private List<String> authorities = new ArrayList<>();
	
	@XColumn(value="操作", enumKey= SysEnumKeys.ROLE_OPTIONS, type= EColumn.Mult, required=true)
    private int[] options;
	
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
	public int[] getOptions() {
        return options;
    }
	public void setOptions(int[] options) {
        this.options = options;
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
