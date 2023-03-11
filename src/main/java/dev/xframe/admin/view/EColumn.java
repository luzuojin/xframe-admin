package dev.xframe.admin.view;

public enum EColumn {
    Text    (0),    //文本
    Area    (1),    //大段文本
    Number  (2),    //数字
    Pass    (3),    //密码
    Phone   (4),    //手机号
    Email   (5),    //邮箱
    Bool    (20),   //check radio
    Enum    (21),   //下拉框 @see XRegistrator.registEnumValue
    Mult    (22),   //下拉框(多选)
    Tree    (23),   //下拉框(树状多选)
    Datetime(30),   //日期+时间
    Date    (31),   //日期
    Time    (32),   //仅时间
    File    (40),   //文件
    Imag    (41),   //图片
    Model   (80),   //对应object
    List    (81),   //object list
    ;
    public final int val;
    EColumn(int val) {
        this.val = val;
    }
    public boolean isNested() {
        return this == Model || this == List;
    }
}
