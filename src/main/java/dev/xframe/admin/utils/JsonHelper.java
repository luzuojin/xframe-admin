package dev.xframe.admin.utils;

import java.util.List;

import dev.xframe.utils.XCaught;
import dev.xframe.utils.XProperties;
import dev.xframe.utils.XReflection;

public class JsonHelper {
    
    private static final JsonBridge bridge;
    static {
        try {
            String clzName = XProperties.get("xframe.admin.jsonbridge", "dev.xframe.admin.utils.FastJsonBridge");
            bridge = XReflection.newInstance(Class.forName(clzName));
        } catch (ClassNotFoundException e) {
            throw XCaught.wrapException(e);
        }
    }
    
    public static String toJSONString(Object obj) {
        return bridge.toJSONString(obj);
    }
    public static String toPrettyString(Object obj) {
        return bridge.toPrettyString(obj);
    }
    
    public static <T> List<T> parseArray(String text, Class<T> clz) {
        return bridge.parseArray(text, clz);
    }
    public static <T> T parseObject(String text, Class<T> clz) {
        return bridge.parseObject(text, clz);
    }
    public static <T> T parseObject(byte[] bytes, Class<T> clz) {
        return bridge.parseObject(bytes, clz);
    }

}
