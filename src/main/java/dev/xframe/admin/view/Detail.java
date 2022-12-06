package dev.xframe.admin.view;

import dev.xframe.http.Request;
import dev.xframe.http.request.HttpBody;
import dev.xframe.http.request.QueryString;
import dev.xframe.http.service.rest.HttpArgs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Detail {

    Detail parseFrom(XSegment xseg, Class<?> declaring);

    static List<Option> parseOptions(Class<?> declaring, Class<?> model) {
        return Arrays.stream(declaring.getDeclaredMethods())
                .map(m->Option.Parsers.stream()
                        .map(op->op.apply(m, model))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    static List<Column> parseParamColumns(Method method, Class<?> model) {
        List<Column> columns = new ArrayList<>();
        //edit/delete/add 参数由HttpBody解析. 默认使用segment.columns, 如果有XAdapter标识的Param则使用该Param.type替代
        for (Parameter p : method.getParameters()) {
        	//XAdapter标识 HttpArgs.Body
        	if(p.isAnnotationPresent(HttpArgs.Body.class) && isModelBody(p, model)) {
        		columns = parseModelColumns(p.getType());
        		break;//只能有一个
        	}
        	//XColumn标识 HttpArgs.Param
        	if(p.isAnnotationPresent(HttpArgs.Param.class) && isColumnParam(p)) {
                if(p.isAnnotationPresent(XColumn.class)) {
        		    columns.add(Column.of(p.getName(), p.getAnnotation(XColumn.class), p.getType()));
                } else {
        		    columns.add(Column.of(p.getName(), XColumn.Default, p.getType()));
                }
        	}
        }
        return columns;
    }
    static boolean isModelBody(Parameter p, Class<?> model) {
        return p.isAnnotationPresent(XAdapter.class) || !(p.getType().equals(model) || p.getType().equals(HttpBody.class) || p.getType().equals(byte[].class) || p.getType().equals(String.class));
    }
    static boolean isColumnParam(Parameter p) {
        return p.isAnnotationPresent(XColumn.class) || !(p.getType().equals(Request.class) || p.getType().equals(QueryString.class));
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
        	if(Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
                continue;
            XColumn xc = Optional.ofNullable(field.getAnnotation(XColumn.class)).orElse(XColumn.Default);
            if(xc.show() > 0) {
                columns.add(Column.of(xc, field));
            }
        }
        return columns;
    }

}
