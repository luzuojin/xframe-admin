package dev.xframe.admin.view;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface XColumn {

	String value() default "";		//显示文字(hint)
	
	int type() default type_text;	//类型 @see 常量

	int show() default full;		//显示配置 @see 常量
	
	String enumKey() default "";	//下拉列表时获取下拉菜单的关键字 @see BasicContext.registEnumValue
	
	boolean primary() default false;//用来修改/删除时匹配前端cache用
	
	boolean collapse() default false;//type_list时 是否折叠

	boolean compact() default false; //type_list时 是否显示为一行(只对nest object columns为2~3时生效)
	
	boolean required() default false;//客户端提交表单时验证是否有值

	boolean canSort() default true;//是否开启排序
	
	//Column类型
	public static final int type_text = 0;	//文本
	public static final int type_bool = 1;	//check radio
	public static final int type_enum = 2;	//下拉框 @see BasicContext.registEnumValue
	public static final int type_datetime = 3;	//日期+时间
	public static final int type_area = 4;	//大段文本
	public static final int type_file = 5;	//文件上传
	public static final int type_imag = 6;	//文件(图片)上传
	public static final int type_pass = 9;	//密码
	public static final int type_mult = 20;	//下拉框(多选)
	public static final int type_date = 31;	//日期
	public static final int type_time = 32;	//仅时间
	public static final int type_model = 80;//对应object
	public static final int type_list  = 81; //object list
	public static final int type_text_phone  = 101; //手机号
	public static final int type_text_email  = 102; //邮箱

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
		public Class<? extends Annotation> annotationType() {return XColumn.class;}
	};
}
