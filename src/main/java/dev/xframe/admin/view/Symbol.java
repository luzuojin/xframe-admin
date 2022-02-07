package dev.xframe.admin.view;

import dev.xframe.admin.system.BasicContext;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Prototype;

/**
 * path/{symbol}/path
 */
@Prototype
public class Symbol extends Navi {

    @Inject
    private BasicContext basicCtx;

    private String parent;

    private Navi wrapped;

    public Symbol(String parent, Navi navi) {
        super(navi.name, navi.path, navi.order);
        this.parent = parent;
        this.wrapped = navi;
    }

    @Override
    protected void fillTo(Chapter chapter) {
        basicCtx.getNaviValue(parent).stream().map(wrapped::duplicateBy).forEach(chapter.getNavis()::add);
    }

    static Navi unwrap(Navi navi) {
        return navi instanceof Symbol ? ((Symbol) navi).wrapped : navi;
    }
    static Navi wrap(String parent, Navi navi) {
        return isSymbolPah(navi.path) ? new Symbol(parent, navi) : navi;
    }
    static boolean isSymbolPah(String path) {
        return path.startsWith("{") && path.endsWith("}");
    }
}
