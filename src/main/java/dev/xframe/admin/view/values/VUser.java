package dev.xframe.admin.view.values;

public class VUser {
	
	private String name;
	private String token;
	private boolean roled;
	
	public VUser(String name, String token, boolean roled) {
	    this.name = name;
	    this.token = token;
		this.roled = roled;
    }
	
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean isRoled() {
		return roled;
	}
	public void setRoled(boolean roled) {
		this.roled = roled;
	}
}
