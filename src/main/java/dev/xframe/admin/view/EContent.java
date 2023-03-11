package dev.xframe.admin.view;

import dev.xframe.admin.view.entity.Content;

import java.util.function.Supplier;

public enum EContent {
    Table       (1, dev.xframe.admin.view.entity.Table::new),
    Panel       (2, dev.xframe.admin.view.entity.Panel::new),
    Markd       (3, dev.xframe.admin.view.entity.Markd::new),
    Chart       (4, dev.xframe.admin.view.entity.Chart::new),
    Cells       (5, dev.xframe.admin.view.entity.Cells::new),
    ;
    public final int val;
    public final Supplier<Content> fac;
    EContent(int val, Supplier<Content> fac) {
        this.val = val;
        this.fac = fac;
    }
}
