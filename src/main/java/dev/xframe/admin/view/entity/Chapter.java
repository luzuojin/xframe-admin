package dev.xframe.admin.view.entity;

import dev.xframe.admin.view.XChapter;

import java.util.function.Predicate;

public class Chapter extends Navigate {

	public Chapter(String name, String path, int order) {
		super(name, path, order);
	}
	public Chapter(XChapter xc) {
		super(xc.name(), xc.path(), xc.order());
	}

	public Chapter copyBy(String path, Predicate<String> predicate) {
		Chapter c = new Chapter(name, path, order);
		navis.stream().filter(navi->predicate.test(c.path + "/" + navi.path)).forEach(navi->navi.fillTo(c));
		return c;
	}

}
