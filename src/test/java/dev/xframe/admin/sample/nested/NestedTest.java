package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XColumn;

public class NestedTest {
	
	public static final String NESTED_TYPE = "nested_type";
	
	@XColumn(value="ID", primary=true)
	private int id;
	@XColumn("Name")
	private String name;
	@XColumn(value="Type", enumKey=NESTED_TYPE)
	private String type;
	@XColumn(value="Nest", type=XColumn.type_model)
	private NestedObj nest;
	
	public NestedTest() {
	}
	
	public NestedTest(int id, String name, String type, NestedObj nest) {
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
	public NestedObj getNest() {
		return nest;
	}
	public void setNest(NestedObj nest) {
		this.nest = nest;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
