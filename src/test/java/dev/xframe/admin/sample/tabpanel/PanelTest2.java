package dev.xframe.admin.sample.tabpanel;

import dev.xframe.admin.view.XColumn;

public class PanelTest2 extends PanelTest {
	
	private String email;
	@XColumn(type=XColumn.type_area)
	private String content;

	public PanelTest2() {
		setRoleId(1002);
	}
	public PanelTest2(String content) {
		this();
		this.content = content;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "PanelTest2 [email=" + email + ", content=" + content + "]";
	}
	
}
