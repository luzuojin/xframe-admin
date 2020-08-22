package dev.xframe.admin.view;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public class Nested extends Column {

	private List<Column> columns;

	public Nested(String key, XColumn xc, List<Column> columns) {
		super(key, xc, xc.type());
		this.columns = columns;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public static Class<?> getType(Field field) {
		Class<?> ftype = field.getType();
		if(field.getAnnotation(XColumn.class).type() == XColumn.type_model) {
			return ftype;
		} else if(Collection.class.isAssignableFrom(ftype)) {
			return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		} else if(ftype.isArray()) {
			return ftype.getComponentType();
		}
		throw new IllegalArgumentException("Illegal nested field: " + field);
	}

}
