//html
let dlgColumn= `
            <div class="form-group row">
              <label class="col-sm-2 col-form-label" for="dinput_{0}_{1}"><p class="float-right">{2}</p></label>
              <div class="col-sm-10">{3}</div>
            </div>
            `;
let dlgText= `<input id="dinput_{0}_{1}" class="form-control" placeholder="{2}" type="{3}">`;
let dlgArea= `<textarea id="dinput_{0}_{1}" class="form-control" placeholder="{2}" rows="3"/>`;
let dlgEnum= `<select id="dinput_{0}_{1}" class="form-control select2" data-placeholder="{2}" style="width:100%"></select>`;
let dlgBool= `
            <div class="form-control custom-control custom-switch custom-switch-on-primary">
              <input id="dinput_{0}_{1}" type="checkbox" class="custom-control-input" value="false">
              <label class="custom-control-label" for="dinput_{0}_{1}" style="margin-left:7.5px;"/>
            </div>
            `;

let dlgInputDom= function(idkey, columnkey){return $('#dinput_{0}_{1}'.format(idkey, columnkey));}

let addDlgInput= function(parent, xcolumn, idkey, value) {
    let _htm = getFuncFrom(dlgHtmlFuncs, xcolumn.type)(idkey, xcolumn);
    parent.append(dlgColumn.format(idkey, xcolumn.key, xcolumn.hint, _htm));
    let _dom = dlgInputDom(idkey, xcolumn.key);
    getFuncFrom(dlgMakeFuncs, xcolumn.type)(_dom, xcolumn, value);
}

let dlgHtmlFuncs = {};
let dlgMakeFuncs = {};

//enum
setFuncTo(dlgHtmlFuncs, [xTypes._enum, xTypes._mult],
    function(id, c){//idkey, xcolumn
        return dlgEnum.format(id, c.key, c.hint);
    });
setFuncTo(dlgMakeFuncs, [xTypes._enum, xTypes._mult],
    function(_d, c, v){//_domcument, xcolumn, value
        xselect2(_d, c);
        if(v && v != 0) _d.val(v).trigger('change')
    });

//bool
setFuncTo(dlgHtmlFuncs, [xTypes._bool],
    function(id, c) {
        return dlgBool.format(id, c.key);
    });
setFuncTo(dlgMakeFuncs, [xTypes._bool],
    function(_d, c, v){
        _d.change(function(){_d.val(this.checked);});
        if(v) _d.attr('checked', v);
    });

//area
setFuncTo(dlgHtmlFuncs, [xTypes._area],
    function(id, c) {
        return dlgArea.format(id, c.key, c.hint);
    });
setFuncTo(dlgMakeFuncs, [xTypes._area],
    function(_d, c, v){
        if(v) _d.val(v).trigger('change');
    });

//text
setFuncTo(dlgHtmlFuncs, [xTypes._text, xTypes._pass, xTypes._datetime, xTypes._date, xTypes._time],
    function(id, c) {
        return dlgText.format(id, c.key, c.hint, (c.type==xTypes._pass)?'password':'text')
    });
setFuncTo(dlgMakeFuncs, [xTypes._text, xTypes._pass, xTypes._datetime, xTypes._date, xTypes._time],
    function(_d, c, v){
        if(v) _d.val(v).trigger('change');
        if(c.type==xTypes._datetime) xdatepicker(_d);//time pick
        if(c.type==xTypes._date) xdatepicker(_d, xformatDate);//time pick
        if(c.type==xTypes._time) xdatepicker(_d, xformatTime);//time pick
    });

/*
dialog = {
    ident;
    segname; //parent name
    segpath; //submit path
    opColumns(op);
}
*/
function dialogTitle(dlg, op) {
    return '{0}&nbsp;/&nbsp;{1}'.format(dlg.segname, op.name)
}

function dialogInputVal(model, key) {
    return model ? model[key] : undefined;
}

function showDialog(dlg, op, model, func) {
    $('#xdialog_title').empty();
    $('#xdialog_form').empty();

    $('#xdialog_title').append(dialogTitle(dlg, op))
    for(let column of dlg.opColumns(op)){
        if(op.type == opTypes.add && !xcolumn.add(column)) continue;
        if(op.type >= opTypes.edt && !xcolumn.edel(column)) continue;
        let val = dialogInputVal(model, column.key);
        addDlgInput($('#xdialog_form'), column, dlg.ident, val);
        if (op.type == opTypes.del || (model && !xcolumn.edit(column))) {
            dlgInputDom(dlg.ident, column.key).attr("disabled", true);
        }
    }
    xclick($('#xdialog_submit'), function(){submitDialog(dlg, op, func);})
    modalShow();
}

function submitDialog(dlg, op, func) {
    var model = {};
    for(let column of dlg.opColumns(op)) {
        let key = column.key;
        model[key] = dlgInputDom(dlg.ident, key).val();
        if(column.type==xTypes._pass) model[key]=$.md5(model[key]);
    }
    doPost(dlg.segpath.urljoin(op.path), op, model,
        function(resp) {
            modalHide();
            func(resp);
        }
    );
}

function modalShow() {
    $('#xdialog').modal('show');
}

function modalHide() {
    $('#xdialog').modal('hide');
}

function setFuncTo(c, keys, func) {
    for(let key of keys) {
        c[key] = func;
    }
}

function getFuncFrom(c, key) {
    return c[key];
}

