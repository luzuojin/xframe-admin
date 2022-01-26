package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XColumn;

public class NestedTest2 {
	
	public static final String NESTED_TYPE = "nested_type";
	
	@XColumn(value="ID", primary=true)
	private int id;
	@XColumn(value="Name")
	private String name;
	@XColumn(value="Type", enumKey=NESTED_TYPE)
	private String type;
	@XColumn(value="Nest")
	private NestedObj2 nest;
	
	public NestedTest2() {
	}
	
	public NestedTest2(int id, String name, String type, NestedObj2 nest) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.nest = nest;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public NestedObj2 getNest() {
		return nest;
	}
	public void setNest(NestedObj2 nest) {
		this.nest = nest;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
