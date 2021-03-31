String.prototype.format = function() {
    var str = this;
    for (let key in arguments) {
        let reg = new RegExp("({)" + key + "(})", "g");
        str = str.replace(reg, arguments[key]);
    }
    return str;
}
String.prototype.urljoin = function(sub) {
    var url = this;
    if(sub) {
        url = url + '/' + sub;
    }
    return url;
}


var xurl = window.location.origin.startsWith("http") ? window.location.origin : "http://127.0.0.1:8001";
var xpaths = {
    summary: "basic/summary",
    xenum  : "basic/enum",
    upload : "basic/upload",
    preview: "basic/preview"
}

//option types
var opTypes = {
    ini: -1,
    qry: 1,
    add: 2,
    edt: 3,
    del: 4,
    flx: 5
}

//text types
var xTypes = {
    _text: 0,
    _bool: 1,
    _enum: 2,
    _datetime: 3,
    _area: 4,
    _file: 5,
    _imag: 6,
    _pass: 9,
    _mult: 20,//multi enum select
    _date: 31,
    _time: 32,
    _model:80,
    _list: 81,
}

var xcolumn = {
    list: function(c){return (c.show & 1) > 0;},
    edit: function(c){return (c.show & 2) > 0;},
    add : function(c){return (c.show & 4) > 0;},
    edel: function(c){return (c.show & 8) > 0;},
}

var xenumCache = {};
function xenum(key) {
    if(key in xenumCache) {
        return xenumCache[key];
    } else {
        let xenumData = $.parseJSON($.ajax({
            type: "GET",
            url: "{0}/{1}?key={2}".format(xurl, xpaths.xenum, key),
            headers: {"x-token": xtoken()},
            cache: false,
            async: false
        }).responseText).data;
        xenumCache[key] = xenumData;
        return xenumData;//multi,key
    }
}
function xenumText(key, id) {
    let simpleText = function(_id) {
        let data = xenum(key);
        for(let e of data) {
            if(e.id == _id) return e.text;
        }
        return _id;
    }
    if(Array.isArray(id)) {
        let texts = [];
        for(let _id of id) {
            texts.push(simpleText(_id))
        }
        return texts;
    } else {
        return simpleText(id);
    }
}
function xOrElse(val, oth) {
    return val==undefined ? oth : val;
}
function xvalue(val) {
    return xOrElse(val, '');
}
function xvalueByKey(val, key) {
    return val ? xvalue(val[key]) : '';
}

function showSummary() {
    doGet(xpaths.summary, showSiderbar);
}

function doGet(path, func) {
    doGet0(path, doResp(func));
}
function doGet0(path, func) {
    xlatestOp = undefined;
    $.ajax({
        type: 'get',
        url: '{0}/{1}'.format(xurl, path),
        headers: {"x-token": xtoken()},
        dataType: 'json',
        success: func
    });
}

var xlatestOp;
var httpTypes = ['_', 'get', 'post', 'put', 'delete']
function doPost(path, op, data, func, _headers={}) {
    doPost0(path, op, data, doResp(func), _headers);   
}
function doPost0(path, op, data, func, _headers={}) {
    if(!data)return;
    xlatestOp = op;
    $.ajax({
        type: httpTypes[op.type],
        url: '{0}/{1}'.format(xurl, path),
        data: JSON.stringify(data),
        headers: Object.assign({"x-token": xtoken()}, _headers),
        dataType: 'json',
        success: func
    });
}
function doResp(func) {
    return function(resp, textStatus, xhr) {
        clrEnumCaches(xhr);
        if(resp.status == -1) {
            xtoast.error(resp.text?resp.text:'{0} 失败'.format(xlatestOp.name));
        } else if(resp.status == -2) { //提示
            xtoast.info(resp.text)
            func(resp.data);
        } else if(xlatestOp && xlatestOp.name) {
            xtoast.succ('{0} 成功'.format(xlatestOp.name));
            func(resp.data);
        } else {
            func(resp.data);
        }
        xlatestOp = undefined;
    }
}
function clrEnumCaches(xhr) {
    if(xhr) {
        let clrEnumKeys = xhr.getResponseHeader("ClrEnumKeys");
        if(clrEnumKeys) {
            for(let clrEnumKey of clrEnumKeys.split(",")) {
                delete xenumCache[clrEnumKey]
            }
        }
    }
}

