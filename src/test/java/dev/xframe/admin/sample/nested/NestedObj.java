package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XColumn;

public class NestedObj {
	
	@XColumn("sid")
	private int nestId;
	@XColumn("sname")
	private String nestName;
	@XColumn("smail")
	private String email;

	public NestedObj() {
	}
	
	public NestedObj(int nestId, String nestName) {
		this.nestId = nestId;
		this.nestName = nestName;
	}

	public int getNestId() {
		return nestId;
	}

	public void setNestId(int nestId) {
		this.nestId = nestId;
	}

	public String getNestName() {
		return nestName;
	}

	public void setNestName(String nestName) {
		this.nestName = nestName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
