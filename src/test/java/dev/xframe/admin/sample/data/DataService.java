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

    @HttpMethods.GET("fetch")
    public Object fetch(@HttpArgs.Param LocalDate date) {
        Random r = new Random();
        LocalDate other = date.plusDays(1);
        return Arrays.asList(
                VChart.of(date.toString(), "Spring", r.nextInt(100)),
                VChart.of(date.toString(), "Summer", r.nextInt(100)),
                VChart.of(date.toString(), "Autumn", r.nextInt(100)),
                VChart.of(date.toString(), "Winter", r.nextInt(100)),
                VChart.of(other.toString(), "Spring", r.nextInt(100)),
                VChart.of(other.toString(), "Summer", r.nextInt(100)),
                VChart.of(other.toString(), "Autumn", r.nextInt(100)),
                VChart.of(other.toString(), "Winter", r.nextInt(100)));
    }

}
