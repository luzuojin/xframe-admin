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
        return VChart.of(
                VChart.metadata(Arrays.asList("Spring", "Summer", "Autumn", "Winter")),
            Arrays.asList(
                VChart.dataset("setOne", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100))),
                VChart.dataset("setTwo", Arrays.asList(r.nextInt(100), r.nextInt(100), r.nextInt(100), r.nextInt(100)))
            ));
    }

}
