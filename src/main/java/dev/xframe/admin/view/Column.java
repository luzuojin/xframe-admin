package dev.xframe.admin.view;

import dev.xframe.utils.XStrings;

public class Column {
	
	private int type;
	private String key;
	private String hint;
	private String enumKey;
	
	private int show;
	
	public Column(String key, String hint, int type, String enumKey) {
		this(key, hint, type, enumKey, XColumn.full);
	}
	
	public Column(String key, String hint, int type, String enumKey, int show) {
		this.key = key;
		this.hint = hint;
		this.type = type;
		this.enumKey = enumKey;
		this.show = show;
		
		if(!XStrings.isEmpty(enumKey) && this.type == 0)
			this.type = XColumn.type_enum;
	}

	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getEnumKey() {
		return enumKey;
	}

	public void setEnumKey(String enumKey) {
		this.enumKey = enumKey;
	}

}
