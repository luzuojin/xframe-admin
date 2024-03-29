//option types
var opTypes = {
    ini: -1,
    qry: 1,
    add: 2,
    edt: 3,
    del: 4,
    vrt: 5,
    dlh: 6,
    dlr: 7
}
//content types
var cttTypes = {
    _table: 1,
    _panel: 2,
    _markd: 3,
    _chart: 4,
    _cells: 5,
}
//column types
var colTypes = {
    //text...
    _text: 0,
    _area: 1,
    _numb: 2,
    _pass: 3,
    _phone: 4,
    _email: 5,
    //select...
    _bool: 20,
    _enum: 21,
    _mult: 22,//multi enum select
    _tree: 23,//multi tree select
    //time...
    _datetime: 30,
    _date: 31,
    _time: 32,
    //file...
    _file: 40,
    _imag: 41,
    //nested...
    _model:80,
    _list: 81,
}
//column.show
var xShowcase = {
    list: function(c){return (c.show & 1) > 0;},
    edit: function(c){return (c.show & 2) > 0 && !c.primary;},
    add : function(c){return (c.show & 4) > 0;},
    edel: function(c){return (c.show & 8) > 0;},
}

class Node {
    parent;
    children = [];
    constructor(parent) {
        this.parent = parent;
    }
    append(node) {
        this.children.push(node);
        return node;
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
        return this.parent ? `${this.parent.uri()}/${this.path}` : this.path;
    }
    pid() {//path ident
        return (this.parent && this.parent.pid) ? `${this.parent.pid()}_${this.path}` : this.path;
    }
}

class Chapter extends Navi {
    constructor(name, path) {
        super(null, name, path);
    }
    static of(jChapter) {
        let c = new Chapter(jChapter.name, jChapter.path);
        for(let jNavi of jChapter.navis) {
            if(jNavi.content) {  //segment
                c.append(Segment.of(c, jNavi));
            } else {             //tab (二级菜单)
                let tab = c.append(Tab.of(c, jNavi));
                jNavi.navis.forEach(_jNavi=>tab.append(Segment.of(tab, _jNavi)));
            }
        }
        return c
    }
    dom() {
        return $(`#vchapter_${this.pid()}`);
    }
    show() {
        let htm = `<li class="nav-item has-treeview">
                    <a id="vchapter_${this.pid()}_link" class="nav-link" href="javascript:void(0);">
                      <i class="nav-icon fas fa-layer-group"/>
                      <p>
                        ${this.name}
                        <i class="right fas fa-angle-left"/>
                      </p>
                    </a>
                    <ul id="vchapter_${this.pid()}" class="nav nav-treeview"/>
                </li>`;
        $('#xsiderbar').append(htm);
        this.children.forEach(child=>child.show());
    }
    deactive() {
        $(`#vchapter_${this.pid()}_link`).removeClass('active');
    }
    active() {
        $(`#vchapter_${this.pid()}_link`).addClass('active');
    }
}

var LatestSeg;
class Segment extends Navi {
    constructor(parent, name, path) {
        super(parent, name, path);
    }
    static of(parent, jSegment) {
        let seg = new Segment(parent, jSegment.name, jSegment.path);
        seg.append(Content.of(seg, jSegment.content));
        return seg;
    }
    dom() {
        return $(`#vseg_${this.pid()}`);
    }
    show() {
        let htm =`
                <li class="nav-item">
                    <a id="vseg_${this.pid()}" class="nav-link" href="javascript:void(0);">
                      <i class="far fa-circle"/>
                      <p>${this.name}</p>
                    </a>
                </li>`;
        this.parent.dom().append(htm);
        xclick(this.dom(), ()=> {
            this.active();
            this.showContent()
        });
    }
    showContent() {
        this.children[0].show($('#xcontainer').empty());    //content.show
    }
    deactive() {
        this.dom().removeClass('active');
        this.parent.deactive();
    }
    active() {
        LatestSeg && LatestSeg.deactive();
        this.dom().addClass('active');
        this.parent.active();
        LatestSeg = this;
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
        return $(`#vsegtab_${seg.pid()}`);
    }
    showTabNavis(_pdom) {
        _pdom.append(`
            <div id="xtabs" class="card card-primary card-outline card-outline-tabs bg-light">
                <div class="card-header p-0 border-bottom-0">
                    <ul id="xtabNavis" class="nav nav-tabs" role="tablist"></ul>
                </div>
            </div>`);
        for(let seg of this.children) {
            $('#xtabNavis').append(`<li class="nav-item"><a id="vsegtab_${seg.pid()}" class="nav-link text-dark" data-toggle="pill" href="javascript:void(0);" role="tab" aria-selected="false">${seg.name}</a></li>`);
            //onClick show content
            xclick(this.tabDom(seg), ()=>this.showTabContent(seg));
        }
    }
    showContent() {
        this.showTabContent(this.latestTabSeg || this.children[0]);
    }
    showTabContent(seg) {
        let _pdom = $('#xcontainer').empty();
        this.showTabNavis(_pdom);
        this.tabActive(seg)
        seg.children[0].show(_pdom);//content.show
    }
    tabDeactive(seg) {
        if(!seg) return;
        this.tabDom(seg).removeClass('active');
        this.tabDom(seg).attr('aria-selected', false);
    }
    tabActive(seg) {
        this.tabDeactive(this.latestTabSeg);
        this.tabDom(seg).addClass('active');
        this.tabDom(seg).attr('aria-selected', true);
        this.latestTabSeg = seg;
    }
}

