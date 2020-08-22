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
        //edit/delete/add 参数由HttpBody解析. 默认使用segment.columns, 如果有XAdapter标识的Param则使用该Param.type替代
        for (Parameter p : method.getParameters()) {
        	//XAdapter标识 HttpArgs.Body
        	if(p.isAnnotationPresent(XAdapter.class) && p.isAnnotationPresent(HttpArgs.Body.class)) {
        		columns = parseModelColumns(p.getType());
        		break;//只会有一个
        	}
        	//XColumn标识 HttpArgs.Param
        	if(p.isAnnotationPresent(XColumn.class)  && p.isAnnotationPresent(HttpArgs.Param.class)) {
        		columns.add(new Column(p.getName(), p.getAnnotation(XColumn.class), p.getType()));
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
            XColumn xc = field.getAnnotation(XColumn.class);
            String name = field.getName();
            if(xc == null) {
                columns.add(new Column(name));
            } else if(xc.show() > 0) {
                if(xc.type() == XColumn.type_model || xc.type() == XColumn.type_list) {
                	columns.add(new Nested(name, xc, parseModelColumns(Nested.getType(field))));
                } else {
                	columns.add(new Column(name, xc, field.getType()));
                }
            }
        }
        return columns;
    }

}
