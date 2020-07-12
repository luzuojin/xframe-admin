package dev.xframe.admin.view.details;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;

public class Panel extends Classic {
	
	//返回有结构变化的结果
	public static class Flex {
		public List<Column> columns;
		public Object internal;
		private Flex(List<Column> columns, Object internal) {
			this.columns = columns;
			this.internal = internal;
		}
		static final Map<Class<?>, List<Column>> caches = new HashMap<>();
		public static Flex of(Object data) {
			return new Flex(caches.computeIfAbsent(data.getClass(), Detail::parseModelColumns), data);
		}
	}
	
	private String desc;
	
	public Panel() {
		super(type_panel);
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public Detail parseFrom(XSegment xseg, Class<?> declaring) {
		super.parseFrom(xseg, declaring);
		this.desc = xseg.desc();
		this.checkOptions();
		return this;
	}
	//Panel只支持ini/edit/delete操作, qry操作输入框变化时自动处理
	private void checkOptions() {
		if(options.stream().filter(opt->opt.getType()==XOption.type_add).findAny().isPresent()) {
			throw new IllegalArgumentException("Panel don`t support add option");
		}
		if(options.stream().filter(opt->opt.getType()==XOption.type_qry).count() > 1) {
			throw new IllegalArgumentException("Panel can only exist one qry option(@HttpMethods.GET&empty(args)");
		}
	}

}
