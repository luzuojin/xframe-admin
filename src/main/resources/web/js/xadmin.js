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
function xvalue(value) {
    return value == undefined ? '' : value;
}
function xvalueByKey(value, key) {
    return value ? xvalue(value[key]) : '';
}

function showSummary() {
    doGet(xpaths.summary, showSiderbar);
}

function doGet(path, func) {
    $.ajax({
        type: 'get',
        url: '{0}/{1}'.format(xurl, path),
        headers: {"x-token": xtoken()},
        dataType: 'json',
        success: doResp(func)
    });
}

var xlatestOp;
var httpTypes = ['_', 'get', 'post', 'put', 'delete']
function doPost(path, op, data, func, _headers={}) {
    xlatestOp = op;
    $.ajax({
        type: httpTypes[op.type],
        url: '{0}/{1}'.format(xurl, path),
        data: JSON.stringify(data),
        headers: Object.assign({"x-token": xtoken()}, _headers),
        dataType: 'json',
        success: doResp(func)
    });
}

function doResp(func) {
    return function(resp) {
        if(resp.status == -1) {
            if(resp.data) {
                xtoast.error(resp.data);
            } else {
                xtoast.error('{0} 失败'.format(xlatestOp.name));
            }
        } else if(xlatestOp && xlatestOp.name) {
            xtoast.succ('{0} 成功'.format(xlatestOp.name));
            func(resp.data);
        } else {
            func(resp.data);
        }
        xlatestOp = undefined;
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
    error: function(msg) {this.show('error', msg)}
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

function xselect2(e, xinput) {
    let k = xinput.enumKey;
    let d = xenum(k);
    let m = xinput.type==xTypes._mult;
    let s = e.select2({
                theme: 'bootstrap4',
                dropdownAutoWidth : true,
                width: 'auto',
                data: d,
                multiple: m,
                minimumResultsForSearch: 10
            });
    e.val('').trigger('change');//设置默认不选择
    //多选 无记忆
    if(m) return;
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

let tabctxhtm= `
            <div class="card-header p-0 border-bottom-0">
            <ul id="xtabContainer" class="nav nav-tabs" role="tablist"></ul>
            </div>
            `;
let tabelehtm= function(seg){
    return `
        <li class="nav-item">
            <a id="segtab_{0}_{1}" class="nav-link text-dark" data-toggle="pill" href="javascript:void(0);" role="tab" aria-selected="false">{2}</a>
        </li>`.format(seg.cpath, seg.path, seg.name);
}
let tabeledom= function(seg) {return $('#segtab_{0}_{1}'.format(seg.cpath, seg.path));}

function showSiderbar(data) {
    let fixSegDetail = function(seg, cpath, segpath) {
        seg.cpath = cpath;
        seg.detail.path = seg.path;
        seg.detail.segname = seg.name;
        seg.detail.segpath = segpath;
    }
    for(let chapter of data.chapters){
        $('#xsiderbar').append(chapterhtm(chapter));
        if(chapter.padded) {//有tab页
            for(let pseg of chapter.padded) {
                pseg.cpath = chapter.path
                let _segments = [];
                for(let tsegIdx in chapter.segments) {
                    let tseg = chapter.segments[tsegIdx];
                    let nseg = Object.assign({}, tseg, {detail: Object.assign({},tseg.detail)});//copy seg
                    fixSegDetail(nseg, chapter.path, chapter.path.urljoin(pseg.path).urljoin(tseg.path))
                    nseg.index = tsegIdx;
                    _segments.push(nseg);
                }
                chapterdom(chapter).append(segmenthtm(pseg))
                xclick(segmentdom(pseg), showDetailFunc(pseg, _segments));
            }
        } else {
            for(let seg of chapter.segments){//二级菜单
                fixSegDetail(seg, chapter.path, chapter.path.urljoin(seg.path));
                chapterdom(chapter).append(segmenthtm(seg));
                xclick(segmentdom(seg), showDetailFunc(seg));
            }
        }
    }
}

var xlatestSeg;
var xlatestTab={};
function showDetailFunc(seg, segTabs=undefined) {
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
        
        if(segTabs) {
            $('#xcontent').append($(tabctxhtm));
            $('#xcontent').append($(detailBodyHtm));
            for(let segTab of segTabs) {
                $('#xtabContainer').append(tabelehtm(segTab));
                xclick(tabeledom(segTab), showDetailFuncByTab(segTab));
            }
            //show first tab
            let showIndex = 0;
            if(seg.cpath in xlatestTab) {
                showIndex = xlatestTab[seg.cpath];
            }
            showDetailFuncByTab(segTabs[showIndex])();
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
        xlatestTab[segTab.cpath] = segTab.index;
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