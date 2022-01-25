class Node {
    parent;
    children = [];
    constructor(parent) {
        this.parent = parent;
    }
    append(node) {
        this.children.push(node);
    }
    //显示
    show() {}
    dom() {}
    appendHtm(htm) {}
}

class Navi extends Node {
    name;
    path;
    constructor(parent, name, path) {
        super(parent);
        this.name = name;
        this.path = path;
    }
    mergeFrom(navi) {
        this.name = navi.name;
        this.path = navi.path;
        return this;
    }
    uri() {
        return this.parent ? this.parent.uri().urljoin(this.path) : this.path;
    }
    pid() {//path ident
        return (this.parent && this.parent.pid) ? (this.parent.pid() + "_" + this.path) : this.path;
    }
}

class Chapter extends Navi {
    constructor(name, path) {
        super(null, name, path);
    }
    static of(jChapter) {
        let c = new Chapter(jChapter.name, jChapter.path);
        if(jChapter.navis) {
            //仅有一个segment且path={x}, 不展示tab, 合并navis~segments
            if(jChapter.segments.length == 1 && jChapter.segments[0].path.startsWith("{")) {
                let jSegment = jChapter.segments[0];  //wildcard segment
                for(let jNavi of jChapter.navis) {
                    c.append(Segment.of(c, jSegment).mergeFrom(jNavi));
                }
            } else {//展示tab
                for(let jNavi of jChapter.navis) {
                    let tab = Tab.of(c, jNavi);
                    for(let jSegment of jChapter.segments) {
                        tab.append(Segment.of(tab, jSegment));
                    }
                    c.append(tab);
                }
            }
        } else {
            for(let jSegment of jChapter.segments){//二级菜单
                c.append(Segment.of(c, jSegment));
            }
        }
        return c
    }
    dom() {
        return $('#vchapter_{0}'.format(this.pid()));
    }
    show() {
        let htm = `<li class="nav-item has-treeview">
                    <a class="nav-link" href="javascript:void(0);">
                      <i class="nav-icon fab fa-gg"/>
                      <p>
                        {0}
                        <i class="right fas fa-angle-left"/>
                      </p>
                    </a>
                    <ul id="vchapter_{1}" class="nav nav-treeview"/>
                </li>
                `.format(this.name, this.pid());
        $('#xsiderbar').append(htm);
        this.children.forEach(child=>child.show());
    }
}

var LatestSeg;
class Segment extends Navi {
    canSort;
    constructor(parent, name, path) {
        super(parent, name, path);
    }
    static of(parent, jSegment) {
        let seg = new Segment(parent, jSegment.name, jSegment.path);
        seg.canSort = jSegment.canSort;
        seg.append(Detail.of(seg, jSegment.detail));
        return seg;
    }
    dom() {
        return $('#vseg_{0}'.format(this.pid()));
    }
    show() {
        let htm =`
                <li class="nav-item">
                    <a id="vseg_{0}" class="nav-link" href="javascript:void(0);">
                      <i class="fas fa-paperclip"/>
                      <p>{1}</p>
                    </a>
                </li>
                `.format(this.pid(), this.name);
        this.parent.dom().append(htm);
        xclick(this.dom(), ()=> {
            this.active(this);
            this.showContent()
        });
    }
    initContainer() {
        $('#xcontent').empty();
        $('#xcontent').append(`<div class="card-header">
                                <div class="row">
                                    <div id="xboxhead" class="clearfix w-100"></div>
                                </div>
                               </div>
                               <div id="xboxbody" class="card-body">
                               </div>`);
    }
    showContent() {
        this.initContainer();
        this.children[0].show();    //detail.show
    }
    deactive(seg) {
        if(seg) seg.dom().removeClass('active');
    }
    active(seg) {
        this.deactive(LatestSeg);
        LatestSeg = seg;
        seg.dom().addClass('active');
    }
}


