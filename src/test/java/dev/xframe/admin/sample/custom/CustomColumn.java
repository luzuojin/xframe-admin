package dev.xframe.admin.sample.custom;

import dev.xframe.admin.view.XColumn;

public class CustomColumn {

    @XColumn(enumKey = CustomComponent.DYNAMIC_COL_TYPES)
    public int type;
    public String key;
    public String name;

}
