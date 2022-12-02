package dev.xframe.admin.view.details;

import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.XSegment;

public class Chart extends Classic {
    public Chart() {
        super(XSegment.type_chart);
    }
    @Override
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
        this.options = Detail.parseOptions(declaring);
        this.checkIniOption();
        return this;
    }
}
