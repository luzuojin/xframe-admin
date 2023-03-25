package dev.xframe.admin.view;

import dev.xframe.admin.view.structs.Content;
import dev.xframe.admin.view.structs.IFactory;

import java.util.function.Supplier;

public interface EContent {

    int Table = 1;
    int Panel = 2;
    int Markd = 3;
    int Chart = 4;
    int Cells = 5;

    IFactory<Content> Factory = new IFactory<Content>() {
        {//builtin types
            regist(Table, dev.xframe.admin.view.structs.Table::new);
            regist(Panel, dev.xframe.admin.view.structs.Panel::new);
            regist(Markd, dev.xframe.admin.view.structs.Markd::new);
            regist(Chart, dev.xframe.admin.view.structs.Chart::new);
            regist(Cells, dev.xframe.admin.view.structs.Cells::new);
        }
    };
    static void regist(int type, Supplier<Content> tFactory) {
        Factory.regist(type, tFactory);
    }
}
