package dev.xframe.admin.view.structs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class IFactory<T> {

    final Map<Integer, Supplier<T>> factories = new HashMap<>();

    public void regist(int type, Supplier<T> tFactory) {
        factories.put(type,tFactory);
    }

    public T newInstance(int type) {
        return factories.get(type).get();
    }

}
