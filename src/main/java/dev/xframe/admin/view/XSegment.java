package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.xframe.admin.view.details.Table;

/**
 * path use @Http.path
 * @author luzj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XSegment {
	
	String name();
	Class<? extends Detail> detail() default Table.class;
	Class<?> model();
	//for table detail
	boolean padding() default false;//Option.add时是否把qry表单中的字段用来填充
	//for panel detail
	String desc() default "";//description

}
