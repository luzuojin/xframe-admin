var xmodel = {
    datas: [],
    pkeys: [],
    set: function(seg, datas) {
        let pkeys = []
        for(c of seg.columns) {
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

var x = {
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
        if(xinput.type==xTypes._time) xdatepicker(this.qryInputDom(xinput.key));
    }
},
};

function queryDatasFunc(segment, op) {
    return function() {
        let params = {};
        for(let input of op.inputs) {
            params[input.key] = x.qryInputDom(input.key).val();
        }
        doGet('{0}?{1}'.format(segpath(segment), ($.param(params))), function(data){showDetailBody(segment, data);});
    }
}

var xlatestOp;
var _op;
function showDetail(segment, data) {
    $('#xthead').empty();
    $('#xboxhead').empty();

    _op = false;

    let _tr = 0;
    //box head
    for(let op of segment.options) {
        if(op.opType == opTypes.qry) {//only query use inputs
            for(let input of op.inputs) {
                x.addQryInput($('#xboxhead'), input);
            }
            $('#xboxhead').append(x.qryBtn.format(segment.path, _tr, op.name));
            xclick(x.qryBtnDom(segment.path, _tr), queryDatasFunc(segment, op));
            segment.qryOp = op;
        }
        if(op.opType == opTypes.add) {
            $('#xboxhead').append(x.addBtn.format(segment.path, _tr, op.name));
            xclick(x.addBtnDom(segment.path, _tr), showDialogFunc(segment, op, queryInputsToModel));
        }
        if(op.opType == opTypes.edt) _op = true;
        if(op.opType == opTypes.del) _op = true;
    }

    //box body --> table
    //table head
    $('#xthead').append($(x.tabletr.format(_tr)))
    for(let column of segment.columns){
        if(xcolumn.list(column)) {
            x.tabletrDom(_tr).append($(x.tabletd.format(_tr, 0, column.hint)));
        }
    }
    if(_op) {//options td head
        x.tabletrDom(_tr).append($(x.tabletd.format(_tr, 0, "Options")));    
    }

    xmodel.set(segment, data);
    //table body
    showDetailBody(segment, xmodel.datas);
}

function showDetailBody(segment, data) {
    $('#xtbody').empty();

    let _tr = 0;
    //table body
    for(let model of data) {
        model._id = (++ _tr);
        $('#xtbody').append($(x.tabletr.format(_tr)))
        var _td = 0;
        for(let column of segment.columns){
            if(xcolumn.list(column)) {
                x.tabletrDom(_tr).append($(x.tabletd.format(_tr, (++_td), model[column.key])));
            }
        }
        //options td
        if(_op) {
            x.tabletrDom(_tr).append($(x.tabletd.format(_tr, (++_td), '')));
            for(let op of segment.options) {
                if(op.opType == opTypes.edt) {
                    x.tabletdDom(_tr, _td).append(x.edtBtn.format(segment.path, _tr, op.name));
                    xclick(x.edtBtnDom(segment.path, _tr), showDialogFunc(segment, op, model));
                }
                if(op.opType == opTypes.del) {
                    x.tabletdDom(_tr, _td).append(x.delBtn.format(segment.path, _tr, op.name));
                    xclick(x.delBtnDom(segment.path, _tr), showDialogFunc(segment, op, model));
                }
            }
        }
    }
}

function queryInputsToModel(seg) {
    var obj = {};
    if(seg.padding) {
        for(let t of seg.qryOp.inputs) {
           obj[t.key] = x.qryInputDom(t.key).val();
        }
    }
    return obj;
}

function showDialogFunc(seg, op, model) {//model or supplier function
    return function() {
        let _model = ('function'===typeof(model)) ? model(seg) : model;
        showDialog(seg, op, _model, function(data){
            if(op.opType == opTypes.qry || Array.isArray(data)) {
                xmodel.set(segment, data);
            }else{
                if(op.opType == opTypes.add) xmodel.add(data);
                if(op.opType == opTypes.edt) xmodel.edt(data);
                if(op.opType == opTypes.del) xmodel.del(data);
            }
            showDetailBody(segment, xmodel.datas);
        });
    };
}

