package dev.xframe.admin.view.entity;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XSegment;

public class Panel extends Classic {
	
	private String desc;
	
	public Panel() {
		super(EContent.Panel);
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public Content parseFrom(XSegment xseg, Class<?> declaring) {
		super.parseFrom(xseg, declaring);
		this.desc = xseg.desc();
		this.checkOptions();
		return this;
	}
	//Panel只支持ini/edit/delete操作, qry操作输入框变化时自动处理
	private void checkOptions() {
		if(options.stream().anyMatch(opt->opt.getType()== EOption.Add)) {
			throw new IllegalArgumentException("Panel don`t support add option");
		}
		if(options.stream().filter(opt->opt.getType()== EOption.Qry).count() > 1) {
			throw new IllegalArgumentException("Panel can only exist one qry option(@HttpMethods.GET");
		}
		Option qryOp = options.stream().filter(opt->opt.getType()== EOption.Qry).findAny().orElse(null);
		if(qryOp != null) qryOp.setType(EOption.Var);//Panel不支持qry默认转为flx
		if(options.stream().filter(opt->opt.getType()== EOption.Var).count() > 1) {
			throw new IllegalArgumentException("Panel can only exist one flx option(@HttpMethods.GET");
		}
	}

}
