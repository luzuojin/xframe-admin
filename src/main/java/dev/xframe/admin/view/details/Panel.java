package dev.xframe.admin.view.details;

import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;

public class Panel extends Classic {
	
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
			throw new IllegalArgumentException("Panel can only exist one qry option(@HttpMethods.GET");
		}
		Option qryOp = options.stream().filter(opt->opt.getType()==XOption.type_qry).findAny().orElse(null);
		if(qryOp != null) qryOp.setType(XOption.type_flx);//Panel不支持qry默认转为flx
		if(options.stream().filter(opt->opt.getType()==XOption.type_flx).count() > 1) {
			throw new IllegalArgumentException("Panel can only exist one flx option(@HttpMethods.GET");
		}
	}

}
