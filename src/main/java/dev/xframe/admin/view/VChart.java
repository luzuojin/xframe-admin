package dev.xframe.admin.view;

import java.util.Arrays;
import java.util.List;

public class VChart {

    public static final int Type_Table = 0;
    public static final int Type_Line  = 1;
    public static final int Type_Bar   = 2;
    public static final int Type_Pie   = 3;

    public static final String DefaultSet = "_";

    public static VChart of(Row... rows) {
        return of(Arrays.asList(rows));
    }
    public static VChart of(List<Row> rows) {
        return new VChart(rows);
    }

    public static Row row(Col... cols) {
        return row(Arrays.asList(cols));
    }
    public static Row row(List<Col> cols) {
        return new Row(cols);
    }

    public static Col col(List<Data> datas) {
        return col(0, datas);
    }
    /**
     * @param width (total 12)
     */
    public static Col col(int width, List<Data> datas) {
        return new Col(width, datas);
    }

    public static Data data(String label, Number value) {
        return data(DefaultSet, label, value);
    }
    public static Data data(String set, String label, Number value) {
        return new Data(set, label, value);
    }

    public final List<Row> rows;
    private VChart(List<Row> rows) {
        this.rows = rows;
    }

    public static class Row {
        public final List<Col> cols;
        private Row(List<Col> cols) {
            this.cols = cols;
        }
    }

    public static class Col {
        public int type = Type_Line;

        public final int width;
        public final List<Data> datas;
        public String title;

        private Col(int width, List<Data> datas) {
            this.width = width;
            this.datas = datas;
        }
        public Col asLine() {
            this.type = Type_Line;
            return this;
        }
        public Col asPie() {
            this.type = Type_Pie;
            return this;
        }
        public Col asBar() {
            this.type = Type_Bar;
            return this;
        }
        public Col title(String title) {
            this.title = title;
            return this;
        }
    }

    public static class Data {
        public final String set;
        public final String label;
        public final Number value;
        private Data(String set, String label, Number value) {
            this.set   = set;
            this.label = label;
            this.value = value;
        }
    }

}
