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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rest("chart/tabs1/chart1")
@XSegment(name = "单图Content", type = EContent.Chart, desc = "DAU...")
public class DataService {

    @HttpMethods.GET
    public Object ini() {
        Random r = new Random();
        return VChart.of(
                    VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                    VChart.dataset("setOne", Stream.of(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)).map(Object::toString).collect(Collectors.toList())),
                    VChart.dataset("setTwo", Stream.of(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)).map(Object::toString).collect(Collectors.toList()))
                ));
    }

    @HttpMethods.GET("fetch")
    public Object fetch(@HttpArgs.Param LocalDate date) {
        Random r = new Random();
        LocalDate other = date.plusDays(1);
        return VChart.of(
                    VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
                Arrays.asList(
                    VChart.dataset(date.toString(), Stream.of(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)).map(Object::toString).collect(Collectors.toList())),
                    VChart.dataset(other.toString(), Stream.of(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)).map(Object::toString).collect(Collectors.toList()))
                ));
    }
}
