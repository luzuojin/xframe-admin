package dev.xframe.admin.view;

import dev.xframe.utils.XStrings;

public class Column {
	
	private int type;
	private String key;
	private String hint;
	private String enumKey;
	
	private int show;
	private boolean primary;
	
	public Column(String key) {
		this(key, key, XColumn.type_text, "", XColumn.full, false);
	}
	public Column(String key, XColumn xc, Class<?> jType) {
		this(key, xc, byJavaType(xc.type(), jType));
	}
	static int byJavaType(int xtype, Class<?> jtype) {
		if(xtype == 0 && (jtype == boolean.class || jtype == Boolean.class)) {
			return XColumn.type_bool;
		}
		return xtype;
	}
	public Column(String key, XColumn xc, int xcType) {
		this(key, XStrings.orElse(xc.value(), key), xcType, xc.enumKey(), xc.show(), xc.primary());
	}
	public Column(String key, String hint, int type, String enumKey, int show, boolean primary) {
		this.key = key;
		this.hint = hint;
		this.type = type;
		this.enumKey = enumKey;
		this.show = show;
		this.primary = primary;
		
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

    public boolean getPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

}
