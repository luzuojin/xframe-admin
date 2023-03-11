package dev.xframe.admin.view;

/**
 * 展示在哪里的相关配置
 */
public interface EShowcase {
    int List     = 1 << 0;//Table列表中展示
    int Edit     = 1 << 1;//是否可编辑(disable),显示由edel决定
    int Add      = 1 << 2;//新增(add)Dialog中展示
    int Edel     = 1 << 3;//编辑/删除(edt/del)中展示
    int ListEdel = (List | Edel);	//列表及改删(只有新增不显示)
    //下面为各种组合展示的配置
    int Full    = (1 << 4) - 1;		//所有
    int xorEdel = (Full ^ Edel);	//改删均不展示
    int xorAdd  = (Full ^ Add);	//新增不显示
    int xorEdit = (Full ^ Edit);	//只是不能编辑
    int xorList = (Full ^ List);	//只有列表不显示
}
