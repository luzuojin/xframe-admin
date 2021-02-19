package dev.xframe.admin.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonBridge implements JsonBridge {
    public final SerializerFeature[] DefaultFeatures = new SerializerFeature[] {
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.SkipTransientField,
            SerializerFeature.DisableCircularReferenceDetect };
    public final SerializerFeature[] PrettyFeatures = new SerializerFeature[] {
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.SkipTransientField,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.PrettyFormat};
    @Override
    public String toJSONString(Object obj) {
        return JSON.toJSONString(obj, DefaultFeatures);
    }
    @Override
    public String toPrettyString(Object obj) {
        return JSON.toJSONString(obj, PrettyFeatures);
    }
    @Override
    public <T> List<T> parseArray(String text, Class<T> clz) {
        return JSON.parseArray(text, clz);
    }
    @Override
    public <T> T parseObject(String text, Class<T> clz) {
        return JSON.parseObject(text, clz);
    }
    @Override
    public <T> T parseObject(byte[] bytes, Class<T> clz) {
        return JSON.parseObject(bytes, clz);
    }
}