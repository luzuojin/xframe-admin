package dev.xframe.admin.view.values;

import java.util.List;
import java.util.stream.Collectors;

/**
 * chart data
 */
public interface VChart {

    String set();   //dataset
    String label(); //label
    Number value(); //value

    String DefaultDataset = "_";

    static VChart of(String label, Number value) {
        return of(DefaultDataset, label, value);
    }
    static VChart of(String set, String label, Number value) {
        return new VData(set, label, value);
    }
    static <T extends VChart> List<VChart> of(List<T> datas) {
        return datas.stream().map(v->new VData(v.set(), v.label(), v.value())).collect(Collectors.toList());
    }
}

class VData implements VChart {
    public final String set;
    public final String label;
    public final Number value;
    VData(String set, String label, Number value) {
        this.set   = set;
        this.label = label;
        this.value = value;
    }
    @Override
    public String set() {return set;}
    @Override
    public String label() {return label;}
    @Override
    public Number value() {return value;}
}

