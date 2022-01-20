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
    constructor(parent, name, path) {
        super(parent, name, path);
    }
    static of(parent, jSegment) {
        let seg = new Segment(parent, jSegment.name, jSegment.path);
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
    static _ = Detail.regist(1, this);
    constructor(parent) {super(parent);}
    //change cached data(s)
    onDataChanged(op, data) {
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
        this.showContent0();
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
        TableDetail.showTableBody($('#xtbody'), this.columns, this.data, trOptions);
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
        return Column.getVals(this.qryOp.children, col=>col.getQueryVal());
    }
    static showTableHead(_pdom, columns, hasOps=false) {
        let _tabletr = $(`<tr id='xtr_{0}'/>`.format(0));
        _pdom.empty();
        _pdom.append(_tabletr);
        for(let column of columns){
            if(xcolumn.list(column)) {
                _tabletr.append(`<td id='xtd_{0}_{1}' class='align-middle'>{2}</td>`.format(0, 0, column.hint));
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
/*-----------columns-----------*/
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
        return this.children && this.children.length > 0 ? this.children : this.parent.columns.map(col=>Column.of(this, col));
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
            flexOption.doGet(Column.getVals([col], _=>val), resp=>{
                if(resp.struct) {
                    this.parent.setStruct(resp.struct);
                    this.flexName = resp.struct.flexName;
                    delete this.columns;//有结构变化
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

class Column {
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

    static getVals(columns, valFunc) {
        let obj = {};
        columns.forEach(col=>{
            obj[col.key] = valFunc(col);
        });
        return obj;
    }

    equals(other) {
        return this.key == other.key;
    }

    getValFrom(data) {
        let val = data[this.key];
        if(this.type == xTypes._enum || this.type == xTypes._mult) {
            return xenumText(this.enumKey, val);
        }
        return xvalue(val);
    }
    getFormVal() {
        return dlgInputVal(this.parent.pid(), this);
    }

    onValChanged(val) {
        this.parent.onColValChanged(this, val);
    }

    //as query input
    getQueryDom() {return $('#xqry_{0}'.format(this.key));}
    getQueryVal() {return this.getQueryDom().val();}
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
    addToForm(_parent, op, val) {
        tryAddDlgInput(_parent, op, this, op.pid(), val);
        xchange(this.formDom(op), ()=>this.onValChanged(this.getFormVal()));
    }
    formDom(op) {
        return dlgInputDom(op.pid(), this.key);
    }
}

class NestColumn extends Column {
    static _ = Column.regist([xTypes._model, xTypes._list], this);

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
}


/*-----------------------------*/
/*-------------Form------------*/
/*-----------------------------*/
class OptionForm {
    option;
    data;
    flxop;
    constructor(option, data, flxop){
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
            col.addToForm(_pdom, this.option, this.data[col.key]);
        });
    }
    getFormData() {
        return Column.getVals(this.option.columns(), col=>col.getFormVal());
    }
    submit() {
        this.option.doPost(this.getFormData(), resp=>{
            $('#xdialog').modal('hide');
            this.data = resp;   //change data
            this.option.parent.onDataChanged(this.option, resp);
        });
    }
}
