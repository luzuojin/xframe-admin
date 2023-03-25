package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.EShowcase;
import dev.xframe.admin.view.XColumn;
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
import java.util.Objects;

public class Column {

    private int type;
    private String key;
    private String name;
    private String hint;
    private String enumKey;

    private int show;
    private boolean primary;
    private boolean collapse;
    private boolean compact;
    private boolean required;
    private boolean sortable;
    private boolean cacheable;
    private String cacheKey;

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
        if(xc.type() == EColumn.Text) {
            if(!XStrings.isEmpty(xc.enumKey()))
                return isMulti(jtype) ? EColumn.Mult : EColumn.Enum;
            if(jtype == boolean.class || jtype == Boolean.class)
                return EColumn.Bool;
            if(jtype.isPrimitive() || Number.class.isAssignableFrom(jtype))
                return EColumn.Number;
            if(ctype.getClassLoader() == Column.class.getClassLoader())//自定义类型
                return isMulti(jtype) ? EColumn.List : EColumn.Model;
            if(jtype == LocalDateTime.class || jtype == Timestamp.class || jtype == Date.class)
                return EColumn.Datetime;
            if(jtype == LocalDate.class)
                return EColumn.Date;
            if(jtype == LocalTime.class)
                return EColumn.Time;
            //通过字段名推断类型... 默认关闭
            if(XProperties.getAsBool("xframe.admin.column.namingtype", false)) {
                String lowerCaseKey = key.toLowerCase();
                if(lowerCaseKey.contains("password"))
                    return EColumn.Pass;
                if(lowerCaseKey.contains("date") || key.contains("time"))
                    return EColumn.Datetime;
                if(lowerCaseKey.contains("file"))
                    return EColumn.File;
                if(lowerCaseKey.contains("image"))
                    return EColumn.Imag;
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

    public static Column of(XColumn xc, Field field) {
        return of(field.getName(), xc, field.getGenericType());
    }
    public static Column of(String key, XColumn xc, Type type) {
        Class<?> jCls = getRawType(type);
        Class<?> cCls = getComponentType(type);
        int colType = inferType(xc, jCls, cCls, key);
        return EColumn.isNested(colType) ? new Nested(key, colType, xc, Content.parseModelColumns(cCls)) : new Column(key, colType, xc);
    }

    public Column(String key, int type, XColumn xc) {
        this.key = key;
        this.type = type;
        this.name = XStrings.orElse(xc.value(), firstToUpperCase(key));
        this.hint = xc.hint();
        this.enumKey = xc.enumKey();
        this.show = xc.show();
        this.primary = xc.primary();
        this.collapse = xc.collapse();
        this.compact = xc.compact();
        this.required = xc.required();
        this.sortable = xc.sortable() && !EColumn.isNested(type);
        this.cacheKey = xc.cacheKey();
        this.cacheable = xc.cacheable() || !XStrings.isEmpty(this.cacheKey);
    }

    public Column(String key, int type, String name, String hint) {
        this.key = key;
        this.type = type;
        this.name = name;
        this.hint = hint;
        this.show = EShowcase.Full;
    }

    public static Column of(String key) {
        return new Column(key, EColumn.Text, XColumn.Default);
    }

    public int getShow() {
        return show;
    }
    public int getType() {
        return type;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getEnumKey() {
        return enumKey;
    }
    public boolean getPrimary() {
        return primary;
    }
    public boolean getCollapse() {
        return collapse;
    }
    public boolean getRequired() {
        return required;
    }
    public boolean getCompact() {
        return compact;
    }
    public boolean getSortable() {
        return sortable;
    }
    public boolean getCacheable() {
        return cacheable;
    }
    public String getCacheKey() {
        return cacheKey;
    }
    public String getHint() {
        return hint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(key, column.key);
    }
    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
