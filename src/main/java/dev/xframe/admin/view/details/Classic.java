package dev.xframe.admin.view.details;

import java.util.Collections;
import java.util.List;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;

/**
 * model based
 * columns-->model
 * @author luzj
 */
public abstract class Classic implements Detail {

	protected int type;
	protected List<Column> columns = Collections.emptyList();
	protected List<Option> options = Collections.emptyList();
	
	protected Classic(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Column> getColumns() {
        return columns;
    }
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    public List<Option> getOptions() {
        return options;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }
    
    @Override
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
        this.options = Detail.parseOptions(declaring, xseg.model());
        this.columns = Detail.parseModelColumns(xseg.model());
        this.checkIniOption();
        return this;
    }
    
	protected void checkIniOption() {
		if(options.stream().filter(opt->opt.getType()==XOption.type_ini).count() > 1) {
			throw new IllegalArgumentException("Detail can only exist on ini option(@HttpMethods.GET&empty(args)");
		}
	}
	
}
