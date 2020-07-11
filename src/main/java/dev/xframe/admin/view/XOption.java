package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XOption {

	public String value();

	public static final int type_ini = -1;
	public static final int type_qry = 1;
	public static final int type_add = 2;
	public static final int type_edt = 3;
	public static final int type_del = 4;
	
}
