package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XColumn;

public class NestedObj2 extends NestedObj {
	
	@XColumn("desc")
	private String nestDesc;
	
	public NestedObj2() {
	}
	
	public NestedObj2(int nestId, String nestName, String nestDesc) {
		super(nestId, nestName);
		this.nestDesc = nestDesc;
	}

	public String getNestDesc() {
		return nestDesc;
	}
	public void setNestDesc(String nestDesc) {
		this.nestDesc = nestDesc;
	}

}
