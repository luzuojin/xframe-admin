package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XOption {

	//name
	String value() default "";
	
	int type() default 0;

	int type_ini = -1;
	int type_qry = 1;
	int type_add = 2;
	int type_edt = 3;
	int type_del = 4;
	int type_flx = 5;//变更结构的查询操作
	
}
