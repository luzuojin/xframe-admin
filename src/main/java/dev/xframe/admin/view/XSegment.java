package dev.xframe.admin.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.xframe.admin.view.Detail.TableDetail;

/**
 * path use @Http.path
 * @author luzj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XSegment {
	
	String name();
	Class<? extends Detail> detail() default TableDetail.class;
	Class<?> model();
	boolean padding() default false;//Option.add时是否把qry表单中的字段用来填充

}
