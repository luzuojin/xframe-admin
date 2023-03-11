package dev.xframe.admin.view;

public interface EOption {
    /**数据初始化*/
    int Ini = -1;
    /**查询*/
    int Qry = 1;
    /**新增*/
    int Add = 2;
    /**修改*/
    int Edt = 3;
    /**删除*/
    int Del = 4;
    /**变更结构体(Variant.Struct)*/
    int Var = 5;//变更结构的查询操作
    /**download*/
    int Dlh = 6;
    /**table row download*/
    int Dlr = 7;
}
