package dev.xframe.admin.view;

public class VResp {

	private int status;
	private Object data;
	private String text;
	
	VResp(int status, Object data, String text) {
		this.status = status;
		this.data = data;
		this.text = text;
	}
	
	public static VResp succ(Object data) {
		return new VResp(0, data, null);
	}
	
	public static VResp fail(String text) {
		return new VResp(-1, null, text);
	}
	
	public static VResp hint(String text) {
	    return new VResp(-2, null, text);
	}
	public static VResp hint(String text, Object data) {
	    return new VResp(-2, data, text);
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
