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
        return true;
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
    return val;
}

//table detail
var xtd = {
    tabletd: `<td id='xtd_{0}_{1}'>{2}</td>`,
    tabletr: `<tr id='xtr_{0}'/>`,
    tabletdDom: function(tr, td){return $('#xtd_{0}_{1}'.format(tr, td))},
    tabletrDom: function(tr){return $('#xtr_{0}'.format(tr))},

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

        $('#xboxhead').empty();
        $('#xboxbody').empty();

        let tablehtm = `
                    <table id="xtable" class="table table-bordered table-hover">
                        <thead id="xthead"></thead>
                        <tbody id="xtbody"></tbody>
                    </table>
                    `;
        $('#xboxbody').append($(tablehtm));

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
        
        //table head
        $('#xthead').append($(this.tabletr.format(_tr)))
        for(let column of detail.columns){
            if(xcolumn.list(column)) {
                this.tabletrDom(_tr).append($(this.tabletd.format(_tr, 0, column.hint)));
            }
        }
        if(_ops) {//options td head
            this.tabletrDom(_tr).append($(this.tabletd.format(_tr, 0, "Options")));    
        }

        xmodel.set(detail, data);
        //table body
        this.showDetailBody(detail, xmodel.datas);
    },
    showDetailBodyFunc:function() {
        return this.showDetailBody.bind(this);
    },
    showDetailBody: function(detail, data) {
        $('#xtbody').empty();

        let _tr = 0;
        //table body
        for(let model of data) {
            model._id = (++ _tr);
            $('#xtbody').append($(this.tabletr.format(_tr)))
            var _td = 0;
            for(let column of detail.columns){
                if(xcolumn.list(column)) {
                    this.tabletrDom(_tr).append($(this.tabletd.format(_tr, (++_td), getValFromModel(model, column))));
                }
            }
            //options td
            if(_ops) {
                this.tabletrDom(_tr).append($(this.tabletd.format(_tr, (++_td), '')));
                for(let op of detail.options) {
                    let ident = opIdent(detail, op);
                    if(op.type == opTypes.edt) {
                        this.tabletdDom(_tr, _td).append(this.edtBtn.format(ident, _tr, op.name));
                        xclick(this.edtBtnDom(ident, _tr), showDialogFunc(detail, op, model, this.showDetailBodyFunc()));
                    }
                    if(op.type == opTypes.del) {
                        this.tabletdDom(_tr, _td).append(this.delBtn.format(ident, _tr, op.name));
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
    edtBtn: `<div class="col-sm-2 m-auto"><button id="xpanel_edtbtn_{0}" type="button" class="btn btn-block bg-info">{1}</button></div>`,
    delBtn: `<div class="col-sm-2 m-auto"><button id="xpanel_delbtn_{0}" type="button" class="btn btn-block bg-danger">{1}</button></div>`,
    edtBtnDom: function(path){return $('#xpanel_edtbtn_{0}'.format(path))},
    delBtnDom: function(path){return $('#xpanel_delbtn_{0}'.format(path))},
    btnRow: `<div id="xpanel_btnrow" class="form-group row"></div>`,

    showDetailInternal: function(detail, data) {
        if(!data) data = {};
        this.qryOp = getOption(detail, opTypes.qry);
        //empty ex
        $('#xboxhead').empty();
        $('#xboxbody').empty();
        //desc
        $('#xboxhead').append('<div class="col-sm-8 m-auto"><h4>{0}</h4></div>'.format(detail.desc));
        //body form
        let panelhtm = `<form id="xpanel_form" class="form-horizontal"/>`;
        $('#xboxbody').append($(panelhtm));
        //add to body form use methods from dialog
        let dlgIdent = 'xpanel';
        for(let column of detail.columns) {
            let val = dialogInputVal(data, column.key);
            addDlgInput($('#xpanel_form'), column, dlgIdent, val);
            let dom = dlgInputDom(dlgIdent, column.key);
            if (data && !xcolumn.edit(column)) {
                dom.attr("disabled", true);
            }
            if(this.inQryOpInputs(column)) {
                xchange(dom, this.onColumnChangeFunc(detail, column, dom));
            }
        }
        //add button row
        $('#xboxbody').append($(this.btnRow));
        for(let op of detail.options) {
            if(op.type == opTypes.del) {
                $('#xpanel_btnrow').append($(this.delBtn.format(op.path, op.name)));
                xclick(this.delBtnDom(op.path), this.submitPanelFunc(detail, op));
            } else if(op.type == opTypes.edt) {
                $('#xpanel_btnrow').append($(this.edtBtn.format(op.path, op.name)));
                xclick(this.edtBtnDom(op.path), this.submitPanelFunc(detail, op));
            }
        }
    },
    qryOp: undefined,
    inQryOpInputs: function(column) {
        if(this.qryOp) {
            for(let c of this.qryOp.inputs) {
                if(c.key === column.key) return true;
            }
        }
        return false;
    },
    qryColumnVals: {},
    onColumnChangeFunc: function(detail, column, dom) {
        let that = this;
        return function(e) {
            that.qryColumnVals[column.key] = dom.val();
            if(that.qryOp) {
                for(let c of that.qryOp.inputs) {
                    if(!that.qryColumnVals[c.key]) return;
                }
                doGet('{0}?{1}'.format(detail.segpath, ($.param(that.qryColumnVals))), function(data){
                    if(data.columns) {//显示结构发生变化
                        detail.columns = data.columns;
                        that.showDetailInternal(detail, data.internal);    
                    } else {
                        that.showDetailInternal(detail, data);
                    }
                });
            }
        }
    },
    submitPanelFunc: function(detail, op) {
        let that = this;
        return function() {
            that.submitPanel(detail, op, function(resp){
                that.showDetailInternal(detail, resp);
            });
        }
    },
    submitPanel: function(detail, op, func) {
        let dlgIdent = 'xpanel';
        var model = {};
        for(let column of detail.columns) {
            let key = column.key;
            model[key] = dlgInputDom(dlgIdent, key).val();
            if(column.type==xTypes._pass) model[key]=$.md5(model[key]);
        }
        doPost(detail.segpath.urljoin(op.path), op, model, func);
    }
};

function detailToDlg(detail) {
    return {
        ident: detail.path,
        segname: detail.segname,
        segpath: detail.segpath,
        opColumns: function (_op) {
            return (_op.inputs && _op.inputs.length > 0) ? _op.inputs : detail.columns;
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
        });
    };
}

