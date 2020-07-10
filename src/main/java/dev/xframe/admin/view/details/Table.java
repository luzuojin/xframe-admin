package dev.xframe.admin.view.details;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dev.xframe.admin.view.Column;
import dev.xframe.admin.view.Detail;
import dev.xframe.admin.view.Option;
import dev.xframe.admin.view.XOption;
import dev.xframe.admin.view.XSegment;
import dev.xframe.utils.XStrings;

public class Table implements Detail {
    
    private List<Column> columns = Collections.emptyList();
    private List<Option> options = Collections.emptyList();
    
    private boolean padding;
    private boolean listable;
    
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
    public boolean getPadding() {
        return padding;
    }
    public void setPadding(boolean padding) {
        this.padding = padding;
    }
    public boolean getListable() {
        return listable;
    }
    public void setListable(boolean listable) {
        this.listable = listable;
    }
    
    @Override
    public Detail parseFrom(XSegment xseg, Class<?> declaring) {
        List<Option> options = Detail.parseOptions(declaring, xseg.model());
        this.padding = xseg.padding();
        //subres的操作不支持query(原因:query有输入框)-->过滤掉qry&&!empty(suburl)
        this.options = options.stream().filter(op->op.getOpType()!=XOption.type_qry||XStrings.isEmpty(op.getPath())).collect(Collectors.toList());
        this.columns = Detail.parseModelColumns(xseg.model());
        this.listable = options.stream().filter(op->XOption.listing.equals(op.getPath())).findAny().isPresent();
        return this;
    }

}
