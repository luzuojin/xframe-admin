function getOption(detail, opType) {
    for(let op of detail.options) {
        if(op.type ===  opType) return op;
    }
}
function showDetail(detail) {
    let dtypes = [undefined, xtd, xpd]//xtd:1, xpd:2
    let det = dtypes[detail.type];
    let ini = getOption(detail, opTypes.ini);
    if(ini) {//loading ini data
        doGet(detail.segpath.urljoin(ini.path), function(data){
            det.showDetailInternal(detail, data);
        });
    } else {
        det.showDetailInternal(detail);
    }
}

function opIdent(detail, op) {
    return op.path ? detail.path + '_' + op.path : detail.path;
}


var xmodel = {
    datas: [],
    pkeys: [],
    set: function(detail, datas) {
        let pkeys = []
        for(c of detail.columns) {
            if(c.primary) pkeys.push(c.key);
        }
        this.pkeys = pkeys;
        this.datas = datas;
    },
    eq: function(idx, d2) {
        let d1 = this.datas[idx];
        for(key of this.pkeys) {
            if(d1[key] != d2[key]) return false;
        }
        return this.pkeys.length > 0;
    },
    add: function(data) {
        this.datas.push(data);
    },
    del: function(data) {
        for(idx in this.datas) {
            if(this.eq(idx, data)) this.datas.splice(idx, 1);
        }
    },
    edt: function(data) {
        for(idx in this.datas) {
            if(this.eq(idx, data)) this.datas[idx] = data;
        }
    }
}
var getValFromModel = function(model, column) {
    let val = model[column.key];
    if(column.type == xTypes._enum || column.type == xTypes._mult) {
        return xenumText(column.enumKey, val);
    }
    return xvalue(val);
}