class Tab extends Segment {
    latestSeg;
    constructor(parent, name, path) {
        super(parent, name, path);
    }
    static of(parent, jNavi) {
        return new Tab(parent, jNavi.name, jNavi.path);
    }
    tabDom(seg) {
        return $("#vsegtab_{0}".format(seg.pid()));
    }
    initContainer() {
        super.initContainer();
        //tab container
        $('#xcontent').prepend(`<div class="card-header p-0 border-bottom-0"><ul id="xtabContainer" class="nav nav-tabs" role="tablist"></ul></div>`);

        for(let seg of this.children) {
            $('#xtabContainer').append(`<li class="nav-item"><a id="vsegtab_{0}" class="nav-link text-dark" data-toggle="pill" href="javascript:void(0);" role="tab" aria-selected="false">{1}</a></li>`.format(seg.pid(), seg.name));
            //onClick show content
            xclick(this.tabDom(seg), ()=>this.showTabContent(seg));
        }
    }
    showContent() {
        this.initContainer();
        this.showTabContent(this.latestTabSeg ? this.latestTabSeg : this.children[0]);
    }
    tabDeactive(seg) {
        if(!seg) return;
        this.tabDom(seg).removeClass('active');
        this.tabDom(seg).attr('aria-selected', false);
    }
    tabActive(seg) {
        this.tabDeactive(this.latestTabSeg);
        this.latestTabSeg = seg;
        this.tabDom(seg).addClass('active');
        this.tabDom(seg).attr('aria-selected', true);
    }
    showTabContent(seg) {
        this.tabActive(seg)
        seg.children[0].show();//detail.show
    }
}

class Detail extends Node {
    static Impls = new Map();
    static regist(type, cls) {
        Detail.Impls.set(type, cls);
        return cls;
    }
    static getCls(type) {
        let cls = Detail.Impls.get(type);
        return cls ? cls : Detail;
    }
    type;
    padding;
    options;
    data;
    constructor(parent) {
        super(parent);
    }
    static of(parent, jDetail) {
        let d = new (Detail.getCls(jDetail.type))(parent);
        d.type = jDetail.type;
        d.desc = jDetail.desc;
        d.padding = jDetail.padding;
        d.flexName = jDetail.flexName;
        d.children = d.columns = jDetail.columns.map(jColumn=>Column.of(d, jColumn));
        d.options = jDetail.options.map(jOption=>Option.of(d, jOption));
        d.flexOption = d.getOption(opTypes.flx);
        return d;
    }
    setData(data) {
        if(data && data.struct) {//flex
            this.data = data.data;
            this.setStruct(data.struct);
        } else {
            this.data = data;
        }
        return this;
    }
    setStruct(struct) {
        let cols = struct.columns.map(jCol=>Column.of(this, jCol));
        Object.assign(this.columns, cols, {length:cols.length});
        this.flexName = struct.flexName;
    }
    uri() {
        return this.parent.uri();
    }
    pid() {//path ident
        return this.parent.pid();
    }
    getOption(opType) { //only one
        return this.options.find(e=>e.type==opType);
    }
    getOptions(opType) {
        return this.options.filter(e=>e.type==opType);
    }
    show() {
        let ini = this.getOption(opTypes.ini);
        if(ini) {
            ini.doGet({}, _data => this.setData(_data).showContent());
        } else {
            this.setData().showContent();
        }
    }
    showContent() {}
    onDataChanged(op, data) {this.data = data;}
    onColValChanged(col, val) {}
}

