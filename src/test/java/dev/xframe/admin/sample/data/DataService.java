package dev.xframe.admin.sample.data;


import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.values.VChart;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;

import java.util.Arrays;
import java.util.Random;

@Rest("chart/chart1")
@XSegment(name = "图表测试1", type = EContent.Chart)
public class DataService {

    @HttpMethods.GET
    public Object ini() {
        Random r = new Random();
        return VChart.of(
                VChart.row(
                        VChart.col(Arrays.asList(
                                VChart.data("setOne", "Spring", r.nextInt(100)),
                                VChart.data("setOne", "Summer", r.nextInt(100)),
                                VChart.data("setOne", "Autumn", r.nextInt(100)),
                                VChart.data("setOne", "Winter", r.nextInt(100)),
                                VChart.data("setTwo", "Spring", r.nextInt(100)),
                                VChart.data("setTwo", "Summer", r.nextInt(100)),
                                VChart.data("setTwo", "Autumn", r.nextInt(100)),
                                VChart.data("setTwo", "Winter", r.nextInt(100)))),
                        VChart.col(Arrays.asList(VChart.data("Spring", r.nextInt(100)),
                                VChart.data("Summer", r.nextInt(100)),
                                VChart.data("Autumn", r.nextInt(100)),
                                VChart.data("Winter", r.nextInt(100)))).title("DAU...").asBar()
                ),
                VChart.row(
                        VChart.col(Arrays.asList(
                                VChart.data("ThSpring", r.nextInt(100)),
                                VChart.data("ThSummer", r.nextInt(100)),
                                VChart.data("ThAutumn", r.nextInt(100)),
                                VChart.data("ThWinter", r.nextInt(100)),
                                VChart.data("AnSpring", r.nextInt(100)),
                                VChart.data("AnSummer", r.nextInt(100)),
                                VChart.data("AnAutumn", r.nextInt(100)),
                                VChart.data("AnWinter", r.nextInt(100))))
                ),
                VChart.row(
                        VChart.col(Arrays.asList(VChart.data("Spring", r.nextInt(100)),
                                VChart.data("Summer", r.nextInt(100)),
                                VChart.data("Autumn", r.nextInt(100)),
                                VChart.data("Winter", r.nextInt(100)))).asPie(),
                        VChart.col(Arrays.asList(VChart.data("Spring", r.nextInt(100)),
                                VChart.data("Summer", r.nextInt(100)),
                                VChart.data("Autumn", r.nextInt(100)),
                                VChart.data("Winter", r.nextInt(100)))).asPie()
                )
        );
    }

}
