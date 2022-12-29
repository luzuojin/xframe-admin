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

	/**数据初始化*/
	int type_ini = -1;
	/**查询*/
	int type_qry = 1;
	/**新增*/
	int type_add = 2;
	/**修改*/
	int type_edt = 3;
	/**删除*/
	int type_del = 4;
	/**变更结构体(Variant.Struct)*/
	int type_vrt = 5;//变更结构的查询操作
	/**download*/
	int type_dlh = 6;
	/**table row download*/
	int type_dlr = 7;
	
}
