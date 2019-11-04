package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface XColumn {

	String value() default "";
	
	int type() default type_text;

	int show() default full;
	
	String enumKey() default "";
	
	boolean primary() default false;
	
	
	public static final int type_text = 0;
	public static final int type_bool = 1;
	public static final int type_enum = 2;
	public static final int type_time = 3;
	public static final int type_area = 4;
	public static final int type_pass = 9;
	public static final int type_mult = 20;
	
	public static final int list = 1 << 0;
	public static final int edit = 1 << 1;
	public static final int add  = 1 << 2;
	public static final int edel = 1 << 3;//disable edit and delete column display
	public static final int full = (1 << 4) - 1;
	
	public static final int list_edel = list | edel;
	
	public static final int xor_list = (full ^ list);
	public static final int xor_edit = (full ^ edit);
	public static final int xor_add  = (full ^ add);
	public static final int xor_edel = (full ^ edel);
	
}
