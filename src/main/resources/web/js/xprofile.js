var xuser;

let userhtm= `<i class="fas fa-user"></i> {0}`;

function xtoken() {
    return xuser ? xuser.token : '';
}

var xuserseg = {//用户登录/修改密码...
    cpath: "basic",
    path:  "profile",
    name:  "用户",
    columns: [
      {key:"name",hint:"用户名",type:xTypes._text,show:13},
      {key:"passw",hint:"密码",type:xTypes._pass,show:15}
    ]
};

var xuserdlg = {
    ident: xuserseg.path,
    segname: xuserseg.name,
    segpath: xuserseg.cpath.urljoin(xuserseg.path),
    opColumns: function(op){return xuserseg.columns}
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

//submit login without show dialog
function withoutDialog(dlg, op, data, func) {
    doPost0(dlg.segpath.urljoin(op.path), op, data, func);
}

function doLogin(func) {
    let op = {
        name: "登录",
        type: opTypes.add
    };
    let cb = data=>{
        onLogin(data); func();
    };    
    if(isLocalUrl()) {
        let cb0 = resp=>{
            if(resp.status == -1) {
                showDialog(xuserdlg, op, undefined, cb);
            } else {
                cb(resp.data);
            }
        }
        withoutDialog(xuserdlg, op, {name:'local'}, cb0);
    } else {
        showDialog(xuserdlg, op, undefined, cb);
    }
}

function onLogin(data) {
    showUser(data);
}

function showUser(user) {
    xuser = user;
    $('#xuser').empty();
    $('#xuser').append(userhtm.format(user.name));

    $('#xcontent').append('<div class="card-header"><h3>Welcome</h3></div>');

    xclick($('#xuser_logout'), function(){
        let op = {
            // name: "登出",
            type: opTypes.del
        };
        doPost(xuserdlg.segpath, op, {name: xuser.name, passw: ''}, function(resp){
            xuser = undefined;
            location.reload();
        });
    });

    xclick($('#xuser_profile'), function(){
        let op = {
            name: "修改密码",
            type: opTypes.edt
        };
        let model = {
            name: xuser.name,
            passw: ''
        }
        showDialog(xuserdlg, op, model, function(){});
    });
}