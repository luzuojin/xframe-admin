package dev.xframe.admin.view;

import java.util.ArrayList;
import java.util.List;

import dev.xframe.utils.XStrings;

public class Option implements Comparable<Option> {
	
	public static final Option ini = new Option("加载", XOption.type_ini);	//HttpMethods.GET("ini")
	public static final Option qry = new Option("查询", XOption.type_qry);   //HttpMethods.GET
	public static final Option edt = new Option("修改", XOption.type_edt);   //HttpMethods.PUT
	public static final Option add = new Option("新增", XOption.type_add);   //HttpMethods.POST
	public static final Option del = new Option("删除", XOption.type_del);   //HttpMethods.DELETE
	public static final Option flx = new Option("结构", XOption.type_flx);	//HttpMethods.GET(marked @XOption.type_flx)
	
	private String name;
	private String path;
	private List<Column> inputs = new ArrayList<>();
	private int type; //1(增), 2(查)
	
	public Option(String name, int type) {
	    this(name, type, "");
	}
	public Option(String name, int type, String path) {
	    this.name = name;
	    this.type = type;
	    this.path = path;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Column> getInputs() {
		return inputs;
	}
	public void setInputs(List<Column> inputs) {
		this.inputs = inputs;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Option copy(XOption op, String path) {
		String xname = op == null ? null : op.value();
		return new Option(XStrings.orElse(xname, this.name), type, path);
	}
	public Option with(List<Column> columns) {
	    this.inputs = columns;
	    return this;
	}
	@Override
	public int compareTo(Option o) {
	    if(type == o.type) {
	        if(type == XOption.type_add) {//add在前端展示时为从右至左
	            return o.path.compareTo(this.path);
	        } else {
	            return this.path.compareTo(o.path);
	        }
	    }
	    return Integer.compare(type, o.type);
	}
	
}
