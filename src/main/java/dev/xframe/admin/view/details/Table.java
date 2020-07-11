package dev.xframe.admin.view.details;

import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;

public class Table extends Classic {
    
    public Table() {
		super(type_table);
	}

	private boolean padding;
    
    public boolean getPadding() {
        return padding;
    }
    public void setPadding(boolean padding) {
        this.padding = padding;
    }
    
    @Override
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
    	super.parseFrom(xseg, declaring);
    	this.padding = xseg.padding();
    	this.checkQryOption();
        return this;
    }
    
    //只能存在一个qry option
    protected void checkQryOption() {
		if(options.stream().filter(opt->opt.getType()==XOption.type_qry).count() > 1) {
			throw new IllegalArgumentException("Detail can only exist one qry option(@HttpMethods.GET&empty(args)");
		}
	}

}
