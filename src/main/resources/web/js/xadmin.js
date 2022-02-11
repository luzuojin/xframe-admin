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
    _numb: 100,
    _pass: 9,
    _mult: 20,//multi enum select
    _date: 31,
    _time: 32,
    _model:80,
    _list: 81,
    _text_phone: 101,
    _text_email: 102,
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
            headers: {"X-Token": xtoken()},
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
        headers: {"X-Token": xtoken()},
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
        headers: Object.assign({"X-Token": xtoken()}, _headers),
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
    // dom.off('change');
    dom.on('change', func);
}

function xinput(dom, func){
    dom.off('input');
    dom.on('input', func);
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
                minimumResultsForSearch: 10,
                allowClear: !cacheEnable    //碰巧 开启cache的select框 一般都不需要clear操作
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

function showSiderbar(data) {
    data.chapters.forEach(c=>Chapter.of(c).show());
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