package dev.xframe.admin.view.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;

//结构变化的结果
public class Flex {
    //可以用来识别变化之后的结构类
    //Http.header("flex-name")
    public static final String HEADER_KEY = "flex-name";
    
    public final Struct struct;
    public final Object data;
	
	public Flex(Struct struct, Object data) {
	    this.struct = struct;
	    this.data = data;
    }
    public static Flex data(Object data) {
        return new Flex(null, data);
	}
    public static Flex struct(Class<?> model) {
        return new Flex(makeStruct(model), null);
    }
	public static Flex structAndData(Object data) {
	    return structAndData(data.getClass(), data);
	}
	public static Flex structAndData(Class<?> model, Object data) {
	    return new Flex(makeStruct(model), data);
	}

	private static final Map<Class<?>, List<Column>> caches = new HashMap<>();
    private static Struct makeStruct(Class<?> model) {
        return new Struct(caches.computeIfAbsent(model, Detail::parseModelColumns), model.getName());
    }
    public static class Struct {
	    //Http.header("flex-name")
	    public final String flexName;
	    public final List<Column> columns;
	    public Struct(List<Column> columns, String flexName) {
	        this.columns = columns;
	        this.flexName = flexName;
	    }
	}
	
}