class Content extends Node {
    static Impls = new Map();
    static regist(type, cls) {
        Content.Impls.set(type, cls);
        return cls;
    }
    static getCls(type) {
        return Content.Impls.get(type) || Content;
    }
    type;
    options;
    data;
    constructor(parent) {
        super(parent);
    }
    static of(parent, jContent) {
        let d = new (Content.getCls(jContent.type))(parent);
        Object.assign(d, jContent)
        d.children = d.columns = jContent.columns.map(jColumn=>Column.of(d, jColumn));
        d.options = jContent.options.map(jOption=>Option.of(d, jOption));
        d.variantOption = d.getOption(opTypes.vrt);
        d.onInit();
        return d;
    }
    onInit() {}
    setData(data) {
        if(data && data.struct) {//variant
            this.data = data.data;
            this.setStruct(data.struct);
        } else {
            this.data = data;
        }
        return this;
    }
    getData(_defaults) {
        return this.data || _defaults;
    }
    setStruct(struct) {
        let cols = struct.columns.map(jCol=>Column.of(this, jCol));
        Object.assign(this.columns, cols, {length:cols.length});
        this.variantName = struct.variantName;
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
    getOptionsFunc() {
        return this.getOptions.bind(this);
    }
    iniDom(_pdom) {
        this.xc = new XContainer(_pdom).append(
        `<div id="xcontent" class="card card-outline w-100">
            <div class="card-header">
                <div class="row">
                    <div id="xboxhead" class="clearfix w-100"></div>
                </div>
           </div>
           <div id="xboxbody" class="card-body">
           </div>
        </div>`);
        return this;
    }
    show(_pdom) {
        this.ini(_data => {
            this.iniDom(_pdom);
            this.setData(_data).showContent()
        });
    }
    ini(_show) {
       let ini = this.getOption(opTypes.ini);
       (ini ? ini.doGet({}, _show) : _show());
    }
    showContent() {}
    onDataChanged(op, data) {this.data = data;}
    onColValChanged(col, val) {}
}

/*-----------------------------*/
/*-----------contents-----------*/
/*-----------------------------*/
class TableContent extends Content {
    static _ = Content.regist(cttTypes._table, this);
    sorter = new Sorter([], _data => {
        super.setData(_data);
        this.showTableBody0();
    });
    constructor(parent) {
        super(parent);
    }
    //change cached data(s)
    onDataChanged(op, data) {
        if(op.type == opTypes.qry || Array.isArray(data) || (data && data.data && Array.isArray(data.data))) {
            this.setData(data);
        } else if(data){
            let _datas = this.sorter.origin;
            let pks = this.columns.filter(col=>col.primary).map(col=>col.key);
            let idx = pks.length==0?-1:_datas.findIndex(dat=>!pks.some(pk=>data[pk]!=dat[pk]));//! not equals
            if(idx == -1) {
                if(op.type == opTypes.add) _datas.push(data);
            } else {
                if(op.type == opTypes.add) _datas[idx] = data;
                if(op.type == opTypes.edt) _datas[idx] = data;
                if(op.type == opTypes.del) _datas.splice(idx, 1);
            }
        }
        this.sorter.apply();
    }
    setData(data) {
        super.setData(xOrElse(data, []));
        this.sorter.set(this.data);
        return this;
    }

    showContent() {
        $('#xboxhead').empty();
        $('#xboxbody').empty();
        $('#xboxbody').append(`<div class="table-responsive">
                                <table id="xtable" class="table table-bordered table-hover">
                                    <thead id="xthead" bgcolor="#f8f9fa"></thead>
                                    <tbody id="xtbody"></tbody>
                                </table>
                                </div>`);
        this.showContentHead();
        this.showContentBody();
    }
    showContentHead() {//content.head(query box & add btn)
        TableContent.showHeadBar($('#xboxhead'), this.getOptionsFunc(), data=>this.setData(data).showContentBody(), this.padding);
    }
    static showHeadBar(_pdom, opsFunc, onDataChanged, padding) {
        _pdom.empty();
        let anyOption = false;
        let qryOption;
        let qryParams = () => qryOption ? qryOption._form.getFormData0() : {};
        let _tr = 0;
        for(let op of opsFunc(opTypes.qry)) {//query box
            (op._form = new OptionForm(op)).showContent(_pdom);
            let qrybtn = $(`<button type="button" class="btn btn-info float-left ml-2">${op.name}</button>`).appendTo(_pdom);
            xclick(qrybtn, ()=>op.doGet(qryParams(), onDataChanged));
            //最后一个查询框时 监听enter
            if(op.children.length > 0) {
                op.children[op.children.length-1].getFormValDom().keypress(e => {
                    if(e.keyCode==13) qrybtn.trigger('click');
                });
            }
            qryOption = op;
            anyOption = true;
        }
        for(let op of opsFunc(opTypes.dlh)) {
            let dlbtn = $(`<button type="button" class="btn btn-secondary float-right mr-2">${op.name}</button>`).appendTo(_pdom);
            xclick(dlbtn, ()=>op.doDownload(qryParams()));
            anyOption = true;
        }
        for(let op of opsFunc(opTypes.add)) {   //add... btn
            let addbtn = $(`<button type="button" class="btn btn-success float-right mr-2">${op.name}</button>`).appendTo(_pdom);
            xclick(addbtn, ()=>op.popup(padding?qryParams():{}));
            anyOption = true;
        }
        return anyOption;
    }

    showContentBody() {//content.table(head&body)
        this.showTableHead0();
        this.showTableBody0();
    }
    isTableOption(op) {return op.type==opTypes.del||op.type==opTypes.edt||op.type==opTypes.dlr;}
    getTableOptions() {return this.options.filter(this.isTableOption);}
    hasTableOptions() {return this.options.some(this.isTableOption);}
    showTableHead0() {TableContent.showTableHead($('#xthead'), this.columns, this.hasTableOptions());}
    showTableBody0() {TableContent.showTableBody($('#xtbody'), this.columns, this.data, this.getTableOptions());}