var xtoast = {
    show: function(type, msg) {
            Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 1500
        }).fire({
            type: type,
            title: msg
        });
    },
    succ : function(msg) {this.show('success', msg)},
    error: function(msg) {this.show('error', msg)},
    info : function(msg) {this.show('info', msg)}
}

function xclick(btn, func) {
    btn.off('click');
    btn.click(func);
}

function xchange(dom, func) {
    dom.off('change');
    dom.on('change', func);
}

var xformatDatetime = 'YYYY-MM-DD HH:mm:ss';
var xformatDate = 'YYYY-MM-DD';
var xformatTime = 'HH:mm:ss';
function xdatepicker(e, _format=xformatDatetime) {
    e.datetimepicker({
        format: _format,
        useCurrent: 'day',
        showClose: false,
        showTodayButton: false,
        icons: {time: "fa fa-clock"},
        locale: 'zh-cn'
    });
}

function xselect2(e, xinput, cacheEnable=false) {
    let k = xinput.enumKey;
    let d = xenum(k);
    let m = xinput.type==xTypes._mult;
    let s = e.select2({
                theme: 'bootstrap4',
                dropdownAutoWidth : true,
                width: 'auto',
                data: d,
                multiple: m,
                minimumInputLength: (d.length > 100 ? 2 : 0),
                minimumResultsForSearch: 10
            });
    e.val('').trigger('change');//设置默认不选择
    //多选 无记忆
    if(!cacheEnable || m) return;
    //设置cache值
    if(eCaches[k]) s.val(eCaches[k]).trigger('change');
    //设值完成之后添加值变化监听
    s.on('change', function(evt){
        if(this.value) eCaches[k] = this.value;
    });
}
var eCaches = {};

//main html
let chapterhtm= function(chapter){
return `<li class="nav-item has-treeview">
            <a class="nav-link" href="javascript:void(0);">
              <i class="nav-icon fab fa-gg"/>
              <p>
                {0}
                <i class="right fas fa-angle-left"/>
              </p>
            </a>
            <ul id="chapter_{1}" class="nav nav-treeview"/>
        </li>
        `.format(chapter.name, chapter.path)
}
let chapterdom= function(chapter){return $('#chapter_{0}'.format(chapter.path));};

let segmenthtm= function(seg){
    return `
        <li class="nav-item">
            <a id="seg_{0}_{1}" class="nav-link" href="javascript:void(0);">
              <i class="fas fa-paperclip"/>
              <p>{2}</p>
            </a>
        </li>
        `.format(seg.cpath, seg.path, seg.name);
}
let segmentdom= function(seg){return $('#seg_{0}_{1}'.format(seg.cpath, seg.path));}

let tabelehtm= function(seg){
    return `
        <li class="nav-item">
            <a id="segtab_{0}_{1}" class="nav-link text-dark" data-toggle="pill" href="javascript:void(0);" role="tab" aria-selected="false">{2}</a>
        </li>`.format(seg.cpath, seg.path, seg.name);
}
let tabeledom= function(seg) {return $('#segtab_{0}_{1}'.format(seg.cpath, seg.path));}

