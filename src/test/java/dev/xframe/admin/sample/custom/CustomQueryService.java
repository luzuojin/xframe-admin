package dev.xframe.admin.sample.custom;

import dev.xframe.admin.view.values.VChart;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

import java.util.Arrays;
import java.util.Random;

@Rest("{chapter}/{segment}/{tablet}/{cell}")
public class CustomQueryService {

    @HttpMethods.GET
    public Object get(@HttpArgs.Path int cell) {
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