    static showTableHead(_pdom, columns, hasOps=false) {
        let _tabletr = $(`<tr id='xtr_0'/>`).appendTo(_pdom.empty());
        for(let col of columns){
            if(xShowcase.list(col)) {
                let _tableth = $(`<th class='align-middle'>${col.name}</th>`).appendTo(_tabletr);
                (col.parent.sortable && col.sortable && col.parent.sorter.show(_tableth, col.key));
            }
        }
        if(hasOps)//options td head
            _tabletr.append(`<th class='align-middle text-right'>Options</th>`);
    }
    static showTableBody(_pdom, columns, data, options) {
        _pdom.empty();
        for(let model of data) {
            let _tabletr = $(`<tr />`).appendTo(_pdom);
            for(let column of columns){
                if(xShowcase.list(column)) {
                    let _tabletd = $(`<td class='align-middle'></td>`).appendTo(_tabletr);
                    column.addToTable(_tabletd, column.getTextFrom(model));
                }
            }
            //options td
            if(options && options.length > 0) {
                let _tabletd = $(`<td class='align-middle text-right'></td>`).appendTo(_tabletr);
                for(let op of options.filter(e=>e.type==opTypes.edt)) {
                    let edtbtn = $(`<button type="button" class="btn btn-sm btn-outline-info mr-1">${op.name}</button>`).appendTo(_tabletd);
                    xclick(edtbtn, ()=>op.popup(model));
                }
                for(let op of options.filter(e=>e.type==opTypes.dlr)) {
                    let dlbtn = $(`<button type="button" class="btn btn-sm btn-outline-secondary mr-1">${op.name}</button>`).appendTo(_tabletd);
                    xclick(dlbtn, ()=>op.doDownload(model));
                }
                for(let op of options.filter(e=>e.type==opTypes.del)) {
                    let delbtn =$(`<button type="button" class="btn btn-sm btn-outline-danger mr-1">${op.name}</button>`).appendTo(_tabletd);
                    xclick(delbtn, ()=>op.popup(model));
                }
            }
        }
    }
}

class PanelContent extends Content {
    static _ = Content.regist(cttTypes._panel, this);
    constructor(parent) {super(parent);}
    showContent() {
        let data = this.getData({});
        //empty ex
        $('#xboxhead').empty();
        $('#xboxbody').empty();
        //desc
        $('#xboxhead').append(`<div class="col-sm-8 m-auto h-100 h5">${this.desc}</div>`);
        //body form
        $('#xboxbody').append(`<form id="xpanel_form" onsubmit="return false" class="form-horizontal"/>`);
        //form option
        let formOption = Option.of(this, {})
        formOption._form = new OptionForm(formOption, data)
        formOption._form.showContent($('#xpanel_form'));
        //add button row
        let btnrow = $(`<div class="form-group row"></div>`).appendTo($('#xboxbody'));
        for(let op of this.options) {
            if(op.type == opTypes.del) {
                let delbtn = $(`<div class="col-sm-2 m-auto"><button type="button" class="btn btn-block bg-danger">${op.name}</button></div>`).appendTo(btnrow);
                xclick(delbtn, ()=>this.submit(op, formOption._form.getFormData()));
            } else if(op.type == opTypes.edt) {
                let edtbtn = $(`<div class="col-sm-2 m-auto"><button type="button" class="btn btn-block bg-info">${op.name}</button></div>`).appendTo(btnrow);
                xclick(edtbtn, ()=>this.submit(op, formOption._form.getFormData()));
            } else if(op.type == opTypes.dlh || op.type == opTypes.dlr) {
                let dlbtn = $(`<div class="col-sm-2 m-auto"><button type="button" class="btn btn-block bg-secondary">${op.name}</button></div>`).appendTo(btnrow);
                xclick(dlbtn, ()=>op.doDownload(formOption._form.getFormData()));
            }
        }
    }
    submit(op, data) {
        op.doPost(data, resp=>this.setData(resp).showContent(), {'variant-name': this.variantName});
    }
}

class MarkdContent extends Content {
    static _ = Content.regist(cttTypes._markd, this);
    constructor(parent) {super(parent);}
    showContent() {
        let renderer = {
            code(code, infostr, enscaped) {
                let lang = hljs.getLanguage(infostr) ? infostr : 'plaintext';
                let text = hljs.highlight(lang, code).value;
                return `<pre class="p-0"><code class="hljs language-${lang}">${text}</code></pre>`;
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
                return flags.header ? `<th>${content}</th>` : `<td>${content}</td>`;
            }
        };
        marked.use({renderer});
        let text = marked(this.data);
        this.xc.child('xcontent').html(`<div class="card-body">${text}</div>`);
    }
}

//0,1,2,3
let ChartTypesArray = ['table', 'line', 'bar', 'pie', 'number'];
let ChartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};
let ChartColorsArray = [
    ChartColors.blue,
    ChartColors.purple,
    ChartColors.grey,
    ChartColors.red,
    ChartColors.orange,
    ChartColors.yellow,
    ChartColors.green
]

