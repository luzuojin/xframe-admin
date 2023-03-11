package dev.xframe.admin.view.structs;

public class Navi implements Comparable<Navi> {

	protected String name;
	protected String path;
	protected int order;
	
	public Navi(String name, String path) {
	    this(name, path, 10);
	}
	public Navi(String name, String path, int order) {
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
	public int compareTo(Navi o) {
	    return Integer.compare(o.order, this.order);
	}

	protected void fillTo(Chapter chapter) {
		chapter.getNavis().add(this);
	}

	protected Navi duplicateBy(Navi navi) {
		return new Navi(navi.name, navi.path, navi.order);
	}

}
