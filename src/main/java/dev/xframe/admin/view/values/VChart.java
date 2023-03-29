package dev.xframe.admin.view.values;

import java.util.Arrays;
import java.util.List;

/**
 * chart data
 */
public class VChart {

    public final VData<String> metadata;
    public final List<VData<Number>> datasets;
    public VChart(VData<String> metadata, List<VData<Number>> datasets) {
        this.metadata = metadata;
        this.datasets = datasets;
    }

    @SafeVarargs
    public static VChart of(VData<String> metadata, VData<Number>... datasets) {
        return of(metadata, Arrays.asList(datasets));
    }
    public static VChart of(VData<String> metadata, List<VData<Number>> datasets) {
        return new VChart(metadata, datasets);
    }

    public static VData<String> metadata(List<String> datas) {
        return metadata("", datas);
    }
    public static VData<String> metadata(String label, List<String> datas) {
        return new VData<String>(label, datas);
    }

    public static VData<Number> dataset(List<Number> datas) {
        return dataset("", datas);
    }
    public static VData<Number> dataset(String label, List<Number> datas) {
        return new VData<>(label, datas);
    }

    public static class VData<T> {
        public final String label;
        public final List<T> datas;
        public VData(String label, List<T> datas) {
            this.label = label;
            this.datas = datas;
        }
    }
}