/*多维数据集合展示(m*n table|chart)*/
class ChartContent extends Content {
    static _ = Content.regist(cttTypes._chart, this);
    constructor(parent) {super(parent);}
    setData(data, type, title) {
        super.setData(data);
        if(Number.isInteger(type)) {
            this.chartType = type;
            this.chartTitle= title;
        }
        return this;
    }
    iniDom(_pdom) {
        this.xc = new XContainer(_pdom).append(
        `<div class="card card-outline w-100">
            <div id="xcharthead" class="card-header">
                <div class="row">
                    <div id="xchartheadbar" class="clearfix w-100"></div>
                </div>
            </div>
            <div id="xchartitle" class="card-title text-primary"></div>
            <div id="xchartbody" class="card-body d-flex align-items-top justify-content-center"></div>
        </div>`);
        return this;
    }
    showContent() {
        this.showContentHead();
        this.showContentBody();
    }
    showContentHead() {
        if(!TableContent.showHeadBar(this.xc.child('xchartheadbar'), this.getOptionsFunc(), _data=>this.setData(_data).showContentBody())) {
            this.xc.child('xcharthead').remove();
        }
        if(!!this.chartTitle) {
            this.xc.child('xchartitle').html($(`<span>${this.chartTitle}</span>`));
        } else {
            this.xc.child('xchartitle').remove();
        }
    }
    showContentBody() {
        let  _pdom = this.xc.child('xchartbody').empty();
        let config = this.makeConfig({
            type:this.chartType,
            data:this.getData([])
        });
        if(config.type == ChartTypesArray[0]) {
            this.showTableBody(_pdom, config);
        } else if(config.type == ChartTypesArray[4]) {
            this.showTextBody(_pdom, config);
        } else {
            this.showChartBody(_pdom, config);
        }
    }
    showChartBody(_pdom, config) {
        let canvas = $(`<canvas class="w-100" style="max-height:360px;"><canvas/>`).appendTo(_pdom);
        new Chart(canvas, config);
    }
    showTextBody(_pdom, config) {
        let colored = e => (e && e.startsWith('#')) ? `var(--${e.substring(1)}, ${e})` : `var(--gray-dark)`;
        let dataset = config.data.datasets[0];
        _pdom.append($(`
            <div class="text-center">
                <span style="color:${colored(dataset.label)}; font-size:4rem; font-weight:700;">${dataset.data[0]}</span>
                <br/>
                <span>${config.data.labels[0]}</span>
            </div>
        `));
    }
    showTableBody(_pdom, config) {
        let tabox = $(`<div class="table-responsive" style="max-height:540px;"></div>`);
        let table = $(`<table class="table table-bordered table-hover table-sm table-pin mb-0 text-nowrap"></table>`);
        let thead = $(`<thead></thead>`);
        let tbody = $(`<tbody></tbody>`);

        let showBody = _rows => {
            tbody.empty();
            _rows.forEach(_row => {
                let tdr = $(`<tr></tr>`).appendTo(tbody);
                _row.forEach(_col=>tdr.append(`<td>${_col}</td>`))
            });
        };
        let hdata = [config.data.setLabel].concat(config.data.labels);
        let bdata = config.data.datasets.map(dataset=>[dataset.label].concat(dataset.data));
        let sorter= hdata.length > 0 ? new Sorter(bdata, showBody) : {show:()=>{}};
        //head
        let thr = $(`<tr></tr>`).appendTo(thead);
        hdata.forEach((_col, _i) => sorter.show($(`<th>${_col}</th>`).appendTo(thr), _i));
        //body
        showBody(bdata);
        _pdom.append(tabox.append(table.append(thead).append(tbody)));
    }
    makeConfig(_vchart) {
        let type = ChartTypesArray[_vchart.type];
        let index = -1;
        _vchart.data.datasets.forEach(dataset => {
            let idColor = ChartColorsArray[++index];
            dataset.data = dataset.datas;
            dataset.borderColor     = type == 'pie' ? 'rgb(255, 255, 255, 0.3)' : idColor;
            dataset.backgroundColor = type == 'pie' ? ChartColorsArray : idColor;
        })
        return {
            type: type,
            data: {
                setLabel: _vchart.data.metadata.label,
                labels:   _vchart.data.metadata.datas,
                datasets: _vchart.data.datasets
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom',
                    }
                }
            }
        }
    }
}

//只用于ChartContent组合
class CellsContent extends Content {
    static _ = Content.regist(cttTypes._cells, this);
    constructor(parent) {super(parent);}
    iniDom(_pdom) {
        this.xc = new XContainer(_pdom).append(
            `<div id="xcontenthead" class="card">
                <div id="xquerybox" class="card-header"></div>
            </div>
            <div id="xcontentbody" class="w-100">
            </div>`);
    }
    onInit() {
        this.rowsCells = new Grouped('row', this.cells);
        this.pathCells = new Grouped('path', this.cells);
        this.rowsCells.forEach((_, cs) => {
            let rowColW = 12;
            let rowColN = 0;
            cs.forEach(c => {
                rowColW -= c.col;
                rowColN += c.col > 0 ? 0 : 1
            });
            if(rowColN > 0) {
                let cw = rowColW / rowColN || 2;
                cs.forEach(c => c.col = c.col || cw);
            }
        });
        this.options.filter(op=>op.type==opTypes.qry).forEach(op => {
            op.doGet = (_params, onDataChanged) => onDataChanged(this.qryData(_params));
        });
    }
    ini(_show) {
        _show(this.qryData({}));
    }
    qryData(_params) {
        this.qryParams = _params;
        let _data = {};
        for(let _path of this.pathCells.keys) {
            let _op = this.options.filter(e=>e.path==_path)[0];
            _op.syncGet(_params, resp=>_data[_path]=resp);
        }
        return _data;
    }
    getCellData(cell) {
        let e = this.data[cell.path]
        let d = this.pathCells.val(cell.path).length > 1 ? e[cell.pIndex] : e;
        return d;
    }
    getCellDownload(cell) {
        let rs = this.options.filter(e=>e.type==opTypes.dlr&&e.path.startsWith(cell.path + '/'));
        return rs && rs[0];
    }
    showContent() {
        this.showContentHead();
        this.showContentBody();
    }
    showContentHead() {
        if(!TableContent.showHeadBar($('#xquerybox'), this.getOptionsFunc(), _data=>this.setData(_data).showContentBody())) {
           $('#xcontenthead').remove();
        }
    }
    showContentBody() {
        $('#xcontentbody').empty();
        this.rowsCells.forEach((_, rowCells) => {
            let row = $(`<div class="row"></div>`).appendTo($('#xcontentbody'));
            for(let cc of rowCells) {
                let col = $(`<div class="col-md-${cc.col} d-flex"></div>`).appendTo(row);
                Content.of(this, {
                    type: cc.type == -1 ? cttTypes._markd : cttTypes._chart,
                    options: [],
                    columns: []
                }).iniDom(col).setData(this.getCellData(cc), cc.type, cc.title).showContent();
                //download button
                let dlOption = this.getCellDownload(cc);
                if(dlOption) {
                    let dlbtn = $(`<button type="button" class="btn-dl" title="" data-bs-original-title="Dowload">${dlOption.name}</button>`).appendTo(col.children(":first"));
                    xclick(dlbtn, ()=>dlOption.doDownload(this.qryParams, true));
                }
            }
        });
    }
}

