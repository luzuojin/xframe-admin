package dev.xframe.admin.view;

import dev.xframe.utils.XStrings;

public class Column {
    private int type;
    private String key;
    private String hint;
    private String enumKey;

    private int show;
    private boolean primary;
    private boolean collapse;
    private boolean compact;
    private boolean required;
    private boolean canSort;

    static int byJavaType(int xtype, Class<?> jtype) {
        if(xtype == 0 && (jtype == boolean.class || jtype == Boolean.class)) {
            return XColumn.type_bool;
        }
        return xtype;
    }
    static String firstToUpperCase(String key) {
        return String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1);
    }

    public Column(String key) {
        this(key, firstToUpperCase(key), XColumn.type_text, "", XColumn.full, false, false, false, false, true);
    }
    public Column(String key, XColumn xc, Class<?> jType) {
        this(key, xc, byJavaType(xc.type(), jType));
    }
    public Column(String key, XColumn xc, int xcType) {
        this(key, XStrings.orElse(xc.value(), firstToUpperCase(key)), xcType, xc.enumKey(), xc.show(), xc.primary(), xc.collapse(), xc.compact(), xc.required(), xc.canSort());
    }
    public Column(String key, String hint, int type, String enumKey, int show, boolean primary, boolean collapse, boolean compact, boolean required, boolean canSort) {
        this.key = key;
        this.hint = hint;
        this.type = type;
        this.enumKey = enumKey;
        this.show = show;
        this.primary = primary;
        this.collapse = collapse;
        this.compact = compact;
        this.required = required;
        this.canSort = canSort;

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
    public boolean getCollapse() {
        return collapse;
    }
    public void setCollapse(boolean collapse) {
        this.collapse = collapse;
    }
    public boolean getRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public boolean getCompact() {
        return compact;
    }
    public void setCompact(boolean compact) {
        this.compact = compact;
    }
    public boolean isCanSort() {
        return canSort;
    }
    public void setCanSort(boolean canSort) {
        this.canSort = canSort;
    }
}
