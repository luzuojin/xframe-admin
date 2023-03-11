package dev.xframe.admin.view.structs;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XSegment;

public class Table extends Classic {

    private boolean padding;
    private boolean sortable;

    public Table() {
		super(EContent.Table);
	}

    public boolean getPadding() {
        return padding;
    }
    public void setPadding(boolean padding) {
        this.padding = padding;
    }
    public boolean getSortable() {
        return sortable;
    }
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    @Override
    public Content parseFrom(XSegment xseg, Class<?> declaring) {
    	super.parseFrom(xseg, declaring);
    	this.padding = xseg.padding();
        this.sortable = this.columns.stream().anyMatch(Column::getSortable);
    	this.checkQryOption();
        return this;
    }
    
    //只能存在一个qry/flx option
    protected void checkQryOption() {
		if(options.stream().filter(opt->opt.getType()== EOption.Qry).count() > 1) {
			throw new IllegalArgumentException("Content can only exist one qry option(@HttpMethods.GET");
		}
		if(options.stream().filter(opt->opt.getType()== EOption.Var).count() > 1) {
			throw new IllegalArgumentException("Content can only exist one flx option(@HttpMethods.GET");
		}
	}

}