/*-----------------------------*/
/*------------sorting-----------*/
/*-----------------------------*/
class Sorter {
    constructor(data, onDataChanged) { //set origin data
        this.origin = data;
        this.update = onDataChanged;
    }
    set(data) {this.origin = data;}
    show(_pdom, dkey) {  //dkey => sort by data[dkey]
        let icon = $(`<span class="d-flex flex-column justify-content-center">
                        <svg  xmlns="http://www.w3.org/2000/svg"  width="12" height="12" fill="currentColor" class="bi bi-caret-up-fill asc-icon" viewBox="0 0 16 16">
                            <path d="m7.247 4.86-4.796 5.481c-.566.647-.106 1.659.753 1.659h9.592a1 1 0 0 0 .753-1.659l-4.796-5.48a1 1 0 0 0-1.506 0z"/>
                        </svg>
                        <svg  xmlns="http://www.w3.org/2000/svg"  width="12" height="12" fill="currentColor" class="bi bi-caret-down-fill desc-icon" viewBox="0 0 16 16">
                            <path d="M7.247 11.14 2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 0 1 .753 1.659l-4.796 5.48a1 1 0 0 1-1.506 0z"/>
                        </svg>
                    </span>`).appendTo(_pdom);
        _pdom.addClass('th-sortable');
        _pdom.wrapInner(()=>`<div class="d-flex justify-content-between"></div>`);//no line break

        xclick(_pdom, ()=>{
            this.cancel();
            this.active(icon, dkey);
        });
    }
    cancel() {//cancel before
        (this.activeIco && this.activeIco.removeClass(this.style()));
    }
    style() {   //activeIdx[-1, 0, 1]
        return `sort-${['desc', 'nil', 'asc'][this.activeIdx + 1]}`;
    }
    activeIco;
    activeKey;
    activeIdx = 0;
    active(icon, dkey) {
        this.activeIco = icon;
        this.activeIdx = (dkey != this.activeKey) ? 1 : (this.activeIdx == 1 ? -1 : this.activeIdx + 1);
        this.activeKey = dkey;
        (this.activeIdx != 0 && this.activeIco.addClass(this.style()));
        this.apply();
    }
    apply() {
        this.update(Object.assign([], this.origin).sort((a, b) => (a[this.activeKey] > b[this.activeKey] ? 1 : -1) * this.activeIdx));
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
        delete c.columns
        if(jOption.columns) {
            c.children = jOption.columns.map(jCol => (opTypes.qry==jOption.type) ? QueryColumn.of(Column.of(c, jCol)) : Column.of(c, jCol));//复制成Option独有
        }
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
        return this.path ? `${this.parent.uri()}/${this.path}` : this.parent.uri();
    }
    pid() {
        return this.path ? `${this.parent.pid()}_${this.type||0}_${this.path}` : `${this.parent.pid()}_${this.type||0}`;
    }
    onColValChanged(col, val) {
        //do variant??
        let variantOption = this.parent.variantOption;
        if(variantOption && variantOption.hasChild(col) && val) {
            variantOption.doGet(Column.packVals([col], _=>val), resp=>{
                if(resp.struct) {
                    this.parent.setStruct(resp.struct);
                    this.variantName = resp.struct.variantName;
                    delete this._columns;//有结构变化
                }
                //合并数据&刷新form
                Object.assign(this._form.data, this._form.getFormData0(), xOrElse(resp.data, {}));
                this._form.showContent();
            });
        }
    }
    doPost(data, func, _headers) {
        doPost(this.uri(), this, data, func, xOrElse(_headers, {'variant-name': this.variantName}));
    }
    doPost0(data, func, _headers) { //不解析resp, resp格式为({status, data})
        doPost0(this.uri(), this, data, func, xOrElse(_headers, {'variant-name': this.variantName}));
    }
    doGet(data, func) {
        doGet(`${this.uri()}?${$.param(data)}`, func);
    }
    syncGet(data, func) {
        syncGet(`${this.uri()}?${$.param(data)}`, func);
    }
    doDownload(data, fullData=false) {
        let _params = fullData ? data : Column.packVals(xOrElse(this.children, []), col=>data[col.key]);
        doDownload(`${xHref(this.uri(), _params)}`);
    }
    popup(data) {
        (this._form = new OptionForm(this, data)).show();
    }
    onDataChanged(data) {
        this.parent.onDataChanged(this, data);
    }
}

