package dev.xframe.admin.sample.custom;

import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.XColumn;

public class CustomCell {

    @XColumn(enumKey = CustomComponent.DYNAMIC_CEL_TYPES)
    public int type;
    public String title;
    public int row;
    public int col;

    @XColumn(type = EColumn.Area)
    public String sql;
    //for result show
    public String datasetField;
    public String labelField;
    public String valueField;


}
