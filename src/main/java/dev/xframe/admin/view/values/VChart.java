package dev.xframe.admin.view.values;

import java.util.Arrays;
import java.util.List;

/**
 * chart data
 */
public class VChart {

    public final VData metadata;
    public final List<VData> datasets;
    public VChart(VData metadata, List<VData> datasets) {
        this.metadata = metadata;
        this.datasets = datasets;
    }

    public static VChart of(VData metadata, VData... datasets) {
        return of(metadata, Arrays.asList(datasets));
    }
    public static VChart of(VData metadata, List<VData> datasets) {
        return new VChart(metadata, datasets);
    }

    public static VData metadata(List<String> datas) {
        return metadata("", datas);
    }
    public static VData metadata(String label, List<String> datas) {
        return new VData(label, datas);
    }

    public static VData dataset(List<String> datas) {
        return dataset("", datas);
    }
    public static VData dataset(String label, List<String> datas) {
        return new VData(label, datas);
    }

    public static class VData {
        public final String label;
        public final List<String> datas;
        public VData(String label, List<String> datas) {
            this.label = label;
            this.datas = datas;
        }
    }
}



