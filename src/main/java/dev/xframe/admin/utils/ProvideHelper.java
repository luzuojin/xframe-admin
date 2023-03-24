package dev.xframe.admin.utils;

import dev.xframe.inject.Providable;
import dev.xframe.inject.code.Codes;

import java.util.HashMap;
import java.util.Map;

public class ProvideHelper {

    private final static Map<Class<?>, Class<?>> ProvidedClsMap = new HashMap<>();

    private static Class<?> getProvided(Class<?> p) {
        return Codes.getScannedClasses(clz -> clz.isImplementedFrom(p)).stream().findAny().orElse(p);
    }

    public static Class<?> provided(Class<?> p) {
        if(p.isAnnotationPresent(Providable.class)) {
            return ProvidedClsMap.computeIfAbsent(p, ProvideHelper::getProvided);
        }
        return p;
    }

}
