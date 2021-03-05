package dev.xframe.admin.view;

public class VResp {

	private int status;
	private Object data;
	
	public VResp(int status, Object data) {
		this.status = status;
		this.data = data;
	}
	
	public static VResp succ(Object data) {
		return new VResp(0, data);
	}
	
	public static VResp fail(Object data) {
		return new VResp(-1, data);
	}
	
	public static VResp hint(String text) {
	    return new VResp(-2, text);
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
