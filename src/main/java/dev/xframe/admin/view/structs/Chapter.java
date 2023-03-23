package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.XChapter;

import java.util.function.Predicate;

public class Chapter extends Navigate {

	public Chapter(String path, String name, int order) {
		super(path, name, order);
	}
	public Chapter(XChapter xc) {
		super(xc.path(), xc.name(), xc.order());
	}

	public Chapter copyBy(String path, Predicate<String> predicate) {
		Chapter c = new Chapter(path, name, order);
		navis.stream().filter(navi->predicate.test(c.path + "/" + navi.path)).forEach(navi->navi.fillTo(c));
		return c;
	}

}
