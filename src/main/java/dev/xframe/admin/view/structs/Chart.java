package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XSegment;

public class Chart extends Classic {
    private int chartType;
    private String chartTitle;
    public Chart() {
        super(EContent.Chart);
    }
    @Override
    public Content parseFrom(XSegment xseg, Class<?> declaring) {
        this.options = Content.parseOptions(declaring, xseg.model());
        this.checkIniOption();
        this.chartType = xseg.chart();
        this.chartTitle = xseg.desc();
        return this;
    }
    public int getChartType() {
        return chartType;
    }
    public String getChartTitle() {
        return chartTitle;
    }
}
