package dev.xframe.admin.view.structs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//结构变化的结果
public class Variant {
    //可以用来识别变化之后的结构类
    //Http.header("variant-name")
    public static final String HEADER_KEY = "variant-name";
    
    public final Struct struct;
    public final Object data;
	
	public Variant(Struct struct, Object data) {
	    this.struct = struct;
	    this.data = data;
    }
    public static Variant data(Object data) {
        return new Variant(null, data);
	}
    public static Variant struct(Class<?> model) {
        return new Variant(makeStruct(model), null);
    }
	public static Variant structAndData(Object data) {
	    return structAndData(data.getClass(), data);
	}
	public static Variant structAndData(Class<?> model, Object data) {
	    return new Variant(makeStruct(model), data);
	}

	private static final Map<Class<?>, List<Column>> caches = new HashMap<>();
    private static Struct makeStruct(Class<?> model) {
        return new Struct(caches.computeIfAbsent(model, Content::parseModelColumns), model.getName());
    }
    public static class Struct {
	    //Http.header("variant-name")
	    public final String variantName;
	    public final List<Column> columns;
	    public Struct(List<Column> columns, String variantName) {
	        this.columns = columns;
	        this.variantName = variantName;
	    }
	}
	
}