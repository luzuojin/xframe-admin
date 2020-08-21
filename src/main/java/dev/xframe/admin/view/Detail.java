package dev.xframe.admin.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods.DELETE;
import dev.xframe.http.service.rest.HttpMethods.GET;
import dev.xframe.http.service.rest.HttpMethods.POST;
import dev.xframe.http.service.rest.HttpMethods.PUT;
import dev.xframe.utils.XStrings;

public interface Detail {
    
	int type_table = 1;//表格类详情页
	int type_panel = 2;//单对象详情页
	
    public Detail parseFrom(XSegment xseg, Class<?> declaring);
    
    static List<Option> parseOptions(Class<?> declaring, Class<?> model) {
        List<Option> options = new ArrayList<>();
        Method[] methods = declaring.getDeclaredMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(GET.class)) {
            	options.add(parseOption(GetMethodOption(method), model, method, method.getAnnotation(GET.class).value()));
            } else if(method.isAnnotationPresent(PUT.class)) {
                options.add(parseOption(Option.edt, model, method, method.getAnnotation(PUT.class).value()));
            } else if(method.isAnnotationPresent(DELETE.class)) {
                options.add(parseOption(Option.del, model, method, method.getAnnotation(DELETE.class).value()));
            } else if(method.isAnnotationPresent(POST.class)) {
                options.add(parseOption(Option.add, model, method, method.getAnnotation(POST.class).value()));
            }
        }
        Collections.sort(options);
        return options;
    }

    static Option GetMethodOption(Method method) {
    	return isIniMethod(method) ? Option.ini : (isFlxMethod(method) ? Option.flx : Option.qry);
    }
	static boolean isFlxMethod(Method method) {
		return method.isAnnotationPresent(XOption.class) && method.getAnnotation(XOption.class).type() == XOption.type_flx;
	}
	static boolean isIniMethod(Method method) {//GET,只有非URL参数
		return !Arrays.stream(method.getParameters()).filter(p->p.isAnnotationPresent(HttpArgs.Param.class)).findAny().isPresent();
	}
    
    static Option parseOption(Option op, Class<?> model, Method method, String path) {
        return op.copy(method.getAnnotation(XOption.class), path).with(parseParamColumns(model, method));
    }

    static List<Column> parseParamColumns(Class<?> model, Method method) {
        List<Column> columns = new ArrayList<>();
        Parameter[] params = method.getParameters();
        //只有一个参数而且由HttpBody(post)解析.(edit/delete/add)
        if(params.length == 1 && params[0].isAnnotationPresent(HttpArgs.Body.class)) {
			if(!params[0].getType().equals(model) && params[0].isAnnotationPresent(XAdapter.class)) {//与seg.model不同
                columns = parseModelColumns(params[0].getType());
            }//else 特殊解析(JSONString...),由业务完成
        } else {
            for (Parameter p : params) {
                XColumn xi = p.getAnnotation(XColumn.class);
                if(xi != null) {
                    columns.add(new Column(p.getName(), XStrings.orElse(xi.value(), p.getName()), xi.type(), xi.enumKey()));
                }
            }
        }
        return columns;
    }

    static List<Column> parseModelColumns(Class<?> model) {
        return parseModelColumns0(model, new ArrayList<>());
    }
    static List<Column> parseModelColumns0(Class<?> model, List<Column> columns) {
        if(!Object.class.equals(model.getSuperclass())) {
            parseModelColumns0(model.getSuperclass(), columns);
        }
        Field[] fields = model.getDeclaredFields();
        for (Field field : fields) {
        	if(Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
            XColumn xf = field.getAnnotation(XColumn.class);
            String name = field.getName();
            if(xf == null) {
                columns.add(new Column(name, name, XColumn.type_text, "", XColumn.full, false));
            } else if(xf.show() > 0) {
                int xtype = xf.type();
                if(xtype == 0 && (field.getType() == boolean.class || field.getType() == Boolean.class)) {
                	xtype = XColumn.type_bool;
                }
                if(xtype == XColumn.type_model || xtype == XColumn.type_list) {
                	columns.add(new Nested(name, XStrings.orElse(xf.value(), name), xtype, xf.enumKey(), xf.show(), xf.primary(), parseModelColumns(Nested.getType(field))));
                } else {
                	columns.add(new Column(name, XStrings.orElse(xf.value(), name), xtype, xf.enumKey(), xf.show(), xf.primary()));
                }
            }
        }
        return columns;
    }

}
