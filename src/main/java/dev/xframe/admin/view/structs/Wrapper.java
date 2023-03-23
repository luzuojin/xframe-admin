package dev.xframe.admin.view.structs;

import dev.xframe.admin.system.BasicManager;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Prototype;

/**
 * path/{symbol}/path
 */
@Prototype
public class Wrapper extends Navi {

    @Inject
    protected BasicManager basicMgr;

    protected String parent;
    protected Navi origin;

    public Wrapper(String parent, Navi navi) {
        super(navi.path, navi.name, navi.order);
        this.parent = parent;
        this.origin = navi;
    }

    static class Symbol extends Wrapper {
        public Symbol(String parent, Navi navi) {
            super(parent, navi);
        }
        @Override
        protected void fillTo(Chapter chapter) {
            basicMgr.getNaviValue(parent).stream().map(origin::duplicateBy).forEach(chapter.getNavis()::add);
        }
    }

    static class Middle extends Wrapper {//simple middle navi
        public Middle(String parent, Navi navi) {
            super(parent, navi);
        }
        @Override
        protected void fillTo(Chapter chapter) {
            basicMgr.getNaviValue(parent).stream().filter(n->n.path.equals(this.path)).map(origin::duplicateBy).forEach(chapter.getNavis()::add);
        }
    }

    static Navi unwrap(Navi navi) {
        return navi instanceof Wrapper ? ((Wrapper) navi).origin : navi;
    }
    static Navi wrap(String parent, Navi navi) {
        return isSymbolNavi(navi) ? new Symbol(parent, navi) : (isMiddleNavi(navi) ? new Middle(parent, navi) : navi);
    }
    private static boolean isMiddleNavi(Navi navi) {
        return !(navi instanceof Segment);
    }
    private static boolean isSymbolNavi(Navi navi) {
        return navi.path.startsWith("{") && navi.path.endsWith("}");
    }
}
