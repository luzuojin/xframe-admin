package dev.xframe.admin.view;

public interface EColumn {
    int Text    = 0;    //文本
    int Area    = 1;    //大段文本
    int Number  = 2;    //数字
    int Pass    = 3;    //密码
    int Phone   = 4;    //手机号
    int Email   = 5;    //邮箱
    int Bool    = 20;   //check radio
    int Enum    = 21;   //下拉框 @see XRegistrator.registEnumValue
    int Mult    = 22;   //下拉框(多选)
    int Tree    = 23;   //下拉框(树状多选)
    int Datetime= 30;   //日期+时间
    int Date    = 31;   //日期
    int Time    = 32;   //仅时间
    int File    = 40;   //文件
    int Imag    = 41;   //图片
    int Model   = 80;   //对应object
    int List    = 81;   //object list

    static boolean isNested(int ecol) {
        return ecol == Model || ecol == List;
    }
}
