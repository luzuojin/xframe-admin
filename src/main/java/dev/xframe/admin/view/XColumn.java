package dev.xframe.admin.view;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface XColumn {

	/**显示文字(hint)*/
	String value() default "";
	/**类型 @see 常量XColumn.type_...*/
	int type() default type_text;
	/**显示配置 @see 常量XColumn.list...etc*/
	int show() default full;
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
	String cacheKey() default "";
	
	//Column类型
	int type_text = 0;	//文本
	int type_area = 1;	//大段文本
	int type_number = 2;//数字
	int type_pass = 3;	//密码
	int type_phone = 4; //手机号
	int type_email = 5; //邮箱
	int type_bool = 20;	//check radio
	int type_enum = 21;	//下拉框 @see BasicContext.registEnumValue
	int type_mult = 22;	//下拉框(多选)
	int type_datetime = 30;	//日期+时间
	int type_date = 31;	//日期
	int type_time = 32;	//仅时间
	int type_file = 40;	//文件
	int type_imag = 41;	//图片
	int type_model = 80;//对应object
	int type_list  = 81; //object list

	//展示在哪里的相关配置
	int list = 1 << 0;//Table列表中展示
	int edit = 1 << 1;//是否可编辑(disable),显示由edel决定
	int add  = 1 << 2;//新增(add)Dialog中展示
	int edel = 1 << 3;//编辑/删除(edt/del)中展示
	//下面为各种组合展示的配置
	int full = (1 << 4) - 1;		//所有
	int list_edel = (list | edel);	//列表及改删(只有新增不显示)
	int xor_list = (full ^ list);	//只有列表不显示
	int xor_edit = (full ^ edit);	//只是不能编辑
	int xor_add  = (full ^ add);	//新增不显示
	int xor_edel = (full ^ edel);	//改删均不展示

	XColumn Default = new XColumn(){
		public String value() {return "";}
		public int type() {return type_text;}
		public int show() {return full;}
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
