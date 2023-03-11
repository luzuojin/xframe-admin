package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.XColumn;

public class PanelTest {
	
	public static final String PANEL_ROLE_KEY = "panel_role_key";

	@XColumn(value="角色", enumKey=PANEL_ROLE_KEY, cacheable=true)
	private int roleId;
	
	@XColumn(value="图片", type= EColumn.Imag)
	private String file;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
}
