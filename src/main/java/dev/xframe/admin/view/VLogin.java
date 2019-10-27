package dev.xframe.admin.view;

public class VLogin {
	
	private String name;
	private String passw;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassw() {
		return passw;
	}
	public void setPassw(String passw) {
		this.passw = passw;
	}
	
	@Override
	public String toString() {
		return "[name=" + name + ", passw=" + passw + "]";
	}

}
