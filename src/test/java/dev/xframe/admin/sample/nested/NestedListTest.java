package dev.xframe.admin.sample.nested;

import dev.xframe.admin.view.XColumn;

import java.util.Arrays;
import java.util.List;

public class NestedListTest {
	
	@XColumn(value="ID", primary=true, sortable=true)
	private int id;
	@XColumn(value="Name", sortable=true)
	private String name;
	@XColumn(type=XColumn.type_email, required=true)
	private String email;
	@XColumn(value="Nest", compact=true, collapse=true)
	private List<NestedObj> nest;
	@XColumn("Desc")
	private String desc;
	
	public NestedListTest() {
	}
	
	public NestedListTest(int id, String name, String desc, NestedObj... nest) {
		this.id = id;
		this.name = name;
		this.nest = Arrays.asList(nest);
		this.desc = desc;
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
	public List<NestedObj> getNest() {
		return nest;
	}
	public void setNest(List<NestedObj> nest) {
		this.nest = nest;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
