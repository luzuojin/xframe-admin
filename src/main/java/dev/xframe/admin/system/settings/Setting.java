package dev.xframe.admin.system.settings;

import dev.xframe.admin.view.XColumn;

public class Setting {

    @XColumn(primary = true)
    private String key;
    @XColumn(required = true)
    private String val;
    @XColumn
    private String comment;

    public Setting() {
    }
    public Setting(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