/*-----------------------------*/
/*-----------details-----------*/
/*-----------------------------*/
class TableDetail extends Detail {
    canSort;
    sortedData;
    static _ = Detail.regist(1, this);
    constructor(parent) {
        super(parent);
        this.canSort = parent.canSort;
    }
    //change cached data(s)
    onDataChanged(op, data) {
        console.log(this.columns);
        if(op.type == opTypes.qry || Array.isArray(data)) {
            this.setData(data);
        } else if(data){
            let pks = this.columns.filter(col=>col.primary).map(col=>col.key);
            let idx = pks.length==0?-1:this.data.findIndex(dat=>!pks.some(pk=>data[pk]!=dat[pk]));//! not equals
            if(idx == -1) {
                if(op.type == opTypes.add) this.data.push(data);
            } else {
                if(op.type == opTypes.add) this.data[idx] = data;
                if(op.type == opTypes.edt) this.data[idx] = data;
                if(op.type == opTypes.del) this.data.splice(idx, 1);
            }
        }
        this.sort();
        this.showContent1();
        //this.showContent0();
    }
    setData(data) {
        return super.setData(xOrElse(data, []));
    }
    showContent() {
        $('#xboxhead').empty();
        $('#xboxbody').empty();
        $('#xboxbody').append(`<table id="xtable" class="table table-bordered table-hover"><thead id="xthead"></thead><tbody id="xtbody"></tbody></table>`);

        this.showQueryBox();
        this.showContent0();
    }
    showContent0() {
        let trOptions = this.options.filter(e=>e.type==opTypes.del||e.type==opTypes.edt);
        TableDetail.showTableHead($('#xthead'), this.columns, trOptions.length>0);
        if(!this.sortedData){
            this.sortedData = [];
            Object.assign(this.sortedData, this.data);
        }
        TableDetail.showTableBody($('#xtbody'), this.columns, this.sortedData, trOptions);
    }
    showContent1(){
        let trOptions = this.options.filter(e=>e.type==opTypes.del||e.type==opTypes.edt);
        TableDetail.showTableBody($('#xtbody'), this.columns, this.sortedData, trOptions);
    }
    showQueryBox() {
        let _tr = 0;
        //box head
        for(let op of this.getOptions(opTypes.qry)) {
            let _id = op.pid();
            for(let column of op.children) {
                column.addToQueryBox($('#xboxhead'));
            }
            $('#xboxhead').append(`<button id="qrybtn_{0}_{1}" type="button" class="btn bg-gradient-info float-left" style="margin-left:7.5px;margin-right:7.5px;">{2}</button>`.format(_id, _tr, op.name));
            xclick($("#qrybtn_{0}_{1}".format(_id, _tr)), ()=>op.doGet(this.getQueryParams(), resp=>this.setData(resp).showContent0()));
            this.qryOp = op;
        }

        for(let op of this.getOptions(opTypes.add)) {
            let _id = op.pid();
            $('#xboxhead').append(`<button id="addbtn_{0}_{1}" type="button" class="btn bg-gradient-success float-right" style="margin-left:7.5px;margin-right:7.5px;">{2}</button>`.format(_id, _tr, op.name));
            xclick($("#addbtn_{0}_{1}".format(op.pid(), _tr)), ()=>op.onClick(this.padding?this.getQueryParams():{}));
        }
    }
    getQueryParams() {
        return Column.getQueryVals(this.qryOp.children);
    }
    static showTableHead(_pdom, columns, hasOps=false) {
        let _tabletr = $(`<tr id='xtr_{0}'/>`.format(0));
        _pdom.empty();
        _pdom.append(_tabletr);
        for(let column of columns){
            if(xcolumn.list(column)) {
                _tabletr.append(`<td id='xtd_{0}_{1}' class='align-middle'>{2}</td>`.format(0, column.pid(), column.hint));
                if(column.parent instanceof TableDetail && column.canSort && column.parent.canSort){
                    column.openSort();
                }
            }
        }
        if(hasOps)//options td head
            _tabletr.append(`<td id='xtd_{0}_{1}' class='align-middle text-right'>{2}</td>`.format(0, 0, "Options"));
    }
    static showTableBody(_pdom, columns, data, options) {
        _pdom.empty();
        let _tr = 0;
        for(let model of data) {
            model._id = (++ _tr);
            let _tabletr = $(`<tr id='xtr_{0}'/>`.format(_tr));
            _pdom.append(_tabletr)
            var _td = 0;
            for(let column of columns){
                if(xcolumn.list(column)) {
                    let _tabletd = $(`<td id='xtd_{0}_{1}' class='align-middle'></td>`.format(_tr, (++_td)));
                    _tabletr.append(_tabletd);
                    column.addToTable(_tabletd, column.getValFrom(model));
                }
            }
            //options td
            if(options && options.length>0) {
                let _tabletd = $(`<td id='xtd_{0}_{1}' class='align-middle text-right'></td>`.format(_tr, (++_td)));
                _tabletr.append(_tabletd);
                for(let op of options.filter(e=>e.type==opTypes.edt)) {
                    _tabletd.append(`<button id="edtbtn_{0}_{1}" type="button" class="btn btn-sm btn-outline-info" style="margin-right:5px">{2}</button>`.format(op.pid(), _tr, op.name));
                    xclick($("#edtbtn_{0}_{1}".format(op.pid(), _tr)), ()=>op.onClick(model));
                }
                for(let op of options.filter(e=>e.type==opTypes.del)) {
                    _tabletd.append(`<button id="delbtn_{0}_{1}" type="button" class="btn btn-sm btn-outline-danger">{2}</button>`.format(op.pid(), _tr, op.name));
                    xclick($("#delbtn_{0}_{1}".format(op.pid(), _tr)), ()=>op.onClick(model));
                }
            }
        }
    }
    sortChange(sortColumn){
        for (let column of this.columns) {
            if(column !== sortColumn && column.sortType > 0){
                column.cancelSort();
            }
        }
    }
    sort(){
        Object.assign(this.sortedData, this.data);
        for (let colum of this.columns) {
            if(colum.sortType > 0){
                colum.sort();
            }
        }
    }
}

class PanelDetail extends Detail {
    static _ = Detail.regist(2, this);
    constructor(parent) {super(parent);}

