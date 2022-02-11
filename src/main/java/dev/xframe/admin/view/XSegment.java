package dev.xframe.admin.view;

import dev.xframe.admin.view.details.Table;

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
	int type() default type_table;
	Class<?> model();
	//for table detail
	boolean padding() default false;//Option.add时是否把qry表单中的字段用来填充
	//for panel detail
	String desc() default "";//description
	//order[large---small]
	int order() default 10;

	boolean canSort() default false;//是否开启字段排序
	
	/**deprecated by type */
	@Deprecated
	Class<? extends Detail> detail() default Table.class;

	/**表格类详情页*/
	int type_table = 1;
	/**单对象详情页*/
	int type_panel = 2;
	/**Markdown展示页,只需要ini*/
	int type_markd = 3;

}
