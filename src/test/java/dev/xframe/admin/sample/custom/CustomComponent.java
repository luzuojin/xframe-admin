package dev.xframe.admin.sample.custom;

import dev.xframe.admin.system.SystemRepo;
import dev.xframe.admin.system.XRegistrator;
import dev.xframe.admin.view.EChart;
import dev.xframe.admin.view.EColumn;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.structs.Cell;
import dev.xframe.admin.view.structs.Cells;
import dev.xframe.admin.view.structs.Chapter;
import dev.xframe.admin.view.structs.Column;
import dev.xframe.admin.view.structs.Navi;
import dev.xframe.admin.view.structs.Navigate;
import dev.xframe.admin.view.structs.Option;
import dev.xframe.admin.view.structs.Segment;
import dev.xframe.admin.view.values.VEnum;
import dev.xframe.inject.Component;
import dev.xframe.inject.Inject;
import dev.xframe.inject.Loadable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class CustomComponent implements Loadable {

    public static final String DYNAMIC_COL_TYPES = "dynamic_col_types";
    public static final String DYNAMIC_CEL_TYPES = "dynamic_cel_types";

    @Inject
    XRegistrator xReg;
    @Inject
    SystemRepo systemRepo;

    Map<Integer, CustomCell> customCells = new HashMap<>();
    List<Chapter> customChapters = new ArrayList<>();

    @Override
    public void load() {
        List<VEnum> colEnums = Arrays.asList(
                new VEnum(EColumn.Text.val, "文本"),
                new VEnum(EColumn.Number.val, "数字"),
                new VEnum(EColumn.Date.val, "日期"),
                new VEnum(EColumn.Time.val, "时间"),
                new VEnum(EColumn.Datetime.val, "日期&时间")
        );
        List<VEnum> cttEnums = Arrays.asList(
                new VEnum(EChart.Table.val, "表格"),
                new VEnum(EChart.Line.val, "折线图"),
                new VEnum(EChart.Pie.val, "饼图"),
                new VEnum(EChart.Bar.val, "柱状图"),
                new VEnum(EChart.Markd.val, "Markdown")
        );
        xReg.registEnumValue(DYNAMIC_COL_TYPES, () -> colEnums);
        xReg.registEnumValue(DYNAMIC_CEL_TYPES, () -> cttEnums);

        xReg.registChapters(this::getCustomChapters);

        onTabletChanged();
    }

    private List<Chapter> getCustomChapters() {
        return this.customChapters;
    }

    public void onTabletChanged() {
        AtomicInteger cellUnique = new AtomicInteger();
        Map<Integer, CustomCell> customCells = new HashMap<>();

        List<CustomTablet> tablets = CustomConfigService.getTablets(systemRepo);
        Map<String, Chapter> chapters = new LinkedHashMap<>();
        for (CustomTablet tablet : tablets) {
            String[] paths = tablet.path.split("/");
            Chapter chapter = chapters.computeIfAbsent(paths[0], k -> new Chapter(k, tablet.chapterName, 10));
            Optional<Navi> navi = chapter.getNavis().stream().filter(n -> n.getPath().equals(paths[1])).findAny();
            if(!navi.isPresent()) {
                Navi n = new Navigate(paths[1], tablet.segmentName, 10);
                chapter.getNavis().add(n);
                navi = Optional.of(n);
            }
            Cells cells = new Cells();
            ((Navigate) navi.get()).getNavis().add(new Segment(paths[2], tablet.tabletName, 10, cells));

            List<Cell> cList = new ArrayList<>();
            List<Option> oList = new ArrayList<>();
            for (int i = 0; i < tablet.contentCells.size(); i++) {
                CustomCell dCell = tablet.contentCells.get(i);
                int id = cellUnique.incrementAndGet();
                customCells.put(id, dCell);
                String path = String.valueOf(id);
                cList.add(Cell.of(path, dCell.title, dCell.type, dCell.row, dCell.col));
                oList.add(new Option("ini", EOption.Ini, path));
            }
            oList.add(new Option("查询", EOption.Qry, "0")
                    .with(tablet.queryColumns.stream()
                            .map(c-> new Column(c.key, c.type, c.name, ""))
                            .collect(Collectors.toList())));
            cells.setCells(cList);
            cells.setOptions(oList);
        }

        this.customCells = customCells;
        this.customChapters.clear();
        this.customChapters.addAll(chapters.values());
    }
}
