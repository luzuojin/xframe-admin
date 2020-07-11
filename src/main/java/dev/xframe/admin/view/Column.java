package dev.xframe.admin.view;

import dev.xframe.utils.XStrings;

public class Column {
	
	private int type;
	private String key;
	private String hint;
	private String enumKey;
	
	private int show;
	private boolean primary;
	private boolean indep;//enumkey时是否独立选择值, 非独立时如果有会取之前选择过的值作为默认值
	
	public Column(String key, String hint, int type, String enumKey, boolean indep) {
		this(key, hint, type, enumKey, indep, XColumn.full, false);
	}
	
	public Column(String key, String hint, int type, String enumKey, boolean indep, int show, boolean primary) {
		this.key = key;
		this.hint = hint;
		this.type = type;
		this.enumKey = enumKey;
		this.show = show;
		this.primary = primary;
		this.indep = indep;
		
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

    public boolean getIndep() {
        return indep;
    }

    public void setIndep(boolean indep) {
        this.indep = indep;
    }

}
