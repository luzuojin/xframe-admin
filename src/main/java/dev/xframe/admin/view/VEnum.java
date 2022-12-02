package dev.xframe.admin.view;

public class VEnum {
	
	private String id;
	private String text;
	
	public VEnum(String id) {
		this(id, id);
	}
	public VEnum(Number id, String text) {
		this(id.toString(), text);
	}
	public VEnum(String id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

}
