package dev.xframe.admin.sample.data;


import dev.xframe.admin.view.EChart;
import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.values.VChart;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Rest("chart/tabs1/chart1")
@XSegment(name = "单图Content", type = EContent.Chart, desc = "DAU...", chart = EChart.Table)
public class DataService {

    @HttpMethods.GET
    public Object ini() {
        Random r = new Random();
        return VChart.of(
                    VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                    VChart.dataset("setOne", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))),
                    VChart.dataset("setTwo", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                ));
    }
    @HttpMethods.GET("fetch")
    public Object fetch(@HttpArgs.Param LocalDate date) {
        Random r = new Random();
        LocalDate other = date.plusDays(1);
        return VChart.of(
                    VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                    VChart.dataset(date.toString(), Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))),
                    VChart.dataset(other.toString(), Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
                ));
    }
}
