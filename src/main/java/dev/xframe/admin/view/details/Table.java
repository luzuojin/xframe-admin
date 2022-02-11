package dev.xframe.admin.view.details;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;

public class Table extends Classic {
    
    public Table() {
		super(XSegment.type_table);
	}

	private boolean padding;
    private boolean sortable;
    
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
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
    	super.parseFrom(xseg, declaring);
    	this.padding = xseg.padding();
        this.sortable = this.columns.stream().anyMatch(Column::isSortable);
    	this.checkQryOption();
        return this;
    }
    
    //只能存在一个qry/flx option
    protected void checkQryOption() {
		if(options.stream().filter(opt->opt.getType()==XOption.type_qry).count() > 1) {
			throw new IllegalArgumentException("Detail can only exist one qry option(@HttpMethods.GET");
		}
		if(options.stream().filter(opt->opt.getType()==XOption.type_flx).count() > 1) {
			throw new IllegalArgumentException("Detail can only exist one flx option(@HttpMethods.GET");
		}
	}

}
