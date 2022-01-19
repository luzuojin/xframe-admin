class Node {
    parent;
    children = [];
    constructor(parent) {
        this.parent = parent;
    }
    append(node) {
        this.children.push(node);
    }
    //显示
    show() {}
    dom() {}
    appendHtm(htm) {}
}

class Navi extends Node {
    name;
    path;
    constructor(parent, name, path) {
        super(parent);
        this.name = name;
        this.path = path;
    }
    mergeFrom(navi) {
        this.name = navi.name;
        this.path = navi.path;
        return this;
    }
    uri() {
        return this.parent ? (this.parent.uri() + "/" + this.path) : this.path;
    }
    pid() {//path ident
        return (this.parent && this.parent.pid) ? (this.parent.pid() + "_" + this.path) : this.path;
    }
}

class Chapter extends Navi {
    constructor(name, path) {
        super(null, name, path);
    }
    static of(jChapter) {
        let c = new Chapter(jChapter.name, jChapter.path);
        if(jChapter.navis) {
            //仅有一个segment且path={x}, 不展示tab, 合并navis~segments
            if(jChapter.segments.length == 1 && jChapter.segments[0].path.startsWith("{")) {
                let jSegment = jChapter.segments[0];  //wildcard segment
                for(let jNavi of jChapter.navis) {
                    c.append(Segment.of(c, jSegment).mergeFrom(jNavi));
                }
            } else {//展示tab
                for(let jNavi of jChapter.navis) {
                    let tab = Tab.of(c, jNavi);
                    for(let jSegment of jChapter.segments) {
                        tab.append(Segment.of(tab, jSegment));
                    }
                    c.append(tab);
                }
            }
        } else {
            for(let jSegment of jChapter.segments){//二级菜单
                c.append(Segment.of(c, jSegment));
            }
        }
        return c
    }
    dom() {
        return $('#vchapter_{0}'.format(this.pid()));
    }
    show() {
        let htm = `<li class="nav-item has-treeview">
                    <a class="nav-link" href="javascript:void(0);">
                      <i class="nav-icon fab fa-gg"/>
                      <p>
                        {0}
                        <i class="right fas fa-angle-left"/>
                      </p>
                    </a>
                    <ul id="vchapter_{1}" class="nav nav-treeview"/>
                </li>
                `.format(this.name, this.pid());
        $('#xsiderbar').append(htm);
        this.children.forEach(child=>child.show());
    }
}

class Segment extends Navi {
    constructor(parent, name, path) {
        super(parent, name, path);
    }
    static of(parent, jSegment) {
        let seg = new Segment(parent, jSegment.name, jSegment.path);
        seg.append(Detail.of(seg, jSegment.detail));
        return seg;
    }
    dom() {
        return $('#vseg_{0}'.format(this.pid()));
    }
    show() {
        let htm =`
                <li class="nav-item">
                    <a id="vseg_{0}" class="nav-link" href="javascript:void(0);">
                      <i class="fas fa-paperclip"/>
                      <p>{1}</p>
                    </a>
                </li>
                `.format(this.pid(), this.name);
        this.parent.dom().append(htm);
        xclick(this.dom(), ()=>this.showContent())
    }
    showContent() {
        this.initContainer();
        this.children[0].show();    //detail.show
    }
    initContainer() {
        $('#xcontent').empty();
        let htm = `
                    <div class="card-header">
                      <div class="row">
                        <div id="xboxhead" class="clearfix w-100"></div>
                      </div>
                    </div>
                    <div id="xboxbody" class="card-body">
                    </div>
                    `;
        $('#xcontent').append(htm);
    }
}

var latestTabSeg;
class Tab extends Segment {
    constructor(parent, name, path) {
        super(parent, name, path);
    }
    static of(parent, jNavi) {
        return new Tab(parent, jNavi.name, jNavi.path);
    }
    showContent() {
        this.initContainer();
        this.showTabContent(latestTabSeg ? latestTabSeg : this.children[0]);
    }
    initContainer() {
        super.initContainer();
        //tab container
        $('#xcontent').prepand(`<div class="card-header p-0 border-bottom-0"><ul id="xtabContainer" class="nav nav-tabs" role="tablist"></ul></div>`);

        for(let seg of this.children) {
            $('#xtabContainer').append(`<li class="nav-item"><a id="vsegtab_{0}" class="nav-link text-dark" data-toggle="pill" href="javascript:void(0);" role="tab" aria-selected="false">{1}</a></li>`.format(seg.pid(), seg.name));
            //onClick show content
            xclick($("#vsegtab_{0}".format(seg.pid())), ()=>this.showTabContent(seg));
        }
    }
    showTabContent(seg) {
        latestTagSeg = seg;
        seg.children[0].show();//detail.show
    }
}

class Detail extends Node {
    type;
    padding;
    options;
    constructor(parent) {
        super(parent);
    }
    static of(parent, jDetail) {
        let d = new Detail(parent);
        d.type = jDetail.type;
        d.padding = jDetail.padding;
        d.options = jDetail.options;
        d.columns = jDetail.columns;
        for(let jColumn of jDetail.columns) {
            d.append(Column.of(d, jColumn));
        }
        return d;
    }
    uri() {
        return this.parent.uri();
    }
    pid() {//path ident
        return this.parent.pid();
    }
    getOption(opType) {
        return this.options.find(e=>e.type==opTYpe);
    }
    show() {
        this.segpath = this.parent.uri();
        showDetail(this);
    }
}

class Column {
    parent;
    constructor(parent) {
        this.parent = parent;
    }
    static of(parent, jColumn) {
        let c = new Column(parent);
        Object.assign(c, jColumn);
        return c;
    }
}



