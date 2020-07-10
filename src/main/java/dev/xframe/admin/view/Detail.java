package dev.xframe.admin.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.utils.XStrings;

public interface Detail {
    
    public Detail parseFrom(XSegment xseg, Class<?> declaring);

    /**
     * 表格详细页
     * 绑定Model, 支持增删改查
     * @author luzj
     */
    public static class TableDetail implements Detail {
        
        private List<Column> columns = Collections.emptyList();
        private List<Option> options = Collections.emptyList();
        
        private boolean padding;
        private boolean listable;
        
        public List<Column> getColumns() {
            return columns;
        }
        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }
        public List<Option> getOptions() {
            return options;
        }
        public void setOptions(List<Option> options) {
            this.options = options;
        }
        public boolean getPadding() {
            return padding;
        }
        public void setPadding(boolean padding) {
            this.padding = padding;
        }
        public boolean getListable() {
            return listable;
        }
        public void setListable(boolean listable) {
            this.listable = listable;
        }
        
        @Override
        public Detail parseFrom(XSegment xseg, Class<?> declaring) {
            this.options = parseOptions(declaring, xseg.model());
            this.columns = parseModelColumns(xseg.model());
            return this;
        }
        
        List<Option> parseOptions(Class<?> declaring, Class<?> model) {
            List<Option> options = new ArrayList<>();
            boolean listable = false;
            Method[] methods = declaring.getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(HttpMethods.GET.class)) {
                    if(XOption.listing.equals(method.getAnnotation(HttpMethods.GET.class).value())) {
                        listable = true;
                        continue;
                    }
                    options.add(parseOption(Option.qry, model, method));
                } else if(method.isAnnotationPresent(HttpMethods.PUT.class)) {
                    options.add(parseOption(Option.edt, model, method));
                } else if(method.isAnnotationPresent(HttpMethods.DELETE.class)) {
                    options.add(parseOption(Option.del, model, method));
                } else if(method.isAnnotationPresent(HttpMethods.POST.class)) {
                    options.add(parseOption(Option.add, model, method));
                }
            }
            Collections.sort(options);
            this.setListable(listable);
            return options;
        }
        Option parseOption(Option op, Class<?> model, Method method) {
            return op.copy(method.getAnnotation(XOption.class)).with(parseParamColumns(model, method));
        }

        List<Column> parseParamColumns(Class<?> model, Method method) {
            List<Column> columns = new ArrayList<>();
            Parameter[] params = method.getParameters();
            //只有一个参数而且由HttpBody(post)解析.(edit/delete/add)
            if(params.length == 1 && params[0].isAnnotationPresent(HttpArgs.Body.class)) {
                if(!params[0].getType().equals(model)) {//与seg.model不同
                    columns = parseModelColumns(params[0].getType());
                }
            } else {
                for (Parameter p : params) {
                    XColumn xi = p.getAnnotation(XColumn.class);
                    if(xi != null) {
                        columns.add(new Column(p.getName(), XStrings.orElse(xi.value(), p.getName()), xi.type(), xi.enumKey(), xi.indep()));
                    }
                }
            }
            return columns;
        }

        List<Column> parseModelColumns(Class<?> model) {
            return parseModelColumns0(model, new ArrayList<>());
        }
        List<Column> parseModelColumns0(Class<?> model, List<Column> columns) {
            if(!Object.class.equals(model.getSuperclass())) {
                parseModelColumns0(model.getSuperclass(), columns);
            }
            Field[] fields = model.getDeclaredFields();
            for (Field field : fields) {
                XColumn xf = field.getAnnotation(XColumn.class);
                String name = field.getName();
                if(xf == null) {
                    columns.add(new Column(name, name, XColumn.type_text, "", XColumn.full, false));
                } else if(xf.show() > 0) {
                    int xtype = xf.type();
                    if(xtype == 0 && (field.getType() == boolean.class || field.getType() == Boolean.class))
                        xtype = XColumn.type_bool;
                    columns.add(new Column(name, XStrings.orElse(xf.value(), name), xtype, xf.enumKey(), xf.show(), xf.primary()));
                }
            }
            return columns;
        }
    }
}
