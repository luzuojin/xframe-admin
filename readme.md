#### xframe-admin
---
- 通过Java代码映射对应的前端展示
- 前端基于AdminLTE 3, 后端基于xframe-(kernal,jdbc),h2databse.
- 以下为各概念映射关系

---
- 侧边栏
  - 一级目录 `@XChapter`(Chapter)
    - Path (识别)
    - Name (显示)
  - 二级目录 `@XSegment`(Segment) --> 唯一对应一个`@Rest` Service
    - Path (从`@Rest`中读取,`@Rest`的URL以`@Chapter`中配置的Path开头)
    - Name (显示)
- 详情页(与二级目录--对应, `@Reset` Service)
  - Detail
    - Panel  (Columns单页展示,处理单个对象)
    - Table  (Columns表格展示,处理对象列表)
    - Markd  (Markdown file, 返回文件内容)
  - Column(s) (对应`@XSegment.model/query.args`中的字段, `@XColumn`配置参数)
    - type   (`@XColumn.type_*`)
    - key    (默认为字段名/参数名)
    - hint   (展示文字/提示文字)
    - enumKey(下拉列表时获取下拉菜单的关键字)
    - show   (展示配置 `@XColumn.!type_*`)
  - Option(s) (对应`@Rest`中的各`@HttpMethods`方法, `@XOption`配置展示文字)
    - type   (见Options说明)
    - path   (可选suburl)
    - name   (展示文字)
    - inputs (`@HttpArgs`获取,查询操作必须有,其他操作默认使用`Content.Columns`)
- Options
  - ini(加载) --> 对应`@HttpMethods.GET`且参数为空, 唯一(只能有一个该种方法)
  - qry(查询) --> 对应`@HttpMethods.GET`且参数不为空, 唯一(只能有一个该种方法)
  - add(新增) --> 对应`@HttpMethods.POST` 参数为`@HttpArgs.Body`(JSON解析), 不唯一
  - edt(修改) --> 对应`@HttpMethods.PUT`  参数为`@HttpArgs.Body`(JSON解析), 不唯一
  - del(删除) --> 对应`@HttpMethods.DELETE` 参数为`@HttpArgs.Body`(JSON解析), 不唯一

- Others (可参考用户系统相关的代码)
  - Columns(s) --> 对应类型`@XColumn.type_*`, 展示配置`@XColumn.!type_*`(常量)
  - Option(s)  --> 需要多个操作时, 同类方法可以增加(suburl)的方式配置多个
  - Panel      --> qry方法对应的Column(s)值在展示页中变化时会自动发送Get请求(无按钮支持)
  - Type_ENUM  --> 下拉选择框需要通过`XRegistrator.registEnumValue`提前配置选项列表

- 启动参数
  - -Dstore.dir --> h2databse目录
  - -Dlogs.dir  --> pid/log目录


