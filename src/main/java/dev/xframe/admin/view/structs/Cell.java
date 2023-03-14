package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.XCell;

public class Cell implements Comparable<Cell> {
    private String path;
    private String title;
    private int type;
    private int row;
    private int col;
    private int pIndex; //path index
    public String getPath() {
        return path;
    }
    public String getTitle() {
        return title;
    }
    public int getpIndex() {
        return pIndex;
    }
    public int getType() {
        return type;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public int getPIndex() {
        return pIndex;
    }
    @Override
    public int compareTo(Cell o) {
        return Integer.compare(this.row, o.row);
    }
    public static Cell of(String path, XCell xc) {
        return of(path, xc, 0);
    }
    public static Cell of(String path, XCell xc, int pIndex) {
        Cell cell = new Cell();
        cell.path = path;
        cell.title= xc.title();
        cell.type = xc.type().val;
        cell.row  = xc.row();
        cell.col  = xc.col();
        cell.pIndex=pIndex;
        return cell;
    }
}
