package dev.xframe.admin.system.role;

import dev.xframe.admin.system.SysEnumKeys;
import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.EShowcase;
import dev.xframe.admin.view.XColumn;
import dev.xframe.inject.Providable;

import java.util.ArrayList;
import java.util.List;

@Providable
public class Role {

    public static final int op_qry = 0;
    public static final int op_add = 1;
    public static final int op_edt = 2;
    public static final int op_del = 4;
    public static final int op_all = 7;

    @XColumn(value="#", show= EShowcase.ListEdel, primary=true)
	protected int id;
	@XColumn(value="角色", required=true)
	protected String name;
	@XColumn(value="权限", enumKey= SysEnumKeys.PRIVILEGE_TREE, type= EColumn.Tree, required=true)
	protected List<String> authorities = new ArrayList<>();
	@XColumn(value="权限反选", type= EColumn.Bool)
	protected boolean authReversed;
	@XColumn(value="操作", enumKey= SysEnumKeys.ROLE_OPTIONS, type= EColumn.Mult, required=true)
	protected int[] options;
	
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
	public boolean getAuthReversed() {
		return authReversed;
	}
	public void setAuthReversed(boolean authReversed) {
		this.authReversed = authReversed;
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
