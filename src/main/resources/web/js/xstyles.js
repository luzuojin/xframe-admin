//input types
var xTypes = {
    _text: 0,
    _bool: 1,
    _enum: 2,
    _time: 3,
    _area: 4,
    _pass: 9,
    _mult: 20,//multi enum select
}

var x = {
user: `<i class="fas fa-user"></i> {0}`,
userhtm: function(name){return this.user.format(name);},
// style="background-color:rgba(255,255,255,.05) !important;"
chapter: `
        <li class="nav-item has-treeview">
            <a class="nav-link" href="javascript:void(0);">
              <i class="nav-icon fab fa-gg"/>
              <p>
                {0}
                <i class="right fas fa-angle-left"/>
              </p>
            </a>
            <ul id="chapter_{1}" class="nav nav-treeview"/>
        </li>
        `,
chapterhtm: function(chapter){return this.chapter.format(chapter.name, chapter.path)},
//chapter or segment
chapterdom: function(data){return $('#chapter_{0}'.format(data.spath?data.spath : data.path));},

segment: `
        <li class="nav-item">
            <a id="seg_{0}_{1}" class="nav-link" href="javascript:void(0);">
              <i class="fas fa-paperclip"/>
              <p>{2}</p>
            </a>
        </li>
        `,
segmenthtm: function(segment){return this.segment.format(segment.spath, segment.path, segment.name);},
segmentdom: function(segment){return $('#seg_{0}_{1}'.format(segment.spath, segment.path));},

tabletd: `<td id='xtd_{0}_{1}'>{2}</td>`,
tabletr: `<tr id='xtr_{0}'/>`,
tabletdDom: function(tr, td){return $('#xtd_{0}_{1}'.format(tr, td))},
tabletrDom: function(tr){return $('#xtr_{0}'.format(tr))},

delBtn: `<button id="delbtn_{0}_{1}" type="button" class="btn-sm btn-outline-danger">{2}</button>`,
edtBtn: `<button id="edtbtn_{0}_{1}" type="button" class="btn-sm btn-outline-info" style="margin-right:5px">{2}</button>`,
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
        this.select2(this.qryInputDom(xinput.key), xinput);
    } else {
        parent.append(this.qryText.format(xinput.key, xinput.hint));
        if(xinput.type==xTypes._time) this.datepicker(this.qryInputDom(xinput.key));
    }
},

dlgColumn: `
        <div class="form-group row">
          <label class="col-sm-2 col-form-label" for="dinput_{0}_{1}"><p class="float-right">{2}</p></label>
          <div class="col-sm-10">{3}</div>
        </div>
        `,
dlgText:`<input id="dinput_{0}_{1}" class="form-control" placeholder="{2}" value="{3}" type="{4}">`,
dlgEnum:`<select id="dinput_{0}_{1}" class="form-control select2" data-placeholder="{2}" style="width:100%"></select>`,
dlgBool:`
        <div class="form-control custom-control custom-switch custom-switch-on-primary">
          <input id="dinput_{0}_{1}" type="checkbox" class="custom-control-input" value="false">
          <label class="custom-control-label" for="dinput_{0}_{1}" style="margin-left:7.5px;"/>
        </div>
        `,
dlgInputDom: function(idkey, inputkey){return $('#dinput_{0}_{1}'.format(idkey, inputkey));},

addDlgInput: function(parent, xinput, idkey, value) {
    if(xinput.type==xTypes._enum || xinput.type==xTypes._mult) {
        let _col = this.dlgEnum.format(idkey, xinput.key, xinput.hint);
        parent.append(this.dlgColumn.format(idkey, xinput.key, xinput.hint, _col));
        this.select2(this.dlgInputDom(idkey, xinput.key), xinput);
        if(value) this.dlgInputDom(idkey, xinput.key).val(value).trigger('change');
    } else if(xinput.type==xTypes._bool) {
        let _col = this.dlgBool.format(idkey, xinput.key);
        parent.append(this.dlgColumn.format(idkey, xinput.key, xinput.hint, _col));
        let ckbox = this.dlgInputDom(idkey, xinput.key);
        ckbox.change(function (){ckbox.val(this.checked);});
        if(value) ckbox.attr('checked', value);
    } else {
        let _type = (xinput.type==xTypes._pass) ? 'password' : 'text';
        let _col = this.dlgText.format(idkey, xinput.key, xinput.hint, value, _type);
        parent.append(this.dlgColumn.format(idkey, xinput.key, xinput.hint, _col));
        if(xinput.type==xTypes._time) this.datepicker(this.dlgInputDom(idkey, xinput.key));
    }
},

datepicker: function(e) {
    e.datepicker({
        format: "yyyy-mm-dd",
        autoclose: true,
        todayHighlight: true,
        isRTL: false,
        language: "zh-CN"
    });
},

select2: function(e, xinput) {
    let d = xenum(xinput.enumKey);
    e.select2({
        theme: 'bootstrap4',
        dropdownAutoWidth : true,
        width: 'auto',
        data: d,
        multiple: xinput.type==xTypes._mult,
        minimumResultsForSearch: 10
    });
},

};