    showContent() {
        let data = this.data;
        if(!data) data = {};
        //empty ex
        $('#xboxhead').empty();
        $('#xboxbody').empty();
        //desc
        $('#xboxhead').append(`<div class="col-sm-8 m-auto h-100 h5">{0}</div>`.format(this.desc));
        //body form
        $('#xboxbody').append(`<form id="xpanel_form" class="form-horizontal"/>`);
        //form option
        let formOption = Option.of(this, {})
        formOption._form = new OptionForm(formOption, data)
        formOption._form.showContent($('#xpanel_form'));
        //add button row
        $('#xboxbody').append(`<div id="xpanel_btnrow" class="form-group row"></div>`);
        for(let op of this.options) {
            if(op.type == opTypes.del) {
                $('#xpanel_btnrow').append(`<div class="col-sm-2 m-auto"><button id="xpanel_delbtn_{0}" type="button" class="btn btn-block bg-danger">{1}</button></div>`.format(op.path, op.name));
                xclick($('#xpanel_delbtn_{0}'.format(op.path)), ()=>this.submit(op, formOption._form.getFormData()));
            } else if(op.type == opTypes.edt) {
                $('#xpanel_btnrow').append(`<div class="col-sm-2 m-auto"><button id="xpanel_edtbtn_{0}" type="button" class="btn btn-block bg-info">{1}</button></div>`.format(op.path, op.name));
                xclick($('#xpanel_edtbtn_{0}'.format(op.path)), ()=>this.submit(op, formOption._form.getFormData()));
            }
        }
    }
    submit(op, data) {
        op.doPost(data, resp=>this.setData(resp).showContent(), {'flex-name': this.flexName});
    }
    onColValChanged(col, val) {}
}

class MarkdDetail extends Detail {
    static _ = Detail.regist(3, this);
    constructor(parent) {super(parent);}

    showContent() {
        let renderer = {
            code(code, infostr, enscaped) {
                let lang = hljs.getLanguage(infostr) ? infostr : 'plaintext';
                let text = hljs.highlight(lang, code).value;
                return `<pre class="pre" style="white-space:pre-wrap;word-break:normal;background-color:#f6f8fa;border-radius:4px;">${text}</pre>`;
            },
            table(header, body) {
                return `<div class="table-responsive">
                        <table class="table table-striped table-bordered table-responsive">
                            <thead class="thead-light">${header}</thead>
                            <tbody>${body}</tbody>
                        </table>
                        </div>`;
            },
            tablerow(content) {
                return `<tr>${content}</tr>`;
            },
            tablecell(content, flags) {
                if(flags.header) {
                    return `<th>${content}</th>`
                }
                return `<td>${content}</td>`;
            }
        };
        marked.use({renderer});
        let text = marked(this.data);
        $('#xcontent').html('<div class="card-body">{0}</div>'.format(text));
    }
}

/*-----------------------------*/
/*------------option-----------*/
/*-----------------------------*/
class Option extends Node {
    static of(parent, jOption) {
        let c = new Option(parent);
        Object.assign(c, jOption);
        c.parent = parent;
        if(jOption.inputs)
            c.children = jOption.inputs.map(jCol=>Column.of(c, jCol));//复制成Option独有
        return c;
    }
    hasChild(col) {
        return this.children && this.children.some(e=>e.key==col.key);
    }
    columns() {
        if(this.children && this.children.length>0)
            return this.children;
        if(this._columns)
            return this._columns
        return (this._columns = this.parent.columns.map(col=>Column.of(this, col)));
    }
    uri() {
        return this.path ? this.parent.uri().urljoin(this.path) : this.parent.uri();
    }
    pid() {
        return this.path ? (this.parent.pid() + "_" + this.path) : this.parent.pid();
    }
    onColValChanged(col, val) {
        //do flex??
        let flexOption = this.parent.flexOption;
        if(flexOption && flexOption.hasChild(col) && val) {
            flexOption.doGet(Column.packVals([col], _=>val), resp=>{
                if(resp.struct) {
                    this.parent.setStruct(resp.struct);
                    this.flexName = resp.struct.flexName;
                    delete this._columns;//有结构变化
                }
                //合并数据&刷新form
                Object.assign(this._form.data, this._form.getFormData(), xOrElse(resp.data, {}));
                this._form.showContent();
            });
        }
    }
    doPost(data, func, _headers) {
        doPost(this.uri(), this, data, func, xOrElse(_headers, {'flex-name': this.flexName}));
    }
    doGet(data, func) {
        doGet('{0}?{1}'.format(this.uri(), $.param(data)), func);
    }
    onClick(data) {
        (this._form = new OptionForm(this, data)).show();
    }
}

/*-----------------------------*/
/*-----------columns-----------*/
/*-----------------------------*/
class Column {
    sortType = 0;//1升序，2降序
    static Impls = new Map();
    static regist(types, cls) {
        for(let type of types)
            Column.Impls.set(type, cls);
        return cls;
    }
    static getCls(type) {
        let cls = Column.Impls.get(type);
        return cls ? cls : Column;
    }

