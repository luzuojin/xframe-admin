package dev.xframe.admin.view.entity;

import dev.xframe.admin.view.EContent;
import dev.xframe.admin.view.EOption;
import dev.xframe.admin.view.XSegment;

import java.util.Collections;
import java.util.List;

/**
 * model based
 * columns-->model
 * @author luzj
 */
public abstract class Classic implements Content {

	protected int type;
	protected List<Column> columns = Collections.emptyList();
	protected List<Option> options = Collections.emptyList();
	
	protected Classic(EContent type) {
		this.type = type.val;
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
    public Content parseFrom(XSegment xseg, Class<?> declaring) {
        this.columns = Content.parseModelColumns(xseg.model());
        this.options = Content.parseOptions(declaring, xseg.model());
        this.checkIniOption();
        return this;
    }
    
	protected void checkIniOption() {
		if(options.stream().filter(opt->opt.getType()== EOption.Ini).count() > 1) {
			throw new IllegalArgumentException("Content can only exist on ini option(@HttpMethods.GET&empty(args)");
		}
	}
	
}
