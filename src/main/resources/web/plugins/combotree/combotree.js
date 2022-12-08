/*!
 * jQuery ComboTree Plugin
 * Author:  Erhan FIRAT
 * Mail:    erhanfirat@gmail.com
 * Licensed under the MIT license
 * Version: 1.2.1
 */
;(function ( $, window, document, undefined ) {

  // Default settings
  var comboTreePlugin = 'comboTree',
    defaults = {
      source: [],
      isMultiple: false,
      cascadeSelect: false,
      selected: [],
      collapse: false,
      selectableLastNode: false,
      withSelectAll: false,
      isolatedSelectable: false
    };

  // LIFE CYCLE
  function ComboTree( element, options ) {

    this.options = $.extend( {}, defaults, options) ;
    this._defaults = defaults;
    this._name = comboTreePlugin;

    this.constructorFunc(element, options);
  }

  ComboTree.prototype.constructorFunc = function(element, options){
    this.elemInput = element;
    this._elemInput = $(element);

    this.init();
  }

  ComboTree.prototype.init = function () {
    // Setting Doms
    this.comboTreeId = 'comboTree' + Math.floor(Math.random() * 999999);

    this._elemInput.addClass('comboTreeInputBox');

    if (this._elemInput.attr('id') === undefined) {
      this._elemInput.attr('id', this.comboTreeId + 'Input');
    }

    this.elemInputId = this._elemInput.attr('id');

    this._elemInput.wrap(`<div id="${this.comboTreeId}Wrapper" class="comboTreeWrapper"></div>`);
    this._elemWrapper = $(`#${this.comboTreeId}Wrapper`);

    this._selectionContainer = this.selectionContainer();
    this._selectionContainer.hide();
    this._elemWrapper.append(this._selectionContainer);

    this._elemWrapper.append('<div id="' + this.comboTreeId + 'DropDownContainer" class="comboTreeDropDownContainer"><div class="comboTreeDropDownContent"></div>');

    // DORP DOWN AREA
    this._elemDropDownContainer = $('#' + this.comboTreeId + 'DropDownContainer');

    this._elemDropDownContainer.html(this.createSourceHTML());
    this._elemFilterInput = this.options.isMultiple ? $('#' + this.comboTreeId + 'MultiFilter') : null;
    this._elemSelectAllInput = this.options.isMultiple && this.options.withSelectAll ? $('#' + this.comboTreeId + 'SelectAll') : null;
    this._elemSourceUl = $('#' + this.comboTreeId + 'ComboTreeSourceUl');

    this._elemItems = this._elemDropDownContainer.find('li');
    this._elemItemsText = this._elemDropDownContainer.find('span.comboTreeItemText');

    // VARIABLES
    this._selectedItem = {};
    this._selectedItems = [];

    this.processSelected();

    this.bindings();
  };

  ComboTree.prototype.unbind = function () {
    this._elemInput.off('click');
    this._elemItems.off('click');
    this._elemItemsText.off('click');
    this._elemItemsText.off("mousemove");
    this._elemInput.off('keyup');
    this._elemInput.off('keydown');
    this._elemInput.off('mouseup.' + this.comboTreeId);
    $(document).off('mouseup.' + this.comboTreeId);
  }

  ComboTree.prototype.destroy = function () {
    this.unbind();
    this._elemWrapper.before(this._elemInput);
    this._elemWrapper.remove();
    this._elemInput.removeData('plugin_' + comboTreePlugin);
  }



  // CREATE DOM HTMLs

  ComboTree.prototype.removeSourceHTML = function () {
    this._elemDropDownContainer.html('');
  };

  ComboTree.prototype.createSourceHTML = function () {
    var sourceHTML = '';
    if (this.options.isMultiple)
      sourceHTML += this.createFilterHTMLForMultiSelect();
    if (this.options.isMultiple && this.options.withSelectAll)
      sourceHTML += this.createSelectAllHTMLForMultiSelect();
    sourceHTML += this.createSourceSubItemsHTML(this.options.source);
    return sourceHTML;
  };

  ComboTree.prototype.createFilterHTMLForMultiSelect = function (){
    return '<input id="' + this.comboTreeId + 'MultiFilter" type="text" class="multiplesFilter" placeholder="Type to filter"/>';
  }

  ComboTree.prototype.createSelectAllHTMLForMultiSelect = function () {
    return '<label class="selectAll"><input class="combotreeInput" type="checkbox" id="' + this.comboTreeId + 'SelectAll' + '">[Select All]</label>';
  }

  ComboTree.prototype.createSourceSubItemsHTML = function (subItems, parentId, collapse=false) {
    var subItemsHtml = '<UL id="' + this.comboTreeId + 'ComboTreeSourceUl' + (parentId ? parentId : 'main' ) + '" style="' + (((this.options.collapse||collapse) && parentId) ? 'display:none;' : '')  + '">';
    for (var i=0; i<subItems.length; i++){
      subItemsHtml += this.createSourceItemHTML(subItems[i]);
    }
    subItemsHtml += '</UL>'
    return subItemsHtml;
  }

  ComboTree.prototype.escapeSourceItemId = function (sourceItem) {
    return sourceItem.id.replaceAll('/', '_');
  }

  ComboTree.prototype.createSourceItemHTML = function (sourceItem) {
    var itemHtml = "",
      isThereSubs = sourceItem.children && sourceItem.children.length > 0,
        collapse = sourceItem.hasOwnProperty("collapse") ? sourceItem.hasOwnProperty("collapse") : false;
    let isSelectable = (sourceItem.isSelectable === undefined ? true : sourceItem.isSelectable);
    let selectableClass = (isSelectable || isThereSubs) ? 'selectable' : 'not-selectable';
    if (this.options.isolatedSelectable) {
      selectableClass = isSelectable ? 'selectable' : 'not-selectable';
    }
    let selectableLastNode = (this.options.selectableLastNode !== undefined && isThereSubs) ? this.options.selectableLastNode : false;

    itemHtml += '<LI id="' + this.comboTreeId + 'Li' + this.escapeSourceItemId(sourceItem) + '" class="ComboTreeItem' + (isThereSubs?'Parent':'Chlid') + '"> ';

    if (isThereSubs)
      itemHtml += '<span class="comboTreeParentPlus">' + (this.options.collapse || collapse ? '<span class="fas fa-xs fa-chevron-right"></span>' : '<span class="fas fa-xs fa-chevron-down"></span>') + '</span>'; // itemHtml += '<span class="comboTreeParentPlus">' + (this.options.collapse ? '+' : '&minus;') + '</span>';

    if (this.options.isMultiple)
      itemHtml += '<span data-id="' + sourceItem.id + '" data-selectable="' + isSelectable + '" class="comboTreeItemText ' + selectableClass + '">' + (!selectableLastNode && isSelectable ? '<input class="combotreeInput" type="checkbox"/>' : '') + sourceItem.text + '</span>';
    else
      itemHtml += '<span data-id="' + sourceItem.id + '" data-selectable="' + isSelectable + '" class="comboTreeItemText ' + selectableClass + '">' + sourceItem.text + '</span>';

    if (isThereSubs)
      itemHtml += this.createSourceSubItemsHTML(sourceItem.children, this.escapeSourceItemId(sourceItem), collapse);

    itemHtml += '</LI>';
    return itemHtml;
  };


  // BINDINGS
  ComboTree.prototype.bindings = function () {
    var _this = this;

    $(this._elemInput).focus(function (e) {
      if (!_this._elemDropDownContainer.is(':visible'))
        $(_this._elemDropDownContainer).slideToggle(100);
    });

    this._elemInput.on('click', function(e){
      e.stopPropagation();
      if (!_this._elemDropDownContainer.is(':visible'))
        _this.toggleDropDown();
    });
    this._selectionContainer.on('click', function(e){
      if (!_this._elemDropDownContainer.is(':visible'))
        _this.toggleDropDown();
    });
    this._selectionContainer.on('click', '.select2-selection__clear', function(e) {
      e.stopPropagation();
      _this.clearSelection();
    });
    this._elemItems.on('click', function(e){
      e.stopPropagation();
      if ($(this).hasClass('ComboTreeItemParent')){
        _this.toggleSelectionTree(this);
      }
    });
    this._elemItemsText.on('click', function(e){
      e.stopPropagation();
      if (_this.options.isMultiple)
        _this.multiItemClick(this);
      else
        _this.singleItemClick(this);
    });
    this._elemItemsText.on("mousemove", function (e) {
      e.stopPropagation();
      _this.dropDownMenuHover(this);
    });
    this._elemSelectAllInput && this._elemSelectAllInput.parent("label").on("mousemove", function (e) {
      e.stopPropagation();
      _this.dropDownMenuHover(this);
    });

    // KEY BINDINGS
    this._elemInput.on('keyup', function(e) {
      e.stopPropagation();

      switch (e.keyCode) {
        case 27:
          _this.closeDropDownMenu(); break;
        case 13:
        case 39: case 37: case 40: case 38:
          e.preventDefault();
          break;
        default:
          if (!_this.options.isMultiple)
            _this.filterDropDownMenu();
          break;
      }
    });

    this._elemFilterInput && this._elemFilterInput.on('keyup', function (e) {
      e.stopPropagation();

      switch (e.keyCode) {
        case 27:
          if ($(this).val()) {
            $(this).val('');
            _this.filterDropDownMenu();
          } else {
            _this.closeDropDownMenu();
          }
          break;
        case 40: case 38:
          e.preventDefault();
          _this.dropDownInputKeyControl(e.keyCode - 39); break;
        case 37: case 39:
          e.preventDefault();
          _this.dropDownInputKeyToggleTreeControl(e.keyCode - 38);
          break;
        case 13:
          _this.multiItemClick(_this._elemHoveredItem);
          e.preventDefault();
          break;
        default:
          _this.filterDropDownMenu();
          break;
      }
    });

    this._elemInput.on('keydown', function(e) {
      e.stopPropagation();

      switch (e.keyCode) {
        case 9:
          _this.closeDropDownMenu(); break;
        case 40: case 38:
          e.preventDefault();
          _this.dropDownInputKeyControl(e.keyCode - 39); break;
        case 37: case 39:
          e.preventDefault();
          _this.dropDownInputKeyToggleTreeControl(e.keyCode - 38);
          break;
        case 13:
          if (_this.options.isMultiple)
            _this.multiItemClick(_this._elemHoveredItem);
          else
            _this.singleItemClick(_this._elemHoveredItem);
          e.preventDefault();
          break;
        default:
          if (_this.options.isMultiple)
            e.preventDefault();
      }
    });


    // ON FOCUS OUT CLOSE DROPDOWN
    $(document).on('mouseup.' + _this.comboTreeId, function (e){
      if (!_this._elemWrapper.is(e.target) && _this._elemWrapper.has(e.target).length === 0 && _this._elemDropDownContainer.is(':visible'))
        _this.closeDropDownMenu();
    });

    this._elemSelectAllInput && this._elemSelectAllInput.on('click', function (e) {
      e.stopPropagation();
      let checked = $(e.target).prop('checked');
      if (checked) {
        _this.selectAll();
      } else {
        _this.clearSelection();
      }
    });
  };





  // EVENTS HERE

  // DropDown Menu Open/Close
  ComboTree.prototype.toggleDropDown = function () {
    let _this = this;
    $(this._elemDropDownContainer).slideToggle(100, function () {
      if (_this._elemDropDownContainer.is(':visible'))
        $(_this._elemInput).focus();
    });
  };

  ComboTree.prototype.closeDropDownMenu = function () {
    $(this._elemDropDownContainer).slideUp(100);
  };

  // Selection Tree Open/Close
  ComboTree.prototype.toggleSelectionTree = function (item, direction) {
    var subMenu = $(item).children('ul')[0];
    if (direction === undefined){
      if ($(subMenu).is(':visible'))
        $(item).children('span.comboTreeParentPlus').html('<span class="fas fa-xs fa-chevron-right"></span>'); //$(item).children('span.comboTreeParentPlus').html("+");
      else
        $(item).children('span.comboTreeParentPlus').html('<span class="fas fa-xs fa-chevron-down"></span>'); //$(item).children('span.comboTreeParentPlus').html("&minus;");

      $(subMenu).slideToggle(50);
    }
    else if (direction == 1 && !$(subMenu).is(':visible')){
      $(item).children('span.comboTreeParentPlus').html('<span class="fas fa-xs fa-chevron-down"></span>'); //$(item).children('span.comboTreeParentPlus').html("&minus;");
      $(subMenu).slideDown(50);
    }
    else if (direction == -1){
      if ($(subMenu).is(':visible')){
        $(item).children('span.comboTreeParentPlus').html('<span class="fas fa-xs fa-chevron-right"></span>'); //$(item).children('span.comboTreeParentPlus').html("+");
        $(subMenu).slideUp(50);
      }
      else {
        this.dropDownMenuHoverToParentItem(item);
      }
    }

  };


  // SELECTION FUNCTIONS
  ComboTree.prototype.selectMultipleItem = function(ctItem){

    if (this.options.selectableLastNode && $(ctItem).parent('li').hasClass('ComboTreeItemParent')) {

      this.toggleSelectionTree($(ctItem).parent('li'));

      return false;
    }

    if ($(ctItem).data("selectable") == true) {
      this._selectedItem = {
        id: $(ctItem).attr("data-id"),
        text: $(ctItem).text()
      };

      let check = this.isItemInArray(this._selectedItem, this.options.source);
      if (check) {
        var index = this.isItemInArray(this._selectedItem, this._selectedItems);
        if (index) {
          this._selectedItems.splice(parseInt(index), 1);
          $(ctItem).find("input").prop('checked', false);
        } else {
          this._selectedItems.push(this._selectedItem);
          $(ctItem).find("input").prop('checked', true);
        }
      } // if check
    } // if selectable
  };

  ComboTree.prototype.singleItemClick = function (ctItem) {
    if ($(ctItem).data("selectable") == true) {
      this._selectedItem = {
        id: $(ctItem).attr("data-id"),
        text: $(ctItem).text()
      };
    } // if selectable

    this.refreshInputVal();
    this.closeDropDownMenu();
  };

  ComboTree.prototype.indeterminateCheck = 0;

  ComboTree.prototype.multiItemClick = function (ctItem) {
    this.indeterminateCheck ++;
    this.selectMultipleItem(ctItem);

    if (this.options.cascadeSelect) {
      if ($(ctItem).parent('li').hasClass('ComboTreeItemParent')) {
        var subMenu = $(ctItem).parent('li').children('ul').first().find('input[type="checkbox"]');
        subMenu.each(function() {
          var $input = $(this)
          if ($(ctItem).children('input[type="checkbox"]').first().prop("checked")!==$input.prop('checked')) {
            $input.prop('checked', !$(ctItem).children('input[type="checkbox"]').first().prop("checked"));
            $input.trigger('click');
          }
        });
      }
    }
    if(--this.indeterminateCheck == 0) {
        this.setParentsIndeterminate(ctItem);
    }
    this.refreshInputVal();
  };

  // indeterminate parents
  ComboTree.prototype.setParentsIndeterminate = function(item) {
    var cur = $(item).find('input:first');
    if(cur.prop('checked')) cur.prop('indeterminate', false)

    var cul = $(item).closest('ul');   //本层checkbox container
    var pli = $(cul).parents('li'); //上层checkbox
    if(pli.length > 0) {
        var pcheckbox = pli.find('input:first');
        if(!pcheckbox.prop('checked')) {
            var subset = $(cul).find("input");//本层 所有checkbox
            var checked = subset.filter((i, e)=>e.checked);
            pcheckbox.prop('indeterminate', checked.length > 0);  //any child checked -> indeterminate
        }
    }
  }

  // recursive search for item in arr
  ComboTree.prototype.isItemInArray = function (item, arr) {
    for (var i=0; i<arr.length; i++) {
      if (item.id == arr[i].id && item.text == arr[i].text)
        return i + "";

      if (arr[i].children && arr[i].children.length > 0) {
        let found = this.isItemInArray(item, arr[i].children);
        if (found)
          return found;
      }
    }
    return false;
  };


  /** use select2 style */
  ComboTree.prototype.selectionContainer = function () {
    var $container = $(
      `<span class="select2 select2-container select2-container--bootstrap4">
      <span class="selection">
        <span class="select2-selection select2-selection--multiple" role="combobox" aria-haspopup="true" aria-expanded="false">
          <ul class="select2-selection__rendered">
            <span class="select2-selection__clear" title="Remove all items" data-select2-id="25">×</span>
          </ul>
        </span>
      </span>
    </span>`
    );
    return $container;
  }
  ComboTree.prototype.selectionElement   = function (selectedItem) {
    var $element = $(
      `<li class="select2-selection__choice">
        <span class="select2-selection__choice__remove" role="presentation">&times;</span>
        <span style="font-size: 85%;">${selectedItem.text}</span>
      </li>`
    );
    $element.find('.select2-selection__choice__remove').on('click', (e) => {
      e.stopPropagation();
      this.multiItemClick(this.getSelectedItemElem(selectedItem).find('span.comboTreeItemText:first'));
    });
    return $element;
  };

  ComboTree.prototype.refreshInputVal = function () {
    var rendered = this._selectionContainer.find('.select2-selection__rendered');
    $(rendered).children('.select2-selection__choice').remove();

    if (this.options.isMultiple) {
      if(this._selectedItems.length > 0) {
        this._elemInput.addClass('select2-hidden-accessible');
        this._selectionContainer.show();
      } else {
        this._elemInput.removeClass('select2-hidden-accessible');
        this._selectionContainer.hide();
      }

      for (var i=0; i<this._selectedItems.length; i++){
        var element = this.selectionElement(this._selectedItems[i]);
        rendered.append(element);
      }
    } else {
       this._selectedItem.text;
    }
    if (this.changeHandler) {
      this.changeHandler();
    }
  };

  ComboTree.prototype.dropDownMenuHover = function (itemSpan, withScroll) {
    this._elemWrapper.find('.comboTreeItemHover').removeClass('comboTreeItemHover');
    $(itemSpan).addClass('comboTreeItemHover');
    this._elemHoveredItem = $(itemSpan);
    if (withScroll)
      this.dropDownScrollToHoveredItem(this._elemHoveredItem);
  }

  ComboTree.prototype.dropDownScrollToHoveredItem = function (itemSpan) {
    var curScroll = this._elemSourceUl.scrollTop();
    this._elemSourceUl.scrollTop(curScroll + $(itemSpan).parent().position().top - 80);
  }

  ComboTree.prototype.dropDownMenuHoverToParentItem = function (item) {
    var parentSpanItem = $($(item).parents('li.ComboTreeItemParent')[0]).children("span.comboTreeItemText");
    if (parentSpanItem.length)
      this.dropDownMenuHover(parentSpanItem, true);
    else
      this.dropDownMenuHover(this._elemItemsText[0], true);
  }

  ComboTree.prototype.dropDownInputKeyToggleTreeControl = function (direction) {
    var item = this._elemHoveredItem;
    if ($(item).parent('li').hasClass('ComboTreeItemParent'))
      this.toggleSelectionTree($(item).parent('li'), direction);
    else if (direction == -1)
      this.dropDownMenuHoverToParentItem(item);
  }

  ComboTree.prototype.dropDownInputKeyControl = function (step) {
    if (!this._elemDropDownContainer.is(":visible"))
      this.toggleDropDown();

    var list = this._elemItems.find("span.comboTreeItemText:visible");
    i = this._elemHoveredItem?list.index(this._elemHoveredItem) + step:0;
    i = (list.length + i) % list.length;

    this.dropDownMenuHover(list[i], true);
  },

    ComboTree.prototype.filterDropDownMenu = function () {
      var searchText =  '';
      if (!this.options.isMultiple)
        searchText = this._elemInput.val();
      else
        searchText = $("#" + this.comboTreeId + "MultiFilter").val();

      if (searchText != ""){
        this._elemItemsText.hide();
        this._elemItemsText.siblings("span.comboTreeParentPlus").hide();
        list = this._elemItems.filter(function(index, item){
          return item.innerHTML.toLowerCase().indexOf(searchText.toLowerCase()) != -1;
        }).each(function (i, elem) {
          $(this.children).show()
          $(this).siblings("span.comboTreeParentPlus").show();
        });
      }
      else{
        this._elemItemsText.show();
        this._elemItemsText.siblings("span.comboTreeParentPlus").show();
      }
    }

  ComboTree.prototype.processSelected = function () {
    let elements = this._elemItemsText;
    let selectedItem = this._selectedItem;
    let selectedItems = this._selectedItems;
    this.options.selected.forEach(function(element) {
      let selected = $(elements).filter(function(){
        return $(this).data('id') == element;
      });

      if(selected.length > 0){
        $(selected).find('input').attr('checked', true);

        selectedItem = {
          id: selected.data("id"),
          text: selected.text()
        };
        selectedItems.push(selectedItem);
      }
    });

    //Without this it doesn't work
    this._selectedItem = selectedItem;

    this.refreshInputVal();
  };


  // METHODS
  ComboTree.prototype.findItembyId = function(itemId, source) {
    if (itemId && source) {
      for (let i=0; i<source.length; i++) {
        if (source[i].id == itemId)
          return {id: source[i].id, text: source[i].text};
        if (source[i].children && source[i].children.length > 0) {
          let found = this.findItembyId(itemId, source[i].children);
          if (found)
            return found;
        }
      }
    }
    return null;
  }

  // Returns selected id array or null
  ComboTree.prototype.getSelectedIds = function () {
    if (this.options.isMultiple && this._selectedItems.length>0){
      var tmpArr = [];
      for (i=0; i<this._selectedItems.length; i++)
        tmpArr.push(this._selectedItems[i].id);

      return tmpArr;
    }
    else if (!this.options.isMultiple && this._selectedItem.hasOwnProperty('id')){
      return [this._selectedItem.id];
    }
    return null;
  };

  // Retuns Array (multiple), Integer (single), or False (No choice)
  ComboTree.prototype.getSelectedNames = function () {
    if (this.options.isMultiple && this._selectedItems.length>0){
      var tmpArr = [];
      for (i=0; i<this._selectedItems.length; i++)
        tmpArr.push(this._selectedItems[i].text);

      return tmpArr;
    }
    else if (!this.options.isMultiple && this._selectedItem.hasOwnProperty('id')){
      return this._selectedItem.text;
    }
    return null;
  };

  ComboTree.prototype.setSource = function(source) {
    this._selectedItems = [];

    this.destroy();
    this.options.source = source;
    this.constructorFunc(this.elemInput, this.options);
  };

  ComboTree.prototype.clearSelection = function() {
    for (i=0; i<this._selectedItems.length; i++) {
      this.getSelectedItemElem(this._selectedItems[i]).find("input").prop('checked', false);
    }
    this._elemDropDownContainer.find("input").prop('indeterminate', false);

    this._selectedItems = [];
    this._selectedItem = {};
    if(this._elemSelectAllInput){
      this._elemSelectAllInput.prop("checked", false);
    }
    this.refreshInputVal();
  };

  ComboTree.prototype.getSelectedItemElem = function(selectedItem) {
    return $(`#${this.comboTreeId}Li${this.escapeSourceItemId(selectedItem)}`);
  }

  ComboTree.prototype.setSelection = function (selectionIdList) {
    if (selectionIdList && selectionIdList.length && selectionIdList.length > 0) {
      for (let i = 0; i < selectionIdList.length; i++) {
        let selectedItem = this.findItembyId(selectionIdList[i], this.options.source);
        if (selectedItem) {
          let check = this.isItemInArray(selectedItem, this.options.source);
          if (check) {
            var index = this.isItemInArray(selectedItem, this._selectedItems);
            if (!index) {
              let selectedItemElem = this.getSelectedItemElem(selectedItem);
              this._selectedItems.push(selectedItem);
              this._selectedItem = selectedItem;
              // If cascadeSelect is true, check all children, otherwise just check this item
//              if (this.options.cascadeSelect) {
//                $(selectedItemElem).find("input").prop('checked', true);
//              } else {
                $(selectedItemElem).find("input:first").prop('checked', true);
                this.setParentsIndeterminate(selectedItemElem);
//              }
            }
          }
        }
      }
    }

    this.refreshInputVal();
  };

  ComboTree.prototype.selectAll = function () {
    // clear
    for (let i = 0; i < this._selectedItems.length; i++) {
      this.getSelectedItemElem(this._selectedItems[i]).find('input').prop('checked', false);
    }
    this._selectedItems = [];
    // select all
    let selected = this._selectedItems;
    $('#' + this.comboTreeId + 'ComboTreeSourceUlmain')
        .find("input[type='checkbox']")
        .each(function (idx, elem) {
          let $itemElem = $(elem).parent('span').first();
          let item = {
            id: $itemElem.data('id'),
            text: $itemElem.text(),
          };
          $(elem).prop('checked', true);
          selected.push(item);
        });
    if(this._elemSelectAllInput){
      this._elemSelectAllInput.prop("checked", true);
    }

    this.refreshInputVal();
  };

  // EVENTS
  ComboTree.prototype.onChange = function(callBack) {
    if (callBack && typeof callBack === "function")
      this.changeHandler = callBack;
  };

  // -----
  $.fn[comboTreePlugin] = function (options) {
    var ctArr = [];
    this.each(function () {
      if (!$.data(this, 'plugin_' + comboTreePlugin)) {
        $.data(this, 'plugin_' + comboTreePlugin, new ComboTree( this, options));
        ctArr.push($(this).data()['plugin_' + comboTreePlugin]);
      }
    });

    if (this.length == 1)
      return ctArr[0];
    else
      return ctArr;
  }

})( jQuery, window, document );
