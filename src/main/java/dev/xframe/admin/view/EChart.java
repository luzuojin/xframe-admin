package dev.xframe.admin.view;

public enum EChart {
    Markd   (-1),   //only used by CellsContent: @XCell.type
    Table   (0),
    Line    (1),
    Bar     (2),
    Pie     (3),
    ;
    public final int val;
    EChart(int val) {
        this.val = val;
    }
}