/*-----------------------------*/
/*-----------columns-----------*/
/*-----------------------------*/
class Column {
    static Impls = new Map();
    static regist(types, cls) {
        for(let type of types)
            Column.Impls.set(type, cls);
        return cls;
    }
    static getCls(type) {
        return Column.Impls.get(type) || Column;
    }

    parent;
    static of(parent, jColumn) {
        let c = new (Column.getCls(jColumn.type))();
        Object.assign(c, jColumn);
        c.required = (c.required || c.primary);
        c.parent = parent;
        if(jColumn.columns)
            c.columns = jColumn.columns.map(jCol=>Column.of(c, jCol));
        return c;
    }

    static packVals(columns, valFunc) {
        let obj = {};
        columns.forEach(col=>{
            let val = valFunc(col);
            if(val) obj[col.key] = val;
        });
        return obj;
    }

    equals(other) {
        return this.key == other.key;
    }
    pid() {
        return `${this.parent.pid()}_${this.key}`;
    }
    //for val local cache
    getCacheKey() {
        return this.cacheKey || this.pid();
    }
    tryCache(val) {
        if(this.cacheable) localStorage.setItem(this.getCacheKey(), val);
    }
    orCached(val) {
        return (this.cacheable && !val) ? localStorage.getItem(this.getCacheKey()) : val;
    }

    getTextFrom(data) {
        let val = data[this.key];
        if(this.type == colTypes._enum || this.type == colTypes._mult || this.type == colTypes._tree) {
            return xenumText(this.enumKey, val);
        }
        if(isPrimitive(val)) {
            return `${val}`;
        }
        return xOrEmpty(val);
    }

    onValChanged(val) {
        this.tryCache(val);
        this.parent.onColValChanged(this, val);
    }

    addToTable(_parent, val) {
        return _parent.append(`${val}`);
    }

    /*--------------------------*/
    /*-----for show in form-----*/
    /*--------------------------*/
    static showInForm(op, col) {
        return !((op.type == opTypes.add && !xShowcase.add(col)) || (op.type >= opTypes.edt && !xShowcase.edel(col)));
    }
    static validateFormVals(op, columns, vals) {
        return !columns.some(col=>col.required&&!col.validateAndPromptFormVal(op, vals[col.key]));
    }
    static getFormVals(columns) {
        return Column.packVals(columns, col=>col.getFormVal());
    }
    getFormVal() {  //dlgDataFunc
        return this.getFormValDom().val();
    }
    getFormValDom() {
        return $(`#dinput_${this.pid()}`);
    }
    addToForm(_parent, val) {
        this.addToForm0(_parent, this.parent, val)
    }
    addToForm0(_parent, op, val) {
        if(!Column.showInForm(op, this)) {
            return;
        }
        this.doAddToForm(_parent, val);
        //disabled
        if (op.type == opTypes.del || (op.type == opTypes.edt && !xShowcase.edit(this))) {
            this.getFormValDom().attr("disabled", true);
            return
        }
        xchange(this.getFormValDom(), ()=>this.onValChanged(this.getFormVal()));
        //for input validate
        if(this.required) {
            xinput(this.getFormValDom(), ()=>this.validateAndPromptFormVal(op, this.getFormVal()));
        }
    }
    doAddToForm(_parent, val) {
        let formValHtm = this.makeFormValHtm();
        let labelHtm = this.name;
        if(this.required){  //validation
            formValHtm = `${formValHtm}<div class="invalid-feedback">${this.invalidText()}</div>`;
            labelHtm   = `<span class="text-danger pr-1">*</span>${labelHtm}`;
        }
        let _dom = $(this.getColBoxHtm(labelHtm, formValHtm));
        _parent.append(_dom);
        this.setValToFormDom(this.getFormValDom(), this.orCached(val));
    }

    getColBoxHtm(labelHtm, formValHtm) {
        return `<div class="form-group row">
                    <label class="col-sm-2 col-form-label"><p class="float-right">${labelHtm}</p></label>
                    <div class="col-sm-10">${formValHtm}</div>
                </div>`;
    }
    makeFormValHtm() {    //dlgHtmFunc
        return `<input id="dinput_${this.pid()}" class="form-control" placeholder="${this.hint||this.name}" type="text">`;
    }
    setValToFormDom(dom, val) {//dlgMakeFunc
        if(isPrimitive(val) || val) dom.val(val).trigger('change')
    }
    //for validation
    invalidText(){
        return `${this.name} 不能为空`;
    }
    validateAndPromptFormVal(op, val){
        let isValid = !Column.showInForm(op, this) || this.validateFormVal(op, val);
        this.getFormValDom().toggleClass("is-invalid", !isValid);
        return isValid;
    }
    validateFormVal(op, val){
        return !!val;
    }
}

