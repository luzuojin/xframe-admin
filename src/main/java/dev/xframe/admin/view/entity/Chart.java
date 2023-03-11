package dev.xframe.admin.view.entity;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XSegment;

public class Chart extends Classic {
    public Chart() {
        super(EContent.Chart);
    }
    @Override
    public Content parseFrom(XSegment xseg, Class<?> declaring) {
        this.options = Content.parseOptions(declaring, xseg.model());
        this.checkIniOption();
        return this;
    }
}
