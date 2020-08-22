package dev.xframe.admin.view.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;

//结构变化的结果
public class Flex {
	public static final String HEADER_KEY = "flex-name";
	//Http.header("flex-name")
	public final String flexName;
	public final List<Column> columns;
	private Flex(List<Column> columns, String flexName) {
		this.columns = columns;
		this.flexName = flexName;
	}
	private static final Map<Class<?>, List<Column>> caches = new HashMap<>();
	public static Flex of(Class<?> model) {
		return new Flex(caches.computeIfAbsent(model, Detail::parseModelColumns), model.getName());
	}
}