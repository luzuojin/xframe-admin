package dev.xframe.admin.view;

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
		private final Class<T> ma;
		private final Function<T, String> vl;
		private final boolean mk;
		private final boolean np;
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
		private Option make(Method m) {
			return op.copy(m.getAnnotation(XOption.class), vl.apply(m.getAnnotation(ma))).with(Detail.parseParamColumns(m));
		}
		Option apply(Method m) {
			return m.isAnnotationPresent(ma) && isOpPredicated(m) && isParamPredicated(m) ? make(m) : null;
		}
	}
	static final List<Parser<?>> Parsers = Arrays.asList(
			new Parser<>(new Option("结构", XOption.type_flx), GET.class,    GET::value,   true,  false),
			new Parser<>(new Option("下载", XOption.type_dlh), GET.class,    GET::value,   true,  false),
			new Parser<>(new Option("下载", XOption.type_dlr), GET.class,    GET::value,   true,  false),
			new Parser<>(new Option("加载", XOption.type_ini), GET.class,    GET::value,   false, true),
			new Parser<>(new Option("查询", XOption.type_qry), GET.class,    GET::value,   false, false),
			new Parser<>(new Option("修改", XOption.type_edt), PUT.class,    PUT::value,   false, false),
			new Parser<>(new Option("新增", XOption.type_add), POST.class,   POST::value,  false, false),
			new Parser<>(new Option("删除", XOption.type_del), DELETE.class, DELETE::value,false, false)
	);

	private String name;
	private String path;
	private List<Column> inputs = new ArrayList<>();
	private int type; //1(增), 2(查)
	
	public Option(String name, int type) {
	    this(name, type, "");
	}
	public Option(String name, int type, String path) {
	    this.name = name;
	    this.type = type;
	    this.path = path;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Column> getInputs() {
		return inputs;
	}
	public void setInputs(List<Column> inputs) {
		this.inputs = inputs;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Option copy(XOption op, String path) {
		String xname = op == null ? null : op.value();
		return new Option(XStrings.orElse(xname, this.name), type, path);
	}
	public Option with(List<Column> columns) {
	    this.inputs = columns;
	    return this;
	}
	@Override
	public int compareTo(Option o) {
	    if(type == o.type) {
	        if(type == XOption.type_add) {//add在前端展示时为从右至左
	            return o.path.compareTo(this.path);
	        } else {
	            return this.path.compareTo(o.path);
	        }
	    }
	    return Integer.compare(typeOrderNum(), o.typeOrderNum());
	}
	int typeOrderNum() {
		return type == XOption.type_del ? type * 10 : type;
	}
	
}