    parent;
    static of(parent, jColumn) {
        let c = new (Column.getCls(jColumn.type))();
        Object.assign(c, jColumn);
        c.parent = parent;
        if(jColumn.columns)
            c.columns = jColumn.columns.map(jCol=>Column.of(c, jCol));
        return c;
    }


    static packVals(columns, valFunc) {
        let obj = {};
        let validResult = true;
        columns.forEach(col=>{
            let val = valFunc(col);
            if(val === undefined){
                validResult = false;
                return
            }
            if(val) obj[col.key] = val;
        });
        return validResult?obj:undefined;
    }

    equals(other) {
        return this.key == other.key;
    }
    pid() {
        return this.parent.pid() + "_" + this.key;
    }

    getValFrom(data) {
        let val = data[this.key];
        if(this.type == xTypes._enum || this.type == xTypes._mult) {
            return xenumText(this.enumKey, val);
        }
        return xvalue(val);
    }

    onValChanged(val) {
        this.parent.onColValChanged(this, val);
    }

    //as query input
    static getQueryVals(columns) {
        return Column.packVals(columns, col=>col.getQueryVal());
    }
    getQueryVal() {return this.getQueryDom().val();}
    getQueryDom() {return $('#xqry_{0}'.format(this.key));}
    addToQueryBox(_parent) {
        if(this.type == xTypes._enum) {
            _parent.append(`<div class="col-sm-2 float-left"><select id="xqry_{0}" class="form-control select2bs4" data-placeholder="{1}" style="width:100%"><option/></select></div>`.format(this.key, this.hint));
            xselect2(this.getQueryDom(), this, true);//查询框/enum记忆
        } else {
            _parent.append(`<div class="col-sm-2 float-left"><input id="xqry_{0}" class="form-control" type="text" placeholder="{1}" autocomplete="off"></div>`.format(this.key, this.hint));
            if(this.type==xTypes._datetime) xdatepicker(this.getQueryDom());
            if(this.type==xTypes._date) xdatepicker(this.getQueryDom(), xformatDate);
            if(this.type==xTypes._date) xdatepicker(this.getQueryDom(), xformatTime);
        }
    }

    addToTable(_parent, val) {
        return _parent.append(val);
    }

    /*--------------------------*/
    /*-----for show in form-----*/
    /*--------------------------*/
    static getFormVals(columns) {
        return Column.packVals(columns, col=>col.getFormVal());
    }
    getFormVal(needValid=true) {  //dlgDataFunc
        let val = this.getFormValDom().val();
        if(needValid && this.required){
            if(!this.validateInput(val)){
                return undefined;
            }
        }
        return val;
    }
    getFormValDom() {
        return $("#dinput_{0}".format(this.pid()));
    }
    addToForm(_parent, val) {
        this.addToForm0(_parent, this.parent, val)
    }
    addToForm0(_parent, op, val) {
        if(op.type == opTypes.add && !xcolumn.add(this)) return;
        if(op.type >= opTypes.edt && !xcolumn.edel(this)) return;
        this.doAddToForm(_parent, val);
        //disabled
        if (op.type == opTypes.del || (op.type == opTypes.edt && !xcolumn.edit(this))) {
            this.getFormValDom().attr("disabled", true);
            return
        }
        xchange(this.getFormValDom(), ()=>this.onValChanged(this.getFormVal(false)));
        xinput(this.getFormValDom(), ()=>this.getFormVal());
    }
    doAddToForm(_parent, val) {
        let formValHtm = this.makeFormValHtm();
        let labelHtm = this.hint;
        if(this.required){
            formValHtm += this.inputInvalidHtm().format(this.invalidText());
            labelHtm = this.inputRequiredHtm() + labelHtm;
        }
        let _dom = $(this.getColBoxHtm().format(labelHtm, formValHtm));
        _parent.append(_dom);
        this.setValToFormDom(this.getFormValDom(), val);
    }
    getColBoxHtm() {
        return `<div class="form-group row">
                    <label class="col-sm-2 col-form-label"><p class="float-right">{0}</p></label>
                    <div class="col-sm-10">{1}</div>
                </div>`;
    }
    makeFormValHtm() {    //dlgHtmFunc
        return `<input id="dinput_{0}" class="form-control" placeholder="{1}" type="{2}">`.format(this.pid(), this.hint, 'text');
    }
    setValToFormDom(dom, val) {//dlgMakeFunc
        if(val) dom.val(val).trigger('change')
    }
    inputRequiredHtm(){
        return `<span style="font-size: 14px;color: #ed4014;margin-right: 4px">*</span>`;
    }
    inputInvalidHtm(){
        return `<div class="invalid-feedback">{0}</div>`;
    }
    invalidText(){
        return "{0}不能为空".format(this.hint);
    }
    validateInput(val){
        if(!val){
            this.invalid();
            return false;
        }
        this.valid();
        return true;
    }
    valid(){
        this.getFormValDom().removeClass("is-invalid");
    }
    invalid(){
        this.getFormValDom().addClass("is-invalid");
    }

