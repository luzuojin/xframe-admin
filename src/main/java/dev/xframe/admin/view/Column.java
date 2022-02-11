package dev.xframe.admin.view;

import dev.xframe.utils.XProperties;
import dev.xframe.utils.XStrings;

import java.util.Collection;

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
    private boolean sortable;

    static int inferType(XColumn xc, Class<?> jtype, String key) {
        if(xc.type() == 0) {
            if(!XStrings.isEmpty(xc.enumKey()))
                return (jtype.isArray() || Collection.class.isAssignableFrom(jtype)) ?
                        XColumn.type_mult : XColumn.type_enum;
            if(jtype == boolean.class || jtype == Boolean.class)
                return XColumn.type_bool;
            if(jtype.isPrimitive() || Number.class.isAssignableFrom(jtype))
                return XColumn.type_number;
            //通过字段名推断类型... 默认关闭
            if(XProperties.getAsBool("xframe.admin.column.namingtype", false)) {
                String lowerCaseKey = key.toLowerCase();
                if(lowerCaseKey.contains("password"))
                    return XColumn.type_pass;
                if(lowerCaseKey.contains("date") || key.contains("time"))
                    return XColumn.type_datetime;
                if(lowerCaseKey.contains("file"))
                    return XColumn.type_file;
                if(lowerCaseKey.contains("image"))
                    return XColumn.type_imag;
            }
        }
        return xc.type();
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
        this.hint = XStrings.orElse(xc.value(), firstToUpperCase(key));
        this.type = inferType(xc, jType, key);
        this.enumKey = xc.enumKey();
        this.show = xc.show();
        this.primary = xc.primary();
        this.collapse = xc.collapse();
        this.compact = xc.compact();
        this.required = xc.required();
        this.sortable = xc.canSort();
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
    public boolean isSortable() {
        return sortable;
    }
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }
}
