package dev.xframe.admin.view.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;

//结构变化的结果
public class Flex {
	public List<Column> columns;
	private Flex(List<Column> columns) {
		this.columns = columns;
	}
	private static final Map<Class<?>, List<Column>> caches = new HashMap<>();
	public static Flex of(Class<?> model) {
		return new Flex(caches.computeIfAbsent(model, Detail::parseModelColumns));
	}
}