    sortIconHtm(){
        return `<span id="sort-icon-{0}" style="display: flex;flex-direction: column;float: right;vertical-align: middle;">
                    <svg  xmlns="http://www.w3.org/2000/svg"  width="12" height="12" fill="currentColor" class="bi bi-caret-up-fill asc-icon" viewBox="0 0 16 16">
                        <path d="m7.247 4.86-4.796 5.481c-.566.647-.106 1.659.753 1.659h9.592a1 1 0 0 0 .753-1.659l-4.796-5.48a1 1 0 0 0-1.506 0z"/>
                    </svg>
                    <svg  xmlns="http://www.w3.org/2000/svg"  width="12" height="12" fill="currentColor" class="bi bi-caret-down-fill desc-icon" viewBox="0 0 16 16">
                        <path d="M7.247 11.14 2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 0 1 .753 1.659l-4.796 5.48a1 1 0 0 1-1.506 0z"/>
                    </svg>
                </span>`.format(this.pid())
    }
    sortIconDom() {
        return $("#sort-icon-{0}".format(this.pid()));
    }
    tableHeadDom(){
        return $("#xtd_{0}_{1}".format(0, this.pid()))
    }
    openSort(){
        let headDom = this.tableHeadDom();
        headDom.append(this.sortIconHtm());
        headDom.addClass("sort-table-head");
        xclick(headDom, ()=> this.sortChange());
    }
    sortChange(){
        this.parent.sortChange(this);
        this.sortType++;
        if(this.sortType > 2){
            this.sortType = 0;
        }
        this.sort();
    }
    sort(){
        let dom = this.sortIconDom();
        switch (this.sortType){
            case 1:
                dom.removeClass("sort-desc");
                dom.addClass("sort-asc");
                this.parent.sortedData.sort(this.ascSortFunc(this));
                break;
            case 2:
                dom.removeClass("sort-asc");
                dom.addClass("sort-desc");
                this.parent.sortedData.sort(this.descSortFunc(this));
                break;
            default:
                this.cancelSort();
                break;
        }
        this.parent.showContent1();
    }
    ascSortFunc(column){
        return function (a,b){
            if(!column.columns){
                return (a[column.key] > b[column.key])? 1:-1;
            }
        }
    }
    descSortFunc(column){
        return function (a,b){
            if(!column.columns){
                return (b[column.key] > a[column.key])? 1:-1;
            }
        }
    }
    cancelSort(){
        this.sortType = 0;
        this.sortIconDom().removeClass("sort-desc");
        this.sortIconDom().removeClass("sort-asc");
        Object.assign(this.parent.sortedData, this.parent.data);
    }
}

