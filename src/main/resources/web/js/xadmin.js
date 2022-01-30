var xurl = window.location.origin.startsWith("http") ? window.location.origin : "http://127.0.0.1:8001";
var xpaths = {
    summary: "basic/summary",
    xenum  : "basic/enum",
    upload : "basic/upload",
    preview: "basic/preview"
}

var xenumCache = {};
function xenum(key) {
    if(key in xenumCache) {
        return xenumCache[key];
    } else {
        let xenumData = $.parseJSON($.ajax({
            type: "GET",
            url: `${xurl}/${xpaths.xenum}?key=${key}`,
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
function xConcat(left, delimiter, right) {
    return (left && right) ? `${left}${delimiter}${right}` : (left ? left : (right ? right : ''));
}
function xOrElse(val, oth) {
    return val ? val : oth;
}
function xOrEmpty(val) {
    return xOrElse(val, '');
}
function xOrGet(val, key) {
    return val ? val[key] : undefined;
}

function doGet(path, func) {
    doGet0(path, doResp(func));
}
function doGet0(path, func) {
    xlatestOp = undefined;
    $.ajax({
        type: 'get',
        url: `${xurl}/${path}`,
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
        url: `${xurl}/${path}`,
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
            xtoast.error(resp.text?resp.text:`${xlatestOp.name} 失败`);
        } else if(resp.status == -2) { //提示
            xtoast.info(resp.text)
            func(resp.data);
        } else if(xlatestOp && xlatestOp.name) {
            xtoast.succ(`${xlatestOp.name} 成功`);
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
//    btn.off('click');
    btn.click(func);
}

function xchange(dom, func) {
//    dom.off('change');
    dom.on('change', func);
}

function xinput(dom, func){
//    dom.off('input');
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

// show contents
function showSummary() {
    doGet(xpaths.summary, showSidebar);
}
function showSidebar(data) {
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

// profile
var xuser;
function xtoken() {
    return xuser ? xuser.token : '';
}

//不严谨. 内网访问判定-自动登录
function isLocalUrl() {
    if( xurl.startsWith('http://127.0.0.') ||
        xurl.startsWith('http://10.') ||
        xurl.startsWith('http://172.16.') ||
        xurl.startsWith('http://192.168.')
        ){
        return true;
    }
    return false;
}

const _inputs = [
    {key:"name",hint:"用户名",type:xTypes._text,show:13},
    {key:"passw",hint:"密码",type:xTypes._pass,show:15}
];
const _navi = new Navi(new Navi(null, '用户', 'basic'), 'unused', 'profile');//segment->detail->option

function showUser(user) {
    xuser = user;
    $('#xuser').empty();
    $('#xuser').append(`<i class="fas fa-user"></i> ${user.name}`);

    $('#xcontent').append('<div class="card-header"><h3>Welcome</h3></div>');

    xclick($('#xuser_logout'), ()=>{
        Option.of(_navi, {
            name: "登出",
            type: opTypes.del,
            inputs: _inputs
        }).doPost({name: xuser.name, passw: ''}, ()=>location.reload());
    });
    xclick($('#xuser_profile'), ()=>{
        let op = Option.of(_navi, {
            name: "修改密码",
            type: opTypes.edt,
            inputs: _inputs
        });
        op.popup({name: xuser.name, passw: ''});
    });
}

function doLogin() {
    let op = Option.of(_navi, {
        name: "登录",
        type: opTypes.add,
        inputs: _inputs
    });
    let cb = op.onDataChanged = data=>{
        showUser(data);
        showSummary();  //show
    };
    if(isLocalUrl()) {
        let cb0 = resp=>{
            if(resp.status == -1) {
                op.popup();
            } else {
                cb(resp.data);
            }
        }
        op.doPost0({name:'local'}, cb0);
    } else {
        op.popup();
    }
}

$(function () {
    initial();
    doLogin();
});