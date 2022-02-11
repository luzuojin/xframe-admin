package dev.xframe.admin.view;

import dev.xframe.utils.XProperties;
import dev.xframe.utils.XStrings;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;

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

    static Class<?> getRawType(Type type) {
        return (Class<?>) (type instanceof ParameterizedType ? ((ParameterizedType) type).getRawType() : type);
    }
    static Class<?> getComponentType(Type type) {
        Class<?> rawType = getRawType(type);
        if(Collection.class.isAssignableFrom(rawType)) {
            return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        if(rawType.isArray()) {
            return rawType.getComponentType();
        }
        return rawType;
    }
    /**
     * @param jtype field.type
     * @param ctype field.componentType
     */
    static int inferType(XColumn xc, Class<?> jtype, Class<?> ctype, String key) {
        if(xc.type() == 0) {
            if(!XStrings.isEmpty(xc.enumKey()))
                return isMulti(jtype) ? XColumn.type_mult : XColumn.type_enum;
            if(jtype == boolean.class || jtype == Boolean.class)
                return XColumn.type_bool;
            if(jtype.isPrimitive() || Number.class.isAssignableFrom(jtype))
                return XColumn.type_number;
            if(ctype.getClassLoader() == Column.class.getClassLoader())//自定义类型
                return isMulti(jtype) ? XColumn.type_list : XColumn.type_model;
            if(jtype == LocalDateTime.class || jtype == Timestamp.class || jtype == Date.class)
                return XColumn.type_datetime;
            if(jtype == LocalDate.class)
                return XColumn.type_date;
            if(jtype == LocalTime.class)
                return XColumn.type_time;
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

    static boolean isMulti(Class<?> jtype) {
        return jtype.isArray() || Collection.class.isAssignableFrom(jtype);
    }
    static String firstToUpperCase(String key) {
        return String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1);
    }
    static boolean isNested(int type) {
        return (type / 10) == (XColumn.type_model / 10);
    }

    public static Column of(XColumn xc, Field field) {
        return of(field.getName(), xc, field.getGenericType());
    }
    public static Column of(String key, XColumn xc, Type type) {
        Class<?> jCls = getRawType(type);
        Class<?> cCls = getComponentType(type);
        int colType = inferType(xc, jCls, cCls, key);
        return isNested(colType) ? new Nested(key, colType, xc, Detail.parseModelColumns(cCls)) : new Column(key, colType, xc);
    }

    public Column(String key, int type, XColumn xc) {
        this.key = key;
        this.type = type;
        this.hint = XStrings.orElse(xc.value(), firstToUpperCase(key));
        this.enumKey = xc.enumKey();
        this.show = xc.show();
        this.primary = xc.primary();
        this.collapse = xc.collapse();
        this.compact = xc.compact();
        this.required = xc.required();
        this.sortable = xc.sortable() && !isNested(this.type);
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
