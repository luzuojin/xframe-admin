package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XCell;
import dev.xframe.admin.view.XCells;
import dev.xframe.admin.view.XSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cells extends Classic {
    List<Cell> cells = new ArrayList<>();
    public Cells() {
        super(EContent.Cells);
    }
    public List<Cell> getCells() {
        return cells;
    }
    @Override
    public Content parseFrom(XSegment xseg, Class<?> declaring) {
        this.options = Content.parseOptions(declaring, xseg.model());
        List<Column> qryColumns = this.options.stream()
                .peek(this::parseCells)
                .filter(op -> op.getType() == EOption.Qry)
                .peek(op -> op.setType(EOption.Ini))
                .flatMap(op -> op.getColumns().stream())
                .collect(Collectors.groupingBy(Column::getKey, HashMap::new, Collectors.toList()))
                .values().stream()
                .map(list -> list.stream().findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        Collections.sort(cells);
        this.options.add(new Option("查询", EOption.Qry).with(qryColumns));
        return this;
    }

    private void parseCells(Option option) {
        if(option.method.isAnnotationPresent(XCell.class) || option.method.isAnnotationPresent(XCells.class)) {
            XCell[] xCells = option.method.getAnnotationsByType(XCell.class);
            for (int i = 0; i < xCells.length; i++) {
                cells.add(Cell.of(option.getPath(), xCells[i], i));
            }
        }
    }
}