function showSiderbar(data) {
    let copySegment = function(navi, _detail, _index) {
        return Object.assign({}, navi, {detail: Object.assign({}, _detail)}, {index: _index});
    }
    let psetSegment = function(seg, chapter, navi={}) {
        seg.cpath = chapter.path;
        seg.detail.path = seg.path;
        seg.detail.segname = seg.name;
        seg.detail.segpath = chapter.path.urljoin(xvalue(navi.path)).urljoin(seg.path);
    };
    let showSegment = function(seg, chapter) {
        chapterdom(chapter).append(segmenthtm(seg))
        xclick(segmentdom(seg), showDetailFunc(seg));
    };
    for(let chapter of data.chapters){
        $('#xsiderbar').append(chapterhtm(chapter));
        if(chapter.navis) {
            //仅有一个segment且path={x}, 不展示tab, 合并navis~segments
            if(chapter.segments.length == 1 && chapter.segments[0].path.startsWith("{")) {
                let wseg = chapter.segments[0];//wildcard segment
                for(let pseg of chapter.navis) {
                    let seg = copySegment(pseg, wseg.detail)
                    psetSegment(seg, chapter);
                    showSegment(seg, chapter);
                }
            } else {//展示tab
                for(let pseg of chapter.navis) {
                    pseg.cpath = chapter.path
                    pseg.tabs = [];
                    for(let tsegIdx in chapter.segments) {
                        let tseg = chapter.segments[tsegIdx];
                        let nseg = copySegment(tseg, tseg.detail, tsegIdx);//copy seg
                        psetSegment(nseg, chapter, pseg);
                        pseg.tabs.push(nseg);
                    }
                    showSegment(pseg, chapter)
                }
            }
        } else {
            for(let seg of chapter.segments){//二级菜单
                psetSegment(seg, chapter);
                showSegment(seg, chapter)
            }
        }
    }
}

var xlatestSeg;
function showDetailFunc(seg) {
    return function() {
        if(xlatestSeg)
            segmentdom(xlatestSeg).removeClass('active'); 
        xlatestSeg = seg;
        segmentdom(seg).addClass('active');

        $('#xcontent').empty();
        let detailBodyHtm = `
                <div class="card-header">
                  <div class="row">
                    <div id="xboxhead" class="clearfix w-100"></div>
                  </div>
                </div>
                <div id="xboxbody" class="card-body">
                </div>`;
        if(seg.tabs) {
            let tabctxhtm= `
                    <div class="card-header p-0 border-bottom-0">
                    <ul id="xtabContainer" class="nav nav-tabs" role="tablist"></ul>
                    </div>
                    `;
            $('#xcontent').append($(tabctxhtm));
            $('#xcontent').append($(detailBodyHtm));
            for(let segTab of seg.tabs) {
                $('#xtabContainer').append(tabelehtm(segTab));
                xclick(tabeledom(segTab), showDetailFuncByTab(segTab));
            }
            //show first tab
            let showIndex = 0;
            if(xlatestSeg.tab) {
                showIndex = xlatestSeg.tab.index;
            }
            showDetailFuncByTab(seg.tabs[showIndex])();
        } else {
            $('#xcontent').append($(detailBodyHtm));
            showDetail(seg.detail);
        }
    };
}

function showDetailFuncByTab(segTab) {
    return function() {
        if(xlatestSeg.tab) {
            tabeledom(xlatestSeg.tab).removeClass('active');
            tabeledom(xlatestSeg.tab).attr('aria-selected', false);
        }
        xlatestSeg.tab = segTab;
        tabeledom(segTab).addClass('active');
        tabeledom(segTab).attr('aria-selected', true);
        showDetail(segTab.detail);
    }
}

// popup dialog keypress listening
function initial() {
    $('#xdialog').on('hide.bs.modal ', function(){
        $(document).off('keyup');
    });
    $('#xdialog').on('shown.bs.modal', function(){
        $('#xdialog').focus();
        $(document).on('keyup', function(e){
            if(e.keyCode==13) {$("#xdialog_submit").trigger('click');}
            if(e.keyCode==27) {$("#xdialog_close").trigger('click');}
        });
    });
}

$(function () {
    initial();
    doLogin(showSummary);
});