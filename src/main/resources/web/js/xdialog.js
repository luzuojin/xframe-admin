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


function dialogTitle(segment, op) {
    return '{0}&nbsp;/&nbsp;{1}'.format(segment.name, op.name)
}

function dialogInputVal(seg, model, key) {
    return model ? model[key] : '';
}

function showDialog(segment, op, model, func) {
    $('#xdialog_title').empty();
    $('#xdialog_form').empty();

    $('#xdialog_title').append(dialogTitle(segment, op))
    for(let column of opColumns(segment, op)){
        if(op.opType == opTypes.add && !xcolumn.add(column)) continue;
        if(op.opType >= opTypes.edt && !xcolumn.edel(column)) continue;
        let val = dialogInputVal(segment, model, column.key);
        addDlgInput($('#xdialog_form'), column, segment.path, val);
        if (op.opType == opTypes.del || (model && !xcolumn.edit(column))) {
            dlgInputDom(segment.path, column.key).attr("disabled", true);
        }
    }
    xclick($('#xdialog_submit'), function(){submitDialog(segment, op, func);})
    modalShow();
}

function opColumns(seg, op) {
    return (op.inputs && op.inputs.length > 0) ? op.inputs : seg.columns;
}

function submitDialog(segment, op, func) {
    var model = {};
    for(let column of opColumns(segment, op)) {
        let key = column.key;
        model[key] = dlgInputDom(segment.path, key).val();
        if(column.type==xTypes._pass) model[key]=$.md5(model[key]);
    }
    doPost(segpath(segment), op, model,
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