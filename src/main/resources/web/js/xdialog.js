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
let dlgImag=`<img class="col-sm-2 img-thumbnail" src="{0}">`;
let dlgFile=`
            <div class="custom-file">
              <input type="file" class="custom-file-input" id="dinput_{0}_{1}">
              <label class="custom-file-label" id="dinput_{0}_{1}_label" for="dinput_{0}_{1}"></label>
            </div>
            <div id="dinput_{0}_{1}_preview"/>
            `;

let fileLabelDom = function(_id) {return $('#{0}_label'.format(_id));};
let filePreviewDom = function(_id) {return $('#{0}_preview'.format(_id));};

//dialog input
let dlgInputId = function(idkey, columnkey){return 'dinput_{0}_{1}'.format(idkey, columnkey);};
let dlgInputDom= function(idkey, columnkey){return $('#dinput_{0}_{1}'.format(idkey, columnkey));};
let dlgInputVal= function(idkey, column){
    let func = getFuncFrom(dlgDataFuncs, column.type);
    return func ? func(idkey, column) : dlgInputDom(idkey, column.key).val();
};

let addDlgInput= function(parent, xcolumn, idkey, value) {
    let _htm = getFuncFrom(dlgHtmlFuncs, xcolumn.type)(idkey, xcolumn);
    parent.append(dlgColumn.format(idkey, xcolumn.key, xcolumn.hint, _htm));
    let _dom = dlgInputDom(idkey, xcolumn.key);
    getFuncFrom(dlgMakeFuncs, xcolumn.type)(_dom, xcolumn, value);
}

let dlgHtmlFuncs = {};
let dlgMakeFuncs = {};
let dlgDataFuncs = {};

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
setFuncTo(dlgDataFuncs, [xTypes._pass],
    function(k, c){
        return $.md5(dlgInputDom(k, c.key).val());
    });


//file
setFuncTo(dlgHtmlFuncs, [xTypes._file, xTypes._imag],
    function(id, c) {
        return dlgFile.format(id, c.key, c.hint)
    });
setFuncTo(dlgMakeFuncs, [xTypes._file, xTypes._imag],
    function(_d, c, v){
        let _id = _d.attr('id');
        let _fv = function(_v) {
            fileLabelDom(_id).html(_v);
            if(c.type == xTypes._imag)
                filePreviewDom(_id).html(dlgImag.format('{0}/{1}?name={2}&x-token={3}'.format(xurl, xpaths.preview, _v, xtoken())));
        }
        if(v) _fv(v);

        xchange(_d, function(evt){
            let fi = evt.target.files[0];
            if(!fi) return;//cancel
            let fd = new FormData();
            fd.append('file', fi);
            $.ajax({
                url: '{0}/{1}'.format(xurl, xpaths.upload),
                type: 'post',
                headers: {"x-token": xtoken()},
                data: fd,
                dataType: 'json',
                contentType: false,
                processData: false,
                success: function(resp){_fv(resp.data);}
            });
        });
    });
setFuncTo(dlgDataFuncs, [xTypes._file, xTypes._imag],
    function(k, c){
        return fileLabelDom(dlgInputDom(k, c.key).attr('id')).html();
    });


//nested
setFuncTo(dlgHtmlFuncs, [xTypes._model],
    function(id, c) {
        let nestedHtml = `<div id="dinput_{0}_{1}" class="border-left border-bottom text-sm"></div>`;
        return nestedHtml.format(id, c.key);
    });
setFuncTo(dlgMakeFuncs, [xTypes._model],
    function(_d, c, v) {
        _d.empty();
        let nid = _d.attr('id');
        for(let col of c.columns) {
            addDlgInput(_d, col, nid, xvalueByKey(v, col.key));
        }
    });
setFuncTo(dlgDataFuncs, [xTypes._model],
    function(k, c) {
        let nid = dlgInputId(k, c.key);
        let obj = {};
        for(let col of c.columns) {
            let val = dlgInputVal(nid, col);
            if(val) obj[col.key] = val;
        }
        return obj;
    });
//nested
setFuncTo(dlgHtmlFuncs, [xTypes._list],
    function(id, c) {
        let nestedHtm = `<button id="dinput_{0}_{1}" type="button" style="border: dashed 1px #dee2e6;" class="form-group form-control">+</button>`;
        return nestedHtm.format(id, c.key);
    });
