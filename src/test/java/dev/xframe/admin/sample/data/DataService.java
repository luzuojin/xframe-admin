package dev.xframe.admin.sample.data;


import dev.xframe.admin.view.EChart;
import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XSegment;
import dev.xframe.admin.view.values.VChart;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpMethods;

import java.util.Arrays;
import java.util.Random;

@Rest("chart/chart1")
@XSegment(name = "图表测试1", type = EContent.Chart, desc = "DAU...", chart = EChart.Table)
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

}
