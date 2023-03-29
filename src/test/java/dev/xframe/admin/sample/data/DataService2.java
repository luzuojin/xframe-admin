package dev.xframe.admin.sample.data;


import dev.xframe.admin.view.EChart;
import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XCell;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.values.VChart;
import dev.xframe.http.response.ContentType;
import dev.xframe.http.response.FileResponse;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.utils.XReflection;
import dev.xframe.utils.XStrings;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Rest("chart/tabs2/chart2")
@XSegment(name = "多图组合", type = EContent.Cells)
public class DataService2 {
    Random r = new Random();

    @XCell(row = 1, col = 4)
    @HttpMethods.GET("a")
    public Object a() {
        return VChart.of(
                    VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                    VChart.dataset("setOne", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))),
                    VChart.dataset("setTwo", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                ));
    }

    @XCell(row = 1, col = 4, type = EChart.Markd)
    @HttpMethods.GET("b")
    public Object b() {
        return XStrings.readFrom(XReflection.getResourceAsStream("sysops.md"));
    }

    @XCell(row = 1, col = 4, title = "DAU...", type = EChart.Bar)
    @HttpMethods.GET("f")
    public Object f() {
        return VChart.of(
                VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                        VChart.dataset(Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                ));
    }

    @XCell(row = 2, type = EChart.Table)
    @HttpMethods.GET("c")
    public Object c(@HttpArgs.Param LocalDate date) {
        if(date == null) date = LocalDate.now();
        LocalDate other = date.plusDays(1);
        Random r = new Random();
        return VChart.of(
                    VChart.metadata("Date", Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                    VChart.dataset(date.toString(), Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))),
                    VChart.dataset(other.toString(), Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                ));
    }

    @HttpMethods.GET("c/dl")
    @XOption(type=EOption.Dlr)
    public Object download(@HttpArgs.Param LocalDate date) {
        if(date == null) date = LocalDate.now();
        return new FileResponse.Binary(ContentType.FILE, date.toString().getBytes(StandardCharsets.UTF_8)).setFileName("dl.txt");
    }

    @XCell(row = 3, type = EChart.Pie, col = 4)
    @XCell(row = 3, type = EChart.Line)
    @HttpMethods.GET("m")
    public Object multi() {
       return Arrays.asList(
               VChart.of(
                       VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                   Arrays.asList(
                       VChart.dataset(Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                   )),
               VChart.of(
                       VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                   Arrays.asList(
                       VChart.dataset("setOne", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))),
                       VChart.dataset("setTwo", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                   )));
    }

    @XCell(row = 0, col = 3, type = EChart.Number)
    @XCell(row = 0, col = 3, type = EChart.Number)
    @XCell(row = 0, col = 3, type = EChart.Number)
    @XCell(row = 0, col = 3, type = EChart.Number)
    @HttpMethods.GET("mkddd")
    public Object mkddd() {
        return Arrays.asList(
            VChart.of(VChart.metadata(Arrays.asList("Spring")), VChart.dataset("#6610f2", Arrays.asList(r.nextInt(100000)))),
            VChart.of(VChart.metadata(Arrays.asList("Summer")), VChart.dataset("#info", Arrays.asList(r.nextInt(100000)))),
            VChart.of(VChart.metadata(Arrays.asList("Autumn")), VChart.dataset("#ffc107", Arrays.asList(r.nextInt(100000)))),
            VChart.of(VChart.metadata(Arrays.asList("Winter")), VChart.dataset("#pink", Arrays.asList(r.nextInt(100000))))
        );
    }
}
