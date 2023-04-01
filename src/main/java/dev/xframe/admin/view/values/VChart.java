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

    public static VData metadata(List<Object> datas) {
        return metadata("", datas);
    }
    public static VData metadata(Object label, List<Object> datas) {
        return new VData(label, datas);
    }

    public static VData dataset(List<Object> datas) {
        return dataset("", datas);
    }
    public static VData dataset(Object label, List<Object> datas) {
        return new VData(label, datas);
    }

    public static class VData {
        public final Object label;
        public final List<Object> datas;
        public VData(Object label, List<Object> datas) {
            this.label = label;
            this.datas = datas;
        }
    }
}