//table detail
var xtd = {
    tabletd: `<td id='xtd_{0}_{1}'>{2}</td>`,
    tabletr: `<tr id='xtr_{0}'/>`,

    delBtn: `<button id="delbtn_{0}_{1}" type="button" class="btn btn-sm btn-outline-danger">{2}</button>`,
    edtBtn: `<button id="edtbtn_{0}_{1}" type="button" class="btn btn-sm btn-outline-info" style="margin-right:5px">{2}</button>`,
    qryBtn: `<button id="qrybtn_{0}_{1}" type="button" class="btn bg-gradient-info float-left" style="margin-left:7.5px;margin-right:7.5px;">{2}</button>`,
    addBtn: `<button id="addbtn_{0}_{1}" type="button" class="btn bg-gradient-success float-right" style="margin-left:7.5px;margin-right:7.5px;">{2}</button>`,
    delBtnDom: function(path, tr){return $('#delbtn_{0}_{1}'.format(path, tr))},
    edtBtnDom: function(path, tr){return $('#edtbtn_{0}_{1}'.format(path, tr))},
    qryBtnDom: function(path, tr){return $('#qrybtn_{0}_{1}'.format(path, tr))},
    addBtnDom: function(path, tr){return $('#addbtn_{0}_{1}'.format(path, tr))},

    qryInputDom: function(k){return $('#xqry_{0}'.format(k))},
    qryText: `<div class="col-sm-2 float-left"><input id="xqry_{0}" class="form-control" type="text" placeholder="{1}" autocomplete="off"></div>`,
    qryEnum: `<div class="col-sm-2 float-left"><select id="xqry_{0}" class="form-control select2bs4" data-placeholder="{1}" style="width:100%"><option/></select></div>`,

    addQryInput: function(parent, xinput) {
        if(xinput.type == xTypes._enum) {
            parent.append(this.qryEnum.format(xinput.key, xinput.hint));
            xselect2(this.qryInputDom(xinput.key), xinput);
        } else {
            parent.append(this.qryText.format(xinput.key, xinput.hint));
            if(xinput.type==xTypes._datetime) xdatepicker(this.qryInputDom(xinput.key));
            if(xinput.type==xTypes._date) xdatepicker(this.qryInputDom(xinput.key), xformatDate);
            if(xinput.type==xTypes._date) xdatepicker(this.qryInputDom(xinput.key), xformatTime);
        }
    },
    queryDatasFunc: function(detail, op) {
        let that = this;
        return function() {
            let params = {};
            for(let input of op.inputs) {
                params[input.key] = that.qryInputDom(input.key).val();
            }
            doGet('{0}?{1}'.format(detail.segpath, ($.param(params))), function(data){that.showDetailBody(detail, data);});
        };
    },
    _ops: false,
    showDetailInternal: function(detail, data=undefined) {
        if(!data) data = [];
        xmodel.set(detail, data);

        $('#xboxhead').empty();
        $('#xboxbody').empty();

        _ops = false;
        let _tr = 0;
        //box head
        for(let op of detail.options) {
            let ident = opIdent(detail, op);
            if(op.type == opTypes.qry) {//only query use inputs
                for(let input of op.inputs) {
                    this.addQryInput($('#xboxhead'), input);
                }
                $('#xboxhead').append(this.qryBtn.format(ident, _tr, op.name));
                xclick(this.qryBtnDom(ident, _tr), this.queryDatasFunc(detail, op));
                detail.qryOp = op;
            }
            if(op.type == opTypes.add) {
                $('#xboxhead').append(this.addBtn.format(ident, _tr, op.name));
                xclick(this.addBtnDom(ident, _tr), showDialogFunc(detail, op, this.queryInputsToModelFunc(), this.showDetailBodyFunc()));
            }
            if(op.type == opTypes.edt) _ops = true;
            if(op.type == opTypes.del) _ops = true;
        }
        

        let tablehtm = `
                    <table id="xtable" class="table table-bordered table-hover">
                        <thead id="xthead"></thead>
                        <tbody id="xtbody"></tbody>
                    </table>
                    `;
        $('#xboxbody').append($(tablehtm));

        //table head
        this.showTableHead($('#xthead'), detail.columns, _tr, _ops);
        //table body
        this.showDetailBody(detail, xmodel.datas);
    },
    showTableHead: function(parent, columns, _tr, __ops=false) {
        let _tabletr = $(this.tabletr.format(_tr));
        parent.append(_tabletr);
        for(let column of columns){
            if(xcolumn.list(column)) {
                _tabletr.append($(this.tabletd.format(_tr, 0, column.hint)));
            }
        }
        if(__ops) {//options td head
            _tabletr.append($(this.tabletd.format(_tr, 0, "Options")));    
        }
    },
    showDetailBodyFunc:function() {
        return this.showDetailBody.bind(this);
    },
    showDetailBody: function(detail, data) {
        this.showTableBody($('#xtbody'), detail.columns, data, _ops, detail)
    },
    showTableBody:function(parent, columns, data, _ops, detail) {
        parent.empty();

        let _tr = 0;
        //table body
        for(let model of data) {
            model._id = (++ _tr);
            let _tabletr = $(this.tabletr.format(_tr));
            parent.append(_tabletr)
            var _td = 0;
            for(let column of columns){
                if(xcolumn.list(column)) {
                    if(column.type == xTypes._model || column.type == xTypes._list) {
                        let _tabletd = $(this.tabletd.format(_tr, (++_td), ''));
                        let _ntable = $(`<table class="table table-bordered table-hover table-sm text-sm mb-0 "></table>`);
                        let _nthead = $(`<thead></thead>`);
                        let _ntbody = $(`<tbody></tbody>`);

                        _tabletr.append(_tabletd);
                        _tabletd.append(_ntable);
                        _ntable.append(_nthead);
                        _ntable.append(_ntbody);
                        
                        this.showTableHead(_nthead, column.columns, _tr);
                        let val = getValFromModel(model, column);
                        this.showTableBody(_ntbody, column.columns, column.type==xTypes._model?[val]:val)
                    } else {
                        _tabletr.append($(this.tabletd.format(_tr, (++_td), getValFromModel(model, column))));
                    }
                }
            }
            //options td
            if(_ops) {
                let _tabletd = $(this.tabletd.format(_tr, (++_td), ''));
                _tabletr.append(_tabletd);
                for(let op of detail.options) {
                    let ident = opIdent(detail, op);
                    if(op.type == opTypes.edt) {
                        _tabletd.append(this.edtBtn.format(ident, _tr, op.name));
                        xclick(this.edtBtnDom(ident, _tr), showDialogFunc(detail, op, model, this.showDetailBodyFunc()));
                    }
                    if(op.type == opTypes.del) {
                        _tabletd.append(this.delBtn.format(ident, _tr, op.name));
                        xclick(this.delBtnDom(ident, _tr), showDialogFunc(detail, op, model, this.showDetailBodyFunc()));
                    }
                }
            }
        }
    },
    queryInputsToModelFunc:function() {
        return this.queryInputsToModel.bind(this);
    },
    queryInputsToModel: function(detail) {
        if(detail.padding) {
            var obj = {};
            for(let t of detail.qryOp.inputs) {
               obj[t.key] = this.qryInputDom(t.key).val();
            }
            return obj;
        }
    }
};

