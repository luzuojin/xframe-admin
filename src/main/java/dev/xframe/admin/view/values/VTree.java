package dev.xframe.admin.view.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * tree struct multi select
 */
public class VTree extends VEnum {

	private List<VTree> children = new ArrayList<>();

	public VTree(String id) {
		super(id);
	}
	public VTree(Number id, String text) {
		super(id, text);
	}
	public VTree(String id, String text) {
		super(id, text);
	}
	
	public List<VTree> getChildren() {
		return children;
	}
	public void setChildren(List<VTree> children) {
		this.children = children;
	}

	public VTree child(VTree child) {
		this.children.add(child);
		return this;
	}
	public VTree children(VTree... children) {
		return children(Arrays.asList(children));
	}
	public VTree children(List<VTree> children) {
		this.children = children;
		return this;
	}
}
