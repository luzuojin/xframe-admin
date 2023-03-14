package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.XCell;
import dev.xframe.admin.view.XCells;
import dev.xframe.admin.view.XSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        for (Option option : this.options) {
            if(option.method.isAnnotationPresent(XCell.class) || option.method.isAnnotationPresent(XCells.class)) {
                XCell[] xCells = option.method.getAnnotationsByType(XCell.class);
                for (int i = 0; i < xCells.length; i++) {
                    cells.add(Cell.of(option.getPath(), xCells[i], i));
                }
            }
        }
        Collections.sort(cells);
        return this;
    }
}
