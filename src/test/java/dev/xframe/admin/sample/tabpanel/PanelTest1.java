package dev.xframe.admin.sample.tabpanel;

public class PanelTest1 extends PanelTest {

	private String name;
	
	public PanelTest1() {
		setRoleId(1001);
	}
	
	public PanelTest1(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PanelTest1 [name=" + name + "]";
	}
	
}
