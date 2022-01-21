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

let _inputs = [
    {key:"name",hint:"用户名",type:xTypes._text,show:13},
    {key:"passw",hint:"密码",type:xTypes._pass,show:15}
];
let _navi = new Navi(new Navi(null, '用户', 'basic'), "用户", "profile");

function showUser(user) {
    xuser = user;
    $('#xuser').empty();
    $('#xuser').append(`<i class="fas fa-user"></i> {0}`.format(user.name));

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
        op.onClick({name: xuser.name, passw: ''});
    });
}

function doLogin(func) {
    let op = Option.of(_navi, {
        name: "登录",
        type: opTypes.add,
        inputs: _inputs
    });
    let cb = op.onDataChanged = data=>{
        showUser(data); func();
    };
    if(isLocalUrl()) {
        let cb0 = resp=>{
            if(resp.status == -1) {
                op.onClick();
            } else {
                cb(resp.data);
            }
        }
        op.doPost0({name:'local'}, cb0);
    } else {
        op.onClick();
    }
}