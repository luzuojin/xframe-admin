package dev.xframe.admin.view;

public enum EChart {
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
