package dev.xframe.admin.view;

import dev.xframe.admin.view.structs.Content;

import java.util.function.Supplier;

public enum EContent {
    Table       (1, dev.xframe.admin.view.structs.Table::new),
    Panel       (2, dev.xframe.admin.view.structs.Panel::new),
    Markd       (3, dev.xframe.admin.view.structs.Markd::new),
    Chart       (4, dev.xframe.admin.view.structs.Chart::new),
    Cells       (5, dev.xframe.admin.view.structs.Cells::new),
    ;
    public final int val;
    public final Supplier<Content> fac;
    EContent(int val, Supplier<Content> fac) {
        this.val = val;
        this.fac = fac;
    }
}