class AreaColumn extends Column {
    static _ = Column.regist([xTypes._area], this);
    makeFormValHtm() {    //dlgHtmFunc
        return `<textarea id="dinput_{0}" class="form-control" placeholder="{1}" rows="8"/>`.format(this.pid(), this.hint);
    }
}
class PasswordColumn extends Column {
    static _ = Column.regist([xTypes._pass], this);
    makeFormValHtm() {    //dlgHtmFunc
        return `<input id="dinput_{0}" class="form-control" placeholder="{1}" type="{2}">`.format(this.pid(), this.hint, 'password');
    }
    getFormVal() {  //dlgDataFunc
        return $.md5(super.getFormVal());
    }
}
class DateTimeColumn extends Column {
    static _ = Column.regist([xTypes._datetime], this);
    setValToFormDom(dom, val) {//dlgMakeFunc
        super.setValToFormDom(dom, val);
        xdatepicker(dom)
    }
}
class DateColumn extends Column {
    static _ = Column.regist([xTypes._date], this);
    setValToFormDom(dom, val) {//dlgMakeFunc
        super.setValToFormDom(dom, val);
        xdatepicker(dom, xformatDate)
    }
}
class TimeColumn extends Column {
    static _ = Column.regist([xTypes._time], this);
    setValToFormDom(dom, val) {//dlgMakeFunc
        super.setValToFormDom(dom, val);
        xdatepicker(dom, xformatTime)
    }
}
class EnumColumn extends Column {
    static _ = Column.regist([xTypes._enum, xTypes._mult], this);
    makeFormValHtm() {
        return `<select id="dinput_{0}" class="form-control select2" data-placeholder="{1}" style="width:100%"></select>`.format(this.pid(), this.hint);
    }
    setValToFormDom(dom, val) {
        xselect2(dom, this);
        if(val && val != 0) dom.val(val).trigger('change')
    }
}
class BoolColumn extends Column {
    static _ = Column.regist([xTypes._time], this);
    makeFormValHtm() {
        return `<div class="form-control custom-control custom-switch custom-switch-on-primary">
                    <input id="dinput_{0}" type="checkbox" class="custom-control-input" value="false">
                    <label class="custom-control-label" xfor="dinput_{0}" style="margin-left:7.5px;"/>
                </div>`.format(this.pid());
    }
    setValToFormDom(dom, val) {
        dom.change(function(){dom.val(this.checked);});//this:changed event
        if(val) dom.attr('checked', val).trigger('change');
    }
}
class FileColumn extends Column {
    static _ = Column.regist([xTypes._file], this);
    makeFormValHtm() {
        return `
                <div class="custom-file">
                    <input type="file" class="custom-file-input" id="dinput_{0}">
                    <label class="custom-file-label" id="dinput_{0}_label" xfor="dinput_{0}"></label>
                </div>
                <div id="dinput_{0}_preview"/>`.format(this.pid());
    }
    setValToFormDom(dom, val) {
        let _fv = _v => {
            $('#dinput_{0}_label'.format(this.pid())).html(_v);
            this.filePreview(_v);
        }
        if(val) _fv(val);

        xchange(dom, function(evt){
            let fi = evt.target.files[0];
            if(!fi) return;//cancel
            let fd = new FormData();
            fd.append('file', fi);
            $.ajax({
                url: '{0}/{1}'.format(xurl, xpaths.upload),
                type: 'post',
                headers: {"X-Token": xtoken()},
                data: fd,
                dataType: 'json',
                contentType: false,
                processData: false,
                success: function(resp){_fv(resp.data);}
            });
        });
    }
    getFormVal() {
        return $('#dinput_{0}_label'.format(this.pid())).html();
    }
    filePreview(val) {}
}
class ImagColumn extends Column {
    static _ = Column.regist([xTypes._imag], this);
    filePreview(val) {
        $("#dinput_{0}_preview".format(this.pid())).html(`<img class="col-sm-2 img-thumbnail" src="{0}">`.format('{0}/{1}?name={2}&X-Token={3}'.format(xurl, xpaths.preview, val, xtoken())));
    }
}


