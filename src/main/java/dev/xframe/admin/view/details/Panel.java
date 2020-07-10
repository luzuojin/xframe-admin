package dev.xframe.admin.view.details;

import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;

public class Panel extends Classic {
	
	public Panel() {
		super(type_panel);
	}
	
	@Override
	public Detail parseFrom(XSegment xseg, Class<?> declaring) {
		super.parseFrom(xseg, declaring);
		this.checkQryAndAddOption();
		return this;
	}
	//Panel只支持ini/edit/delete操作
	private void checkQryAndAddOption() {
		if(options.stream().filter(opt->opt.getType()==XOption.type_qry||opt.getType()==XOption.type_add).findAny().isPresent()) {
			throw new IllegalArgumentException("Panel don`t support query or add option");
		}
	}

}