let _idxCache={};
setFuncTo(dlgMakeFuncs, [xTypes._list],
    function(_d, c, v) {
        //make empty list element
        let makeElement = function(nid, _id, aplFunc, _v) {
            let _eid = '{0}_{1}'.format(nid, _id);
            let _e = $(`<div id="{0}" class="border-left border-bottom position-relative form-group text-sm">`.format(_eid));
            aplFunc(_e);
            for(let col of c.columns) {
                addDlgInput(_e, col, _eid, xvalueByKey(_v, col.key));
            }
            //minus btn
            let minusBtn = $(`<button type="button" class="position-absolute close" style="right:.5rem;bottom:.25rem;"><i class="fas fa-minus-circle fa-xs"></i></button>`);
            xclick(minusBtn, ()=>minusBtn.parent().remove());
            _e.append(minusBtn);
            return _e;
        };
        let nid = _d.attr('id');
        xclick(_d, ()=>makeElement(nid, (++_idxCache[nid]), (_x)=>_d.before(_x)));
        _idxCache[nid] = 0;
        if(!v) return
        let _l;
        for(let _v of v) {
            let aplFunc = _l ? (_x)=>_l.after(_x) : (_x)=>_d.before(_x);
            let _e = makeElement(nid, _v._id, aplFunc, _v);
            _l = _e;
            _idxCache[nid] = _v._id;
        }
    });
setFuncTo(dlgDataFuncs, [xTypes._list],
    function(k, c) {
        let nid = dlgInputId(k, c.key);
        let max = _idxCache[nid];
        let dat = [];
        for (let _lid = 1; _lid <= max; ++_lid) {
            let _eid = '{0}_{1}'.format(nid, _lid);
            let _e = $('#{0}'.format(_eid));
            if(_e.length && _e.length>0) {
                let obj = {};
                for(let col of c.columns) {
                    let val = dlgInputVal(_eid, col);
                    if(val) obj[col.key] = val;
                }
                if(Object.keys(obj).length > 0) dat.push(obj);
            }
        }
        return dat;
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

function getDialogFormObj(dlg) {
    var obj = {};
    for(let column of dlg.columns) {
        obj[column.key] = dlgInputVal(dlg.ident, column)
    }
    return obj;
}

function packFlxParams(flxOp, valFunc) {
    let flxParams = {}
    for(let c of flxOp.inputs) {
        let v = valFunc(c);
        if(!v) return;
        flxParams[c.key] = v;
    }
    return flxParams;
}

function showDialogForm(parent, dlg, op, model, flxOp) {
    dlg.columns = dlg.opColumns(op);
    if(model && flxOp) {//已有数据 先请求columns
        let flxParams = packFlxParams(flxOp, _c=>model[_c.key]);
        if(flxParams) showDialogForm0Flx(parent, dlg, op, model, flxOp, flxParams);
    }
    showDialogForm0(parent, dlg, op, model, flxOp);
}

function showDialogForm0Flx(parent, dlg, op, model, flxOp, flxParams) {
    doGet('{0}?{1}'.format(dlg.segpath.urljoin(flxOp.path), ($.param(flxParams))), function(resp){
        if(resp) {//显示结构发生变化
            dlg.columns = resp.columns;
            dlg.flexName = resp.flexName;
            dlg.flex(resp);//flx pass
            showDialogForm0(parent, dlg, op, model, flxOp);  
        } else {
            showDialogForm0(parent, dlg, op, model, flxOp);
        }
    });
}

function showDialogForm0(parent, dlg, op, model, flxOp) {
    let flxChange = function() {
        let flxParams = packFlxParams(flxOp, _c=>dlgInputVal(dlg.ident, _c));
        if(flxParams) showDialogForm0Flx(parent, dlg, op, getDialogFormObj(dlg), flxOp, flxParams);
    };
    let inFlxCols = function(col) {
        if(flxOp) 
            for(let c of flxOp.inputs)
                if(c.key == col.key) return true;
        return false;
    };
    parent.empty();
    for(let column of dlg.columns){
        if(op.type == opTypes.add && !xcolumn.add(column)) continue;
        if(op.type >= opTypes.edt && !xcolumn.edel(column)) continue;
        let val = dialogInputVal(model, column.key);
        addDlgInput(parent, column, dlg.ident, val);
        if (op.type == opTypes.del || (model && !xcolumn.edit(column))) {
            dlgInputDom(dlg.ident, column.key).attr("disabled", true);
        }
        if(inFlxCols(column)) xchange(dlgInputDom(dlg.ident, column.key), flxChange);
    }
}

function showDialog(dlg, op, model, func, flxOp) {
    $('#xdialog_title').empty();
    $('#xdialog_title').append(dialogTitle(dlg, op))
    showDialogForm($('#xdialog_form'), dlg, op, model, flxOp);
    xclick($('#xdialog_submit'), function(){submitDialog(dlg, op, func);})
    modalShow();
}

function submitDialog(dlg, op, func) {
    doPost(dlg.segpath.urljoin(op.path), op, getDialogFormObj(dlg),
        function(resp) {
            modalHide();
            func(resp);
        }, {'flex-name': dlg.flexName});
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

