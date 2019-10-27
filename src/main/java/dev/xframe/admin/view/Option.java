package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

public class Option implements Comparable<Option> {
	
	public static final int type_query = 1;
	public static final int type_add = 2;
	public static final int type_edit = 3;
	public static final int type_delete = 4;
	
	public static final Option edit = new Option("修改", "edit", type_edit);
	public static final Option add = new Option("新增", "add", type_add);
	public static final Option del = new Option("删除", "delete", type_delete);
	
	public static final Option qur() {
		return new Option("查询", "query", type_query);
	}
	
	private String name;
	private String path;
	private List<Column> inputs = new ArrayList<>();
	private int opType; //1(增), 2(查)
	
	public Option(String name, String path, int opType) {
		this.name = name;
		this.path = path;
		this.opType = opType;
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
	public List<Column> getInputs() {
		return inputs;
	}
	public void setInputs(List<Column> inputs) {
		this.inputs = inputs;
	}
	public int getOpType() {
		return opType;
	}
	public void setOpType(int opType) {
		this.opType = opType;
	}

	@Override
	public int compareTo(Option o) {
		return Integer.compare(opType, o.opType);
	}
	
}
