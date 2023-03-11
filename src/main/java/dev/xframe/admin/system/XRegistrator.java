package dev.xframe.admin.system;

import dev.xframe.admin.view.structs.Navi;
import dev.xframe.admin.view.values.VEnum;

import java.util.List;
import java.util.function.Supplier;

public interface XRegistrator {

    void registEnumValue(String key, Supplier<List<VEnum>> func);

    void registNaviValue(String key, Supplier<List<Navi>> func);

}
