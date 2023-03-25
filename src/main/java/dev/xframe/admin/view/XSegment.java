package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * path use @Http.path
 * @author luzj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XSegment {
	
	String name();

	int type() default EContent.Table;

	Class<?> model() default void.class;

	//for table content
	boolean padding() default false;//Option.add时是否把qry表单中的字段用来填充

	//for panel content
	String desc() default "";//description

	//for chart content
	int chart() default EChart.Table;

	//order[large---small]
	int order() default 10;
}