class NestColumn extends Column {
    static _ = Column.regist([xTypes._model], this);
    onColValChanged(col, val) {
        this.parent.onColValChanged(col, val);
    }
    addToTable(_parent, val) {
        let _ntable = $(`<table class="table table-bordered table-hover table-sm text-sm mb-0"></table>`);
        let _nthead = this.collapse
            ? $(`<thead data-toggle="collapse" data-target="#collaspse_{0}" aria-controls="xst_{0}" aria-expanded="true"></thead>`.format(_parent.attr("id")))
            : $(`<thead></thead>`);
        let _ntbody = this.collapse
            ? $(`<tbody id="collaspse_{0}_" class="collapse"></tbody>`.format(_parent.attr("id")))
            : $(`<tbody></tbody>`);

        _parent.append(_ntable);
        _ntable.append(_nthead);
        _ntable.append(_ntbody);

        TableDetail.showTableHead(_nthead, this.columns);
        TableDetail.showTableBody(_ntbody, this.columns, this.type==xTypes._model?[val]:val)
    }
    makeFormValHtm() {
        return `<div id="dinput_{0}" class="border-left border-bottom text-sm"></div>`.format(this.pid());
    }
    setValToFormDom(dom, val) {
        dom.empty();
        this.columns.forEach(col => {
            col.addToForm0(dom, this.parent, xvalueByKey(val, col.key));
        });
    }
    getFormVal(needValid=true) {
        return Column.getFormVals(this.columns);
    }
}
class IndexedNestColumn extends Column {
    static of(_origin, _index, _compact) {
        let c = Object.assign(Object.create(_origin), _origin);
        c._origin = _origin;
        c._index  = _index;
        c._compact = _compact;
        c.pid = function() {
            return this._origin.pid() + "_" + this._index;
        };
        c.getColBoxHtm = function() {
            if(this._compact == 2)
                return `<label class="col-sm-2 col-form-label"><p class="float-right">{0}</p></label><div class="col-sm-4">{1}</div>`;
            if(this._compact == 3)
                return `<label class="col-sm-1 col-form-label" ><p class="float-right">{0}</p></label><div class="col-sm-3">{1}</div>`;
            return `<label class="col-sm-2 col-form-label"><p class="float-right">{0}</p></label><div class="col-sm-10">{1}</div>`;
        };
        return c;
    }
}
class ListColumn extends NestColumn {
    static _ = Column.regist([xTypes._list], this);
    _cIndex;
    makeFormValHtm() {
        return `<button id="dinput_{0}" type="button" style="border: dashed 1px #dee2e6;" class="form-group form-control">+</button>`.format(this.pid());
    }
    getCompact() {
        return (this.compact && this.columns.length <= 3) ? this.columns.length : -1;
    }
    setValToFormDom(_dom, _val) {
        let compact = this.getCompact();
        //make empty list element
        let makeElement = (index, aplFunc, _v) => {
            let _outer = $(`<div id="dnest_{0}_{1}" class="border-left border-bottom position-relative form-group text-sm">`.format(this.pid(), index));
            aplFunc(_outer);

            if(compact != -1) {
                let _inner = $(`<div class="form-group row"></div>`);
                _outer.append(_inner);
                this.columns.forEach(col=>{    
                    IndexedNestColumn.of(col, index, compact).addToForm0(_inner, this.parent, xvalueByKey(_v, col.key));
                });    
            } else {
                this.columns.forEach(col=>{
                    let _inner = $(`<div class="form-group row"></div>`);
                    _outer.append(_inner);
                    IndexedNestColumn.of(col, index, compact).addToForm0(_inner, this.parent, xvalueByKey(_v, col.key));
                });
            }
            //minus btn
            let minusBtn = $(`<button type="button" class="position-absolute close" style="right:.5rem;bottom:.25rem;"><i class="fas fa-minus-circle fa-xs"></i></button>`);
            xclick(minusBtn, ()=>minusBtn.parent().remove());
            _outer.append(minusBtn);
            return _outer;
        };
        this._cIndex = 0;
        xclick(_dom, ()=>makeElement((++this._cIndex), _x=>_dom.before(_x)));
        if(_val) {
            let _last;
            for(let _e of _val) {
                let aplFunc = _last ? _x=>_last.after(_x) : _x=>_dom.before(_x);
                if(!_e._id) _e._id = ++this._cIndex
                this._cIndex = Math.max(_e._id, this._cIndex);
                _last = makeElement(_e._id, aplFunc, _e);
            }
        }
    }
    getFormVal(needValid=true) {
        let compact = this.getCompact();
        let _data = [];
        for (let index = 1; index <= this._cIndex; ++index) {
            let _nest = $("#dnest_{0}_{1}".format(this.pid(), index));
            if(_nest.length && _nest.length>0) {
                let obj = Column.getFormVals(this.columns.map(col=>IndexedNestColumn.of(col, index, compact)));
                if(obj === undefined){
                    return undefined;
                }
                if(Object.keys(obj).length > 0) _data.push(obj);
            }
        }
        return _data;
    }
}

class EmailColumn extends Column{
    static _ = Column.regist([xTypes._text_email], this);
    invalidText() {
        return  "邮箱地址不正确";
    }
    validateInput(val){
        let reg = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,5}$/;
        if(!reg.test(val)){
            this.invalid()
            return false;
        }
        this.valid();
        return true;
    }
}

class PhoneColumn extends Column{
    static _ = Column.regist([xTypes._text_phone], this);
    invalidText() {
        return  "请输入正确的手机号";
    }
    validateInput(val){
        let reg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
        if(!reg.test(val)){
            this.invalid();
            return false;
        }
        this.valid();
        return true;
    }
}

/*-----------------------------*/
/*-------------Form------------*/
/*-----------------------------*/
class OptionForm {
    option;
    data;
    constructor(option, data){
        this.option = option;
        this.data = data;
    }
    title() {
        return '{0}&nbsp;/&nbsp;{1}'.format(this.option.parent.parent.name, this.option.name);//option->detail->segment
    }
    show() {
        $('#xdialog_title').empty();
        $('#xdialog_title').append(this.title())
        this.showContent($('#xdialog_form'));
        xclick($('#xdialog_submit'), ()=>this.submit())
        $('#xdialog').modal('show');
    }
    showContent(_parent) {
        let _pdom = this._pdom = xOrElse(_parent, this._pdom);
        _pdom.empty();
        this.option.columns().forEach(col=>{
            col.addToForm(_pdom, xvalueByKey(this.data, col.key));
        });
    }
    getFormData() {
        return Column.getFormVals(this.option.columns());
    }
    submit() {
        this.option.doPost(this.getFormData(), resp=>{
            $('#xdialog').modal('hide');
            this.data = resp;   //change data
            this.option.parent.onDataChanged(this.option, resp);
        });
    }
}
