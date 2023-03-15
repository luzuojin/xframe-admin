package dev.xframe.admin.sample.data;


import dev.xframe.admin.view.EChart;
import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XCell;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.values.VChart;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;

import java.util.Arrays;
import java.util.Random;

@Rest("chart/tabs2/chart2")
@XSegment(name = "多图组合", type = EContent.Cells)
public class DataService2 {
    Random r = new Random();

    @XCell(row = 1, col = 6)
    @HttpMethods.GET("a")
    public Object a() {
        return Arrays.asList(
                VChart.of("setOne", "Spring", r.nextInt(100)),
                VChart.of("setOne", "Summer", r.nextInt(100)),
                VChart.of("setOne", "Autumn", r.nextInt(100)),
                VChart.of("setOne", "Winter", r.nextInt(100)),
                VChart.of("setTwo", "Spring", r.nextInt(100)),
                VChart.of("setTwo", "Summer", r.nextInt(100)),
                VChart.of("setTwo", "Autumn", r.nextInt(100)),
                VChart.of("setTwo", "Winter", r.nextInt(100)));
    }

    @XCell(row = 1, col = 6, title = "DAU...")
    @HttpMethods.GET("b")
    public Object b() {
        return Arrays.asList(
                VChart.of("Spring", r.nextInt(100)),
                VChart.of("Summer", r.nextInt(100)),
                VChart.of("Autumn", r.nextInt(100)),
                VChart.of("Winter", r.nextInt(100)));
    }

    @XCell(row = 2, type = EChart.Table)
    @HttpMethods.GET("c")
    public Object c() {
        return Arrays.asList(
                VChart.of("ThSpring", r.nextInt(100)),
                VChart.of("ThSummer", r.nextInt(100)),
                VChart.of("ThAutumn", r.nextInt(100)),
                VChart.of("ThWinter", r.nextInt(100)),
                VChart.of("AnSpring", r.nextInt(100)),
                VChart.of("AnSummer", r.nextInt(100)),
                VChart.of("AnAutumn", r.nextInt(100)),
                VChart.of("AnWinter", r.nextInt(100)));
    }

    @XCell(row = 3, type = EChart.Pie)
    @XCell(row = 3, type = EChart.Bar)
    @HttpMethods.GET("m")
    public Object multi() {
       return Arrays.asList(Arrays.asList(
                    VChart.of("Spring", r.nextInt(100)),
                    VChart.of("Summer", r.nextInt(100)),
                    VChart.of("Autumn", r.nextInt(100)),
                    VChart.of("Winter", r.nextInt(100))),
               Arrays.asList(
                       VChart.of("setOne", "Spring", r.nextInt(100)),
                       VChart.of("setOne", "Summer", r.nextInt(100)),
                       VChart.of("setOne", "Autumn", r.nextInt(100)),
                       VChart.of("setOne", "Winter", r.nextInt(100)),
                       VChart.of("setTwo", "Spring", r.nextInt(100)),
                       VChart.of("setTwo", "Summer", r.nextInt(100)),
                       VChart.of("setTwo", "Autumn", r.nextInt(100)),
                       VChart.of("setTwo", "Winter", r.nextInt(100))));
    }

}
