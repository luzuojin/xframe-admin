package dev.xframe.admin.view.structs;

import java.util.Objects;

public class Navi implements Comparable<Navi> {

	protected String name;
	protected String path;
	protected int order;
	
	public Navi(String path, String name) {
	    this(path, name, 10);
	}
	public Navi(String path, String name, int order) {
		this.name = name;
		this.path = path;
		this.order = order;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Navi navi = (Navi) o;
		return Objects.equals(path, navi.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@Override
	public int compareTo(Navi o) {
	    return Integer.compare(o.order, this.order);
	}

	protected void fillTo(Chapter chapter) {
		chapter.getNavis().add(this);
	}

	protected Navi duplicateBy(Navi navi) {
		return new Navi(navi.path, navi.name, navi.order);
	}

	public void makeOrdered() {
		//if has children
	}

}
