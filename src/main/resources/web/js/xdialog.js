//html
let dlgColumn= `
            <div class="form-group row">
              <label class="col-sm-2 col-form-label" for="dinput_{0}_{1}"><p class="float-right">{2}</p></label>
              <div class="col-sm-10">{3}</div>
            </div>
            `;
let dlgText= `<input id="dinput_{0}_{1}" class="form-control" placeholder="{2}" type="{3}">`;
let dlgEnum= `<select id="dinput_{0}_{1}" class="form-control select2" data-placeholder="{2}" style="width:100%"></select>`;
let dlgBool= `
            <div class="form-control custom-control custom-switch custom-switch-on-primary">
              <input id="dinput_{0}_{1}" type="checkbox" class="custom-control-input" value="false">
              <label class="custom-control-label" for="dinput_{0}_{1}" style="margin-left:7.5px;"/>
            </div>
            `;

let dlgInputDom= function(idkey, inputkey){return $('#dinput_{0}_{1}'.format(idkey, inputkey));}

let addDlgInput= function(parent, xinput, idkey, value) {
    if(xinput.type==xTypes._enum || xinput.type==xTypes._mult) {
        let _col = dlgEnum.format(idkey, xinput.key, xinput.hint);
        parent.append(dlgColumn.format(idkey, xinput.key, xinput.hint, _col));
        xselect2(dlgInputDom(idkey, xinput.key), xinput);
        if(value) dlgInputDom(idkey, xinput.key).val(value).trigger('change');
    } else if(xinput.type==xTypes._bool) {
        let _col = dlgBool.format(idkey, xinput.key);
        parent.append(dlgColumn.format(idkey, xinput.key, xinput.hint, _col));
        let ckbox = dlgInputDom(idkey, xinput.key);
        ckbox.change(function (){ckbox.val(this.checked);});
        if(value) ckbox.attr('checked', value);
    } else {
        let _type = (xinput.type==xTypes._pass) ? 'password' : 'text';
        let _col = dlgText.format(idkey, xinput.key, xinput.hint, _type);
        parent.append(dlgColumn.format(idkey, xinput.key, xinput.hint, _col));
        let _text = dlgInputDom(idkey, xinput.key);
        if(value) _text.val(value).trigger('change');
        if(xinput.type==xTypes._time) xdatepicker(_text);
    }
}

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
    return model ? model[key] : '';
}

function showDialog(dlg, op, model, func) {
    $('#xdialog_title').empty();
    $('#xdialog_form').empty();

    $('#xdialog_title').append(dialogTitle(dlg, op))
    for(let column of dlg.opColumns(op)){
        if(op.opType == opTypes.add && !xcolumn.add(column)) continue;
        if(op.opType >= opTypes.edt && !xcolumn.edel(column)) continue;
        let val = dialogInputVal(model, column.key);
        addDlgInput($('#xdialog_form'), column, dlg.ident, val);
        if (op.opType == opTypes.del || (model && !xcolumn.edit(column))) {
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