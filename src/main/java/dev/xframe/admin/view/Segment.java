package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

public class Segment {
	
	private String name;
	private String path;
	private List<Column> columns = new ArrayList<>();
	private List<Option> options = new ArrayList<>();
	
	private boolean padding;
	private boolean listable;
	
	public Segment(String name, String path, boolean padding) {
		this.name = name;
		this.path = path;
		this.padding = padding;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	public boolean getListable() {
	    return listable;
	}
    public void setListable(boolean listable) {
        this.listable = listable;
    }
	public boolean getPadding() {
		return padding;
	}
	public void setPadding(boolean padding) {
		this.padding = padding;
	}
	
}
