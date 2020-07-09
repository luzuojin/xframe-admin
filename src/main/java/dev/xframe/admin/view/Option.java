package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.utils.XStrings;

public class Option implements Comparable<Option> {
	
	public static final Option qry = new Option("查询", XOption.type_qry);   //HttpMethods.GET
	public static final Option edt = new Option("修改", XOption.type_edt);   //HttpMethods.PUT
	public static final Option add = new Option("新增", XOption.type_add);   //HttpMethods.POST
	public static final Option del = new Option("删除", XOption.type_del);   //HttpMethods.DELETE
	
	private String name;
	private List<Column> inputs = new ArrayList<>();
	private int opType; //1(增), 2(查)
	
	public Option(String name, int opType) {
		this.name = name;
		this.opType = opType;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public Option copy(XOption op) {
		String xname = op == null ? null : op.value();
		return new Option(XStrings.orElse(xname, this.name), opType);
	}
	public Option with(List<Column> columns) {
	    this.inputs = columns;
	    return this;
	}
	@Override
	public int compareTo(Option o) {
		return Integer.compare(opType, o.opType);
	}
	
}