class AreaColumn extends Column {
    static _ = Column.regist([colTypes._area], this);
    makeFormValHtm() {    //dlgHtmFunc
        return `<textarea id="dinput_${this.pid()}" class="form-control" placeholder="${this.hint||this.name}" rows="8"/>`;
    }
}
class PasswordColumn extends Column {
    static _ = Column.regist([colTypes._pass], this);
    makeFormValHtm() {    //dlgHtmFunc
        return `<input id="dinput_${this.pid()}" class="form-control" placeholder="${this.hint||this.name}" type="password">`;
    }
//    getFormVal() {        //dlgDataFunc
//        return $.md5(super.getFormVal());
//    }
}
class NumberColumn extends Column {
    static _ = Column.regist([colTypes._numb], this);
    makeFormValHtm() {    //dlgHtmFunc
        return `<input id="dinput_${this.pid()}" class="form-control" placeholder="${this.hint||this.name}" type="number">`;
    }
}
class DateTimeColumn extends Column {
    static _ = Column.regist([colTypes._datetime], this);
    setValToFormDom(dom, val) {//dlgMakeFunc
        super.setValToFormDom(dom, val);
        xdatepicker(dom, this)
    }
}
class DateColumn extends Column {
    static _ = Column.regist([colTypes._date], this);
    setValToFormDom(dom, val) {//dlgMakeFunc
        super.setValToFormDom(dom, val);
        xdatepicker(dom, this, xformatDate)
    }
}
class TimeColumn extends Column {
    static _ = Column.regist([colTypes._time], this);
    setValToFormDom(dom, val) {//dlgMakeFunc
        super.setValToFormDom(dom, val);
        xdatepicker(dom, this, xformatTime)
    }
}
class EnumColumn extends Column {
    static _ = Column.regist([colTypes._enum, colTypes._mult], this);
    makeFormValHtm() {
        return `<select id="dinput_${this.pid()}" class="form-control select2" data-placeholder="${this.hint||this.name}"></select>`;
    }
    setValToFormDom(dom, val) {
        xselect2(dom, this, this.parent && this.parent.type == opTypes.qry);
        if(val || (val==0 && xenumHasZero(this.enumKey))) dom.val(val).trigger('change');
    }
}
class TreeColumn extends Column {
    static _ = Column.regist([colTypes._tree], this);
    getFormVal() {
        return this._tree.getSelectedIds();
    }
    setValToFormDom(dom, val) {
        this._tree = xtreeselect(dom, this);
        this._tree.setSelection(val);
    }
}
class BoolColumn extends Column {
    static _ = Column.regist([colTypes._bool], this);
    makeFormValHtm() {
        return `<div class="form-control custom-control custom-switch custom-switch-on-primary">
                    <input id="dinput_${this.pid()}" type="checkbox" class="custom-control-input" value="false">
                    <label for="dinput_${this.pid()}" class="custom-control-label ml-2"/>
                </div>`;
    }
    setValToFormDom(dom, val) {
        dom.change(function(){dom.val(this.checked);});//this:changed event
        if(val && !!JSON.parse(String(val))) dom.attr('checked', val).trigger('change');//support boolean string
    }
}
class FileColumn extends Column {
    static _ = Column.regist([colTypes._file], this);
    makeFormValHtm() {
        return `<div class="custom-file">
                    <input type="file" class="custom-file-input" id="dinput_${this.pid()}">
                    <label for="dinput_${this.pid()}" class="custom-file-label" id="dinput_${this.pid()}_label"></label>
                </div>
                <div id="dinput_${this.pid()}_preview"/>`;
    }
    setValToFormDom(dom, val) {
        let _fv = _v => {
            $(`#dinput_${this.pid()}_label`).html(_v);
            this.filePreview(_v);
        }
        if(val) _fv(val);

        xchange(dom, function(evt){
            let fi = evt.target.files[0];
            if(!fi) return;//cancel
            let fd = new FormData();
            fd.append('file', fi);
            $.ajax({
                url: `${xurl}/${xpaths.upload}`,
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
        return $(`#dinput_${this.pid()}_label`).html();
    }
    filePreview(val) {}
}
class ImagColumn extends FileColumn {
    static _ = Column.regist([colTypes._imag], this);
    filePreview(val) {
        $(`#dinput_${this.pid()}_preview`).html(`<img class="col-sm-2 img-thumbnail" src="${xHref(xpaths.preview,{name:val})}">`);
    }
}

class QueryColumn extends Column {
    static of(_origin) {
        let c = Object.assign(new QueryColumn(), _origin);
        c._origin = _origin;
        return c;
    }
    getColBoxHtm(labelHtm, formValHtm) {
        return `<div class="col-sm-2 float-left">${formValHtm}</div>`
    }
    makeFormValHtm() {
        return this._origin.makeFormValHtm();
    }
    tryCache(val) {
        this._origin.tryCache(val);
    }
    orCached(val) {
        return this._origin.orCached(val);
    }
    setValToFormDom(dom, val) {
        this._origin.setValToFormDom(dom, val);
    }
    getFormVal() {
        return this._origin.getFormVal();
    }
    onValChanged(val) {
        this._origin.onValChanged(val);
    }
}

let CollaspeId = 0;
class NestColumn extends Column {
    static _ = Column.regist([colTypes._model], this);
    onColValChanged(col, val) {
        this.parent.onColValChanged(col, val);
    }
    addToTable(_parent, val) {
        let _pid = ++ CollaspeId;
        let _ntable = $(`<table class="table table-bordered table-hover table-sm text-sm mb-0"></table>`);
        let _nthead = this.collapse
            ? $(`<thead data-toggle="collapse" data-target="#collaspse_${_pid}" aria-controls="collaspse_${_pid}" aria-expanded="true"></thead>`)
            : $(`<thead></thead>`);
        let _ntbody = this.collapse
            ? $(`<tbody id="collaspse_${_pid}" class="collapse"></tbody>`)
            : $(`<tbody></tbody>`);

        _parent.append(_ntable);
        _ntable.append(_nthead);
        _ntable.append(_ntbody);

        TableContent.showTableHead(_nthead, this.columns);
        TableContent.showTableBody(_ntbody, this.columns, this.type==colTypes._model?[val]:val)
    }
    makeFormValHtm() {
        return `<div id="dinput_${this.pid()}" class="border-left border-bottom text-sm"></div>`;
    }
    setValToFormDom(dom, val) {
        dom.empty();
        this.columns.forEach(col => {
            col.addToForm0(dom, this.parent, xOrGet(val, col.key));
        });
    }
    getFormVal() {
        return Column.getFormVals(this.columns);
    }
    validateFormVal(op, val) {
        return Column.validateFormVals(op, this.columns, val);
    }
}
class IndexedNestColumn extends Column {
    static of(_origin, _index, _compact) {
        //prototypeOf(get class.prototype) create(copy prototype), assign(copy fields)
        let c = Object.assign(Object.create(Object.getPrototypeOf(_origin)), _origin);
        c._origin = _origin;
        c._index  = _index;
        c._compact = _compact;
        c.pid = function() {
            return `${this._origin.parent.pid()}_${this._index}_${this._origin.key}`;
        };
        c.getColBoxHtm = function(labelHtm, formValHtm) {
            if(this._compact == 2)
                return `<label class="col-sm-2 col-form-label"><p class="float-right">${labelHtm}</p></label><div class="col-sm-4">${formValHtm}</div>`;
            if(this._compact == 3)
                return `<label class="col-sm-1 col-form-label" ><p class="float-right">${labelHtm}</p></label><div class="col-sm-3">${formValHtm}</div>`;
            return `<label class="col-sm-2 col-form-label"><p class="float-right">${labelHtm}</p></label><div class="col-sm-10">${formValHtm}</div>`;
        };
        return c;
    }
}
class ListColumn extends NestColumn {
    static _ = Column.regist([colTypes._list], this);
    _cIndex;
    makeFormValHtm() {
        return `<button id="dinput_${this.pid()}" type="button" style="border: dashed 1px #dee2e6;" class="form-group form-control">+</button>`;
    }
    getCompact() {
        return (this.compact && this.columns.length <= 3) ? this.columns.length : -1;
    }
    setValToFormDom(_dom, _val) {
        let compact = this.getCompact();
        this._cIndex = 0;   //reset index
        let makeElement = _v => {//make element
            let _outer = $(`<div id="dnest_${this.pid()}_${++this._cIndex}" class="border-left border-bottom position-relative form-group text-sm">`);
            _dom.before(_outer);
            
            if(compact != -1) {
                let _inner = $(`<div class="form-group row"></div>`);
                _outer.append(_inner);
                this.columns.forEach(col=>{    
                    IndexedNestColumn.of(col, this._cIndex, compact).addToForm0(_inner, this.parent, xOrGet(_v, col.key));
                });    
            } else {
                this.columns.forEach(col=>{
                    let _inner = $(`<div class="form-group row"></div>`);
                    _outer.append(_inner);
                    IndexedNestColumn.of(col, this._cIndex, compact).addToForm0(_inner, this.parent, xOrGet(_v, col.key));
                });
            }
            //minus btn
            let minusBtn = $(`<button type="button" class="position-absolute close" style="right:.5rem;bottom:.25rem;"><i class="fas fa-minus-circle fa-xs"></i></button>`);
            xclick(minusBtn, ()=>minusBtn.parent().remove());
            _outer.append(minusBtn);
            return _outer;
        };
        if(_val) _val.forEach(makeElement);
        xclick(_dom, ()=>makeElement());
    }
    getFormVal() {
        let compact = this.getCompact();
        let _data = [];
        for (let index = 1; index <= this._cIndex; ++index) {
            let _nest = $(`#dnest_${this.pid()}_${index}`);
            if(_nest.length && _nest.length>0) {
                let obj = Column.getFormVals(this.columns.map(col=>IndexedNestColumn.of(col, index, compact)));
                if(Object.keys(obj).length > 0) _data.push(obj);
            }
        }
        return _data;
    }
    validateFormVal(op, val) {
        return !val.some(_v=>!Column.validateFormVals(op, this.columns, _v));
    }
}

class EmailColumn extends Column{
    static _ = Column.regist([colTypes._email], this);
    invalidText() {
        return "邮箱地址不正确";
    }
    validateFormVal(op, val){
        return /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,5}$/.test(val);
    }
}

class PhoneColumn extends Column{
    static _ = Column.regist([colTypes._phone], this);
    invalidText() {
        return  "请输入正确的手机号";
    }
    validateFormVal(op, val){
        return /^[1][3,4,5,7,8,9][0-9]{9}$/.test(val);
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
        return `${this.option.parent.parent.name}&nbsp;/&nbsp;${this.option.name}`;//option->content->segment
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
            col.addToForm(_pdom, xOrGet(this.data, col.key));
        });
    }
    getFormData0() {
        return Column.getFormVals(this.option.columns());
    }
    getFormData() {
        let val = this.getFormData0();
        if(Column.validateFormVals(this.option, this.option.columns(), val))
            return val;
        return undefined;//throw error?
    }
    submit() {
        this.option.doPost(this.getFormData(), resp=>{
            $('#xdialog').modal('hide');
            this.option.onDataChanged((this.data = resp));//change data
        });
    }
}

/*-----------------------------*/
/*-------------Utils------------*/
/*-----------------------------*/
class Grouped {
    keys = [];
    vals = {};
    constructor(fkey, vset) {this.fkey = fkey;(vset||[]).forEach(v=>this.add(v));}
    add(val) {
        let key = val[this.fkey];
        this.keys.includes(key) || this.keys.push(key);
        this.vals[key] = this.vals[key] || []
        this.vals[key].push(val);
    }
    val(key) {return this.vals[key];}
    forEach(func) {this.keys.forEach(k=>func(k, this.vals[k]))};
}

//dom parent
class XContainer {
    constructor(htm) {this.dom = htm;}
    empty() {this.dom.empty(); return this;}
    child(id) {return this.dom.find(`#${id}`);}
    append(htm){this.dom.append(htm);return this;}
}