//panel detail
var xpd = {//重用dialog相关element
    descHtm: `<div class="col-sm-8 m-auto h-100 h5">{0}</div>`,
    panelhtm: `<form id="xpanel_form" class="form-horizontal"/>`,
    btnRow: `<div id="xpanel_btnrow" class="form-group row"></div>`,

    edtBtn: `<div class="col-sm-2 m-auto"><button id="xpanel_edtbtn_{0}" type="button" class="btn btn-block bg-info">{1}</button></div>`,
    delBtn: `<div class="col-sm-2 m-auto"><button id="xpanel_delbtn_{0}" type="button" class="btn btn-block bg-danger">{1}</button></div>`,
    edtBtnDom: function(path){return $('#xpanel_edtbtn_{0}'.format(path))},
    delBtnDom: function(path){return $('#xpanel_delbtn_{0}'.format(path))},

    showDetailInternal: function(detail, data) {
        if(!data) data = {};
        //empty ex
        $('#xboxhead').empty();
        $('#xboxbody').empty();
        //desc
        $('#xboxhead').append(this.descHtm.format(detail.desc));
        //body form
        $('#xboxbody').append($(this.panelhtm));
        //add to body form use methods from dialog
        let dlg = detailToDlg(detail, true);
        showDialogForm($('#xpanel_form'), dlg, {}, data, getOption(detail, opTypes.flx));
        //add button row
        $('#xboxbody').append($(this.btnRow));
        for(let op of detail.options) {
            if(op.type == opTypes.del) {
                $('#xpanel_btnrow').append($(this.delBtn.format(op.path, op.name)));
                xclick(this.delBtnDom(op.path), this.submitPanelFunc(detail, dlg, op));
            } else if(op.type == opTypes.edt) {
                $('#xpanel_btnrow').append($(this.edtBtn.format(op.path, op.name)));
                xclick(this.edtBtnDom(op.path), this.submitPanelFunc(detail, dlg, op));
            }
        }
    },
    submitPanelFunc: function(detail, dlg, op) {
        let that = this;
        return function() {
            that.submitPanel(detail, dlg, op, function(resp){
                that.showDetailInternal(detail, resp);
            });
        }
    },
    submitPanel: function(detail, dlg, op, func) {
        doPost(dlg.segpath.urljoin(op.path), op, getDialogFormObj(dlg), func, {'flex-name': detail.flexName});
    }
};

function detailToDlg(detail, flxPass=false) {
    return {
        ident: detail.path,
        segname: detail.segname,
        segpath: detail.segpath,
        opColumns: function (_op) {
            return (_op.inputs && _op.inputs.length > 0) ? _op.inputs : detail.columns;
        },
        flex: function(flx) {//有flxOp时 调用
            if(flxPass) {
                Object.assign(detail.columns, flx.columns, {length:flx.columns.length});
                detail.flexName = flx.flexName;
            }
        }
    }
}

function showDialogFunc(detail, op, model, refreshDetail) {//model or supplier function
    return function() {
        let _model = ('function'===typeof(model)) ? model(detail) : model;
        showDialog(detailToDlg(detail), op, _model, function(data){
            if(op.type == opTypes.qry || Array.isArray(data)) {
                xmodel.set(detail, data);
            }else{
                if(op.type == opTypes.add) xmodel.add(data);
                if(op.type == opTypes.edt) xmodel.edt(data);
                if(op.type == opTypes.del) xmodel.del(data);
            }
            refreshDetail(detail, xmodel.datas);
        }, getOption(detail, opTypes.flx));
    };
}

