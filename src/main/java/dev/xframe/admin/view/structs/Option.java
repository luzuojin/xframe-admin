package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XOption;
import dev.xframe.http.service.rest.HttpArgs.Param;
import dev.xframe.http.service.rest.HttpMethods.DELETE;
import dev.xframe.http.service.rest.HttpMethods.GET;
import dev.xframe.http.service.rest.HttpMethods.POST;
import dev.xframe.http.service.rest.HttpMethods.PUT;
import dev.xframe.utils.XStrings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Option implements Comparable<Option> {

	static class Parser<T extends Annotation> {
		private final Option op;
		private final Class<T> ma;				//@HttpMethods.Annotation
		private final Function<T, String> vl;	//@HttpMethods.Annotation.value
		private final boolean mk;				//@XOption marked required
		private final boolean np;				//@HttpArgs.Param marked argument none
		private Parser(Option op, Class<T> ma, Function<T, String> vl, boolean mk, boolean np) {
			this.op = op;
			this.ma = ma;
			this.vl = vl;
			this.mk = mk;
			this.np = np;
		}
		private boolean isOpPredicated(Method m) {
			return !this.mk || m.isAnnotationPresent(XOption.class) && m.getAnnotation(XOption.class).type() == op.type;
		}
		private boolean isParamPredicated(Method m) {
			return !this.np || Arrays.stream(m.getParameters()).noneMatch(p -> p.isAnnotationPresent(Param.class));
		}
		private Option make(Method m, Class<?> model) {
			return op.copy(m.getAnnotation(XOption.class), vl.apply(m.getAnnotation(ma))).with(m).with(Content.parseParamColumns(m, model));
		}
		Option apply(Method m, Class<?> model) {
			return m.isAnnotationPresent(ma) && isOpPredicated(m) && isParamPredicated(m) ? make(m, model) : null;
		}
	}
	static final List<Parser<?>> Parsers = Arrays.asList(
			new Parser<>(new Option("结构", EOption.Var), GET.class,    GET::value,   true,  false),
			new Parser<>(new Option("下载", EOption.Dlh), GET.class,    GET::value,   true,  false),
			new Parser<>(new Option("下载", EOption.Dlr), GET.class,    GET::value,   true,  false),
			new Parser<>(new Option("加载", EOption.Ini), GET.class,    GET::value,   false, true),
			new Parser<>(new Option("查询", EOption.Qry), GET.class,    GET::value,   false, false),
			new Parser<>(new Option("修改", EOption.Edt), PUT.class,    PUT::value,   false, false),
			new Parser<>(new Option("新增", EOption.Add), POST.class,   POST::value,  false, false),
			new Parser<>(new Option("删除", EOption.Del), DELETE.class, DELETE::value,false, false)
	);

	private String name;
	private String path;
	private List<Column> columns = new ArrayList<>();
	private int type; //1(增), 2(查)
	transient Method method;
	
	public Option(String name, int type) {
	    this(name, type, "");
	}
	public Option(String name, int type, String path) {
	    this.name = name;
	    this.type = type;
	    this.path = path;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public int getType() {
		return type;
	}
	public String getPath() {
        return path;
    }
    public Option copy(XOption op, String path) {
		String xname = op == null ? null : op.value();
		return new Option(XStrings.orElse(xname, this.name), type, path);
	}
	public Option with(Method method) {
		this.method = method;
		return this;
	}
	public Option with(List<Column> columns) {
	    this.columns = columns;
	    return this;
	}
	@Override
	public int compareTo(Option o) {
	    if(type == o.type) {
	        if(type == EOption.Add) {//add在前端展示时为从右至左
	            return o.path.compareTo(this.path);
	        } else {
	            return this.path.compareTo(o.path);
	        }
	    }
	    return Integer.compare(typeOrderNum(), o.typeOrderNum());
	}
	int typeOrderNum() {
		return type == EOption.Del ? type * 10 : type;
	}
	
}
