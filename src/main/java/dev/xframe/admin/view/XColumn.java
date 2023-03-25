package dev.xframe.admin.view;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface XColumn {

	/**显示文字*/
	String value() default "";
	String hint() default "";
	/**类型 @see 常量XColumn.type_...*/
	int type() default EColumn.Text;
	/**显示配置 @see 常量XColumn.list...etc*/
	int show() default EShowcase.Full;
	/**下拉列表时获取下拉菜单的关键字 @see BasicContext.registEnumValue*/
	String enumKey() default "";
	/**用来修改/删除时匹配前端cache用*/
	boolean primary() default false;
	/**type_list时 是否折叠*/
	boolean collapse() default false;
	/**type_list时 是否显示为一行(只对nest object columns为2~3时生效)*/
	boolean compact() default false;
	/**客户端提交表单时验证是否有值*/
	boolean required() default false;
	/**是否需要支持排序*/
	boolean sortable() default false;
	/**是否本地缓存表单数据(Panel&Query)*/
	boolean cacheable() default false;
	/**本地缓存的key*/
	String cacheKey() default "";

	XColumn Default = new XColumn(){
		public int type() {return EColumn.Text;}
		public String value() {return "";}
		public String hint() {return "";}
		public int show() {return EShowcase.Full;}
		public String enumKey() {return "";}
		public boolean primary() {return false;}
		public boolean collapse() {return false;}
		public boolean compact() {return false;}
		public boolean required() {return false;}
		public boolean sortable() {return false;}
		public boolean cacheable() {return false;}
		public String cacheKey() {return "";}
		public Class<? extends Annotation> annotationType() {return XColumn.class;}
	};
}
