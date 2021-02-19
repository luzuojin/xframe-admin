package dev.xframe.admin.utils;

import java.util.List;

public interface JsonBridge {
    String toJSONString(Object obj);
    
    String toPrettyString(Object obj);

    <T> List<T> parseArray(String text, Class<T> clz);

    <T> T parseObject(String text, Class<T> clz);

    <T> T parseObject(byte[] bytes, Class<T> clz);
}