package dev.xframe.admin.sample.custom;

import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.XColumn;

import java.util.List;

public class CustomTablet {

    @XColumn(primary = true)
    public String path; // a/b or a/b/c
    public String chapterName;
    public String segmentName;
    public String tabletName;

    @XColumn(type = EColumn.List)
    public List<CustomColumn> queryColumns;
    @XColumn(type = EColumn.List)
    public List<CustomCell> contentCells;

}
