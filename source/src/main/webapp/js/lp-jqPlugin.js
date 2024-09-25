//表单控件
;(function(){
    $.fn.extend({
        //自定义单选框
        lpRadioBox:function(options){
            //==== set default ====
            var myDefault = {
                "radioCls":"radio",                //radiobox class
                "radioHoverCls":"radio_hover",     //radiobox hover class
                "radioCheckedCls":"radio_checked", //radiobox checked class
                "labelCls":"radioLabel",           //label class
                "labelHoverCls":"radioLabel_hover", //label hover class
                "onCheckedFun":null
            };

            options = $.extend({}, myDefault, options);

            //==== create radioBox ====
            return this.each(function(){
                var $this = $(this);
                $(this).wrap('<em class="' + options.radioCls +'"></em>').css("display", "none"); //将原生radio隐藏，并嵌套一层模拟radiobox
                var $radio = $(this).parent("em");
                var name = $(this).attr("name");
                var labelTxt = $(this).attr("data-label");
                (labelTxt && $radio.after('<label class="'+ options.labelCls +'">'+ labelTxt +'</label>'));//存在label标签

                //==== bind operation ====
                defChecked();
                $radio.click(radioClick).next("label").click(radioClick);
                $radio.mouseenter(radioMEnter).mouseleave(radioMLeave).next("label").mouseenter(radioMEnter).mouseleave(radioMLeave);
                if(options.onCheckedFun){
                    onCheckedFun(a);
                };
                //==== operation function ====
                function defChecked(){
                    var checkedLen = $('input[name="'+name+'"]:checked').length;
                    if(checkedLen){
                        $('input[name="'+name+'"]:checked').parent("em").addClass(options.radioCheckedCls).siblings("em").removeClass(options.radioCheckedCls).find("input").attr("checked",false);
                    }else{
                        $('input[name="' +name+ '"]:first').attr("checked",true).parent("em").addClass(options.radioCheckedCls);
                    }
                }
                function radioClick(){
                    $('input[name="' +name+ '"]').attr("checked",false).parent("em").removeClass(options.radioCheckedCls);
                    $radio.addClass(options.radioCheckedCls).find("input").attr("checked",true);
                };
                function radioMEnter(){
                    var isChecked = $radio.hasClass(options.radioCheckedCls);
                    (!isChecked && $radio.addClass(options.radioHoverCls).next("label").addClass(options.labelHoverCls));
                };
                function radioMLeave(){
                    $radio.removeClass(options.radioHoverCls).next("label").removeClass(options.labelHoverCls);
                };
            });
        },

        //自定义多选框
        lpCheckBox:function(options){
            //==== set default ====
            var myDefault = {
                "ckbCls":"ckb",                  //checkbox class
                "ckbHoverCls":"ckb_hover",       //checkbox hover class
                "ckbCheckedCls":"ckb_checked",   //checkbox checked class
                "labelCls":"ckbLabel",           //checkbox label class
                "labelHoverCls":"ckbLabel_hover",//checkbox label hover class
                "isCkbAll":false,                //is checkAll?
                "checkedItems": ""
            };
            options = $.extend({}, myDefault, options);

            return this.each(function(){
                //==== draw checkbox ====
                var $this = $(this);
                $(this).wrap('<em class="' + options.ckbCls +'"></em>').css("display", "none"); //将原生checkbox隐藏，并嵌套一层模拟checkbox
                var $ckb = $(this).parent("em");
                var labelTxt = $(this).attr("data-label");
                var name = $ckb.find("input").attr("name");
                (options.isCkbAll && $ckb.addClass("ckbAll"));  //控制全选的复选框（点击时间会联动同组name的复选框）
                (labelTxt && $ckb.after('<label class="'+ options.labelCls +'">'+ labelTxt +'</label>'));//存在label标签

                //==== bind operation ====
                //初始选中
                if(options.checkedItems){
                    outSetChecked();
                }else{
                    defChecked();
                }
                $ckb.click(checkedToggle).next("label").click(checkedToggle);
                $ckb.mouseenter(ckbMEnter).mouseleave(ckbMLeave).next("label").mouseenter(ckbMEnter).mouseleave(ckbMLeave);

                //==== operation function ====
                function outSetChecked(){
                    var checkedId = options.checkedItems.split(",");
                    for(var i=0; i<checkedId.length; i++){
                        $("input#"+checkedId[i]).attr("checked",true).parent("em").addClass(options.ckbCheckedCls);
                    }

                };
                function defChecked(){
                    ($this.attr("checked") && $this.parent("em").addClass(options.ckbCheckedCls));//初始选中
                };
                function checkedToggle(){
                    var isChecked = $ckb.find("input").get(0).checked;//是否选中
                    var isCkbAll = $ckb.hasClass("ckbAll");              //是否为全选按钮
                    //点击全选复选框
                    if(isCkbAll){
                        (isChecked && $('input[name="' +name+ '"]').attr("checked",false).parent("em").removeClass(options.ckbCheckedCls));  //全选
                        (!isChecked && $('input[name="' +name+ '"]').attr("checked",true).parent("em").addClass(options.ckbCheckedCls));     //取消全选

                        //点击普通复选框
                    }else{
                        (isChecked && $ckb.removeClass(options.ckbCheckedCls).find("input").attr("checked",false));  //取消当前项勾选
                        (!isChecked && $ckb.addClass(options.ckbCheckedCls).find("input").attr("checked",true));     //选中当前项

                        //联动全选
                        var ckbLen = $('input[name="' +name+ '"]').not(".ckbAll input").length;
                        var ckbCheckedLen = $('input[name="' +name+ '"]:checked').not(".ckbAll input").length;
                        (ckbCheckedLen == ckbLen && $('input[name="' +name+ '"]').attr("checked",true).parent("em").addClass(options.ckbCheckedCls));
                        (ckbCheckedLen != ckbLen &&  $('input[name="' +name+ '"]').parent(".ckbAll").removeClass(options.ckbCheckedCls).children("input").attr("checked",false));
                    };
                };
                function ckbMEnter(){
                    var isChecked = $ckb.find("input").get(0).checked;//是否选中
                    if(!isChecked){
                        $ckb.addClass(options.ckbHoverCls).next("label").addClass(options.labelHoverCls);
                    }
                };
                function ckbMLeave(){
                    $ckb.removeClass(options.ckbHoverCls).next("label").removeClass(options.labelHoverCls);
                };
            });
        },

        //自定义下拉框
        lpComboBox:function(options){
            //==== set default ====
            var myDefault = {
                "comboCls":"combo",                 //input class
                "comboHoverCls":"combo_hover",      //input hover class
                "hasArrow":true,                   //hasArrow ? show : hide
                "arrowCls":"comboArrow",            //arrow class
                "arrowHoverCls":"comboArrow_hover", //arrow hover class
                "ulCls":"comboUl",                  //ul class
                "liHoverCls":"comboLi_hover"       //li hover class
            };
            options = $.extend({}, myDefault, options);

            return this.each(function(){
                //==== create comboBox ====
                var myArray = new Array();
                var opt, tagLi, selectedIdx, html ,$comboWrap;
                opt = $(this).find("option");
                for (var i = 0; i < opt.length; i++) {
                    var optTxt, optId, optVal;
                    optTxt = opt.eq(i).text();    //原生option的text
                    optId = opt.eq(i).attr("id"); //原生option的id
                    optVal = opt.eq(i).val();     //原生option的value

                    if (optId && optVal) {
                        myArray.push('<li optId="' + optId + '" optVal="' + optVal + '">' + optTxt + '</li>');
                    } else if (optId) {
                        myArray.push('<li optId="' + optId + '">' + optTxt + '</li>');
                    } else if (optVal) {
                        myArray.push('<li optVal="' + optVal + '">' + optTxt + '</li>');
                    } else {
                        myArray.push('<li>' + optTxt + '</li>');
                    }
                };

                tagLi = myArray.join(""); //li 模拟原生option 列表
                selectedIdx = $(this).attr("selectedIdx")>0 ? $(this).attr("selectedIdx") : 0;  // 初始选中项的索引
                html = '<input type="text" class="' + options.comboCls + '" selectedId="' + opt.eq(selectedIdx).attr("id") + '" value="' + opt.eq(selectedIdx).text() + '" />'
                    + '<em class="' + options.arrowCls + '"></em>'
                    + '<ul class=' + options.ulCls + '>' + tagLi + '</ul>';
                $(this).val(opt.eq(selectedIdx).val()).attr("selectedIdx",selectedIdx).wrap('<div class="' + options.comboCls + 'Wrap"></div>').css("display", "none"); //将原生select隐藏，并嵌套一层模拟下拉
                $comboWrap = $(this).parents("." + options.comboCls + "Wrap");
                $comboWrap.prepend(html);

                //==== all kinds of situation ====
                (!options.hasArrow && $comboWrap.find("em").remove());  //无下拉箭头
                (!opt.eq(selectedIdx).attr("id") && $comboWrap.find("input").removeAttr("selectedId")); //初始选中项id不存在
                (opt.eq(selectedIdx).attr("id") && $(this).attr("selectedId", opt.eq(selectedIdx).attr("id"))); //初始选中项id存在

                //==== bind operation ====
                $comboWrap.click(comboClick);
                $(document).click(function (e) {
                    ulHide(e);
                });
                $comboWrap.mouseenter(addHighLight).mouseleave(comboMLeave);
                $comboWrap.find("li").mouseenter(liMEnter).mouseleave(liMLeave);
                $comboWrap.find("li").click(liClick);

                //==== operation function ====
                function comboMLeave() {
                    var isHide = $(this).find("ul").is(":hidden");
                    (isHide && removeHighLight());
                }
                function comboClick() {
                    $(this).find("input").blur();
                    $(this).find("ul").toggle();
                }
                function ulHide(e) {
                    var $eClass = $(e.target).attr("class");
                    var $arrowClass;
                    if (options.hasArrow) {
                        $arrowClass = $comboWrap.find("em").attr("class");
                    } else {
                        $arrowClass = 0;
                    }

                    if ($eClass != $comboWrap.find("input").attr("class") && $eClass != $arrowClass) {
                        $comboWrap.find("ul").hide();
                        removeHighLight();
                    }
                    return false;
                }
                function liMEnter() {
                    $(this).addClass(options.liHoverCls);
                }
                function liMLeave() {
                    $(this).removeAttr("class");
                }
                function liClick() {
                    var optTxt, optId, optVal, optIdx;
                    optTxt = $(this).text();        //li对应 option的text
                    optId = $(this).attr("optId");  //li对应 option的id
                    optVal = $(this).attr("optVal");//li对应 option的value
                    optIdx = $(this).index();       //li对应 option的index

                    (optId && $comboWrap.find("select").attr("selectedId", optId));
                    (!optId && $comboWrap.find("select").removeAttr("selectedId"));
                    $comboWrap.find("select").val(optVal).attr("selectedIdx", optIdx);
                    $comboWrap.find("input").val(optTxt);
                    $comboWrap.find("input").attr("selectedId", optId);
                    removeHighLight();
                }
                function addHighLight() {
                    $comboWrap.find("input").addClass(options.comboHoverCls);
                    $comboWrap.find("em").addClass(options.arrowHoverCls);
                }
                function removeHighLight() {
                    $comboWrap.find("input").removeClass(options.comboHoverCls);
                    $comboWrap.find("em").removeClass(options.arrowHoverCls);
                }
            });
        }
    })
})(jQuery);

//日历
; (function ($) {
    var curDate = new Date();
    var dateJson = {
        parameter: null,
        elementObj: null,
        elementValue: null,
        year: curDate.getFullYear(),
        month: curDate.getMonth() + 1,
        eTarget: null,
        state: "hide"
    };

    $.fn.xuhjDate = function (parameter) {
        parameter = $.extend({}, $.fn.xuhjDate.defaults, parameter);
        dateJson.parameter = parameter;
        var html = draw();
        $("body").append(html);
        $(".xuhj-select-year, .xuhj-select-month").change(reDrawDays).blur(changeSelectTitle);
        $(".xuhj-select-y-em, .xuhj-select-m-em").hover(selectEmHover).click(selectEmClick);
        $(".xuhj-days-tr").find(".xuhj-normal-day").hover(dayHover).click(daySelected);
        $(this).each(function () {
            $(this).focus(elementClick).click(function (e) { e.stopPropagation(); });
        });
        $(".xuhjDate").click(function (e) { e.stopPropagation(); });
        $(document).click(function (e) { $(".xuhjDate").hide(); dateJson.state = "hide"; });
        $(".xuhj-select-yearnext, .xuhj-select-yearprev").click(yearUpAndNext);
        $(".xuhj-select-monthprev, .xuhj-select-monthnext").click(monthUpAndNext);
        $(".xuhj-btn-today").click(btnTodayClick);
        $(".xuhj-btn-clear").click(btnClearClick);
        $(".xuhj-btn-close").click(btnCloseClick);
    };

    $.fn.xuhjDate.defaults = {
        onOkClick: null,
        onTodayClick: null,
        onClearClick: null,
        onCloseClick: null,
        maxDate: null,
        minDate: null
    };

    function draw() {
        var html = "";
        var ym = drawYearAndmonth();
        var dayhtml = drawDays();
        html = '<div class="xuhjDate" style="display:none;"><table cellspacing="0" cellpadding="0">';
        html = html + '<tr class="xuhj-select-tr">'
            + '<td colspan="7" class="xuhj-select-date"><a href="javascript:void(0);" class="xuhj-select-yearprev" hideFocus="false"><<</a>'
            + '<a href="javascript:void(0);" class="xuhj-select-monthprev" hideFocus="false"><</a>'
            + ym
            + '<a href="javascript:void(0);" class="xuhj-select-monthnext" hideFocus="false">></a>'
            + '<a href="javascript:void(0);" class="xuhj-select-yearnext" hideFocus="false">>></a></td>'
            + '</tr>';
        html = html + '<tr class="xuhj-week-tr">'
            + '<td>日</td>'
            + '<td>一</td>'
            + '<td>二</td>'
            + '<td>三</td>'
            + '<td>四</td>'
            + '<td>五</td>'
            + '<td>六</td>' + '</tr>';
        html = html + dayhtml + '</table></div>';
        return html;
    };
    function reDrawDays(e) {
        dateJson.year = $(this).parent().find('.xuhj-select-year').val();
        dateJson.month = $(this).parent().find('.xuhj-select-month').val();
        showDays(dateJson.year, dateJson.month);
        changeSelectTitle();
    };
    function drawYearAndmonth() {
        var ymhtml = "", yhtml = "", mhtml = "", y, m;
        if (dateJson.parameter.maxDate == null && dateJson.parameter.minDate == null) {
            y = curDate.getFullYear();
            m = curDate.getMonth() + 1;
            for (var i = (y - 100) ; i < (y + 10) ; i++) {
                if (i == y) {
                    yhtml = yhtml + '<option selected="selected">' + i + '</option>';
                } else {
                    yhtml = yhtml + '<option>' + i + '</option>';
                }
            };
            yhtml = '<select class="xuhj-select-year" style="display:none;">' + yhtml + '</select>';
            yhtml = yhtml + '<em class="xuhj-select-y-em">' + y + '</em>';
            for (var i = 1; i < 13; i++) {
                if (i == m) {
                    mhtml = mhtml + '<option selected="selected">' + i + '</option>';
                } else {
                    mhtml = mhtml + '<option>' + i + '</option>';
                }
            };
            mhtml = '<select class="xuhj-select-month" style="display:none;">' + mhtml + '</select>';
            mhtml = mhtml + '<em class="xuhj-select-m-em">' + (m > 9 ? m : ("0" + m)) + '月</em>';
            ymhtml = yhtml + mhtml;
        };
        return ymhtml;
    };
    function drawDays() {
        var dayshtml = "";
        if (dateJson.parameter.maxDate == null && dateJson.parameter.minDate == null) {
            dayshtml = getDaysHtml(curDate.getFullYear(), curDate.getMonth() + 1);
        };
        return dayshtml;
    };
    function getDaysHtml(year, month) {
        var dayshtml = "", trhtmls = "", tdhtmls = "", week, upmonthMaxDay, curmonthDays, day = 1, bdays, edays = 1, classname = "";
        week = new Date(year, month - 1, 1).getDay();
        upmonthMaxDay = new Date(year, month - 1, 0).getDate();
        curmonthDays = new Date(year, month, 0).getDate();
        bdays = upmonthMaxDay - week + 1;

        for (var i = 0; i < 6; i++) {
            for (var j = 0; j < 7; j++) {
                if (((i * 7 + j) > week - 1) && ((i * 7 + j) < (curmonthDays + week))) {
                    if (year == curDate.getFullYear() && (month == curDate.getMonth() + 1) && day == curDate.getDate()) {
                        classname = "xuhj-normal-day xuhj-today-day";
                    } else if (dateJson.elementValue && year == dateJson.elementValue.substr(0, 4)
                        && month == dateJson.elementValue.substr(5, 2) && day == dateJson.elementValue.substr(8, 2)) {
                        classname = "xuhj-normal-day xuhj-selected-day";
                    } else {
                        classname = "xuhj-normal-day";
                    };
                    if (j == 0 || j == 6) {
                        classname = classname + " xuhj-weekend-day";
                    };
                    tdhtmls = tdhtmls + '<td class="' + classname + '">' + day + '</td>';
                    day++;
                } else {
                    if ((i * 7 + j) < week) {
                        tdhtmls = tdhtmls + '<td class="xuhj-disable-day">' + bdays + '</td>';
                        bdays++;
                    } else {
                        tdhtmls = tdhtmls + '<td class="xuhj-disable-day">' + edays + '</td>';
                        edays++;
                    }
                };
            };
            trhtmls = '<tr class="xuhj-days-tr">' + tdhtmls + '</tr>';
            dayshtml = dayshtml + trhtmls;
            tdhtmls = "";
        };
        return dayshtml;
    };
    function elementClick(e) {
        dateJson.elementObj = $(this);
        if ($(this).is("input") || $(this).is("textarea")) {
            dateJson.elementValue = $(this).val();
            if (dateJson.elementValue.length == 10) {
                try {
                    var y = $(this).val().substr(0, 4),
                        m = $(this).val().substr(5, 2),
                        d = $(this).val().substr(8, 2);
                    var dd = new Date(y, m - 1, d);
                    if ((dateJson.eTarget != ((e.srcElement) ? e.srcElement : e.target)) || (dateJson.eTarget == ((e.srcElement) ? e.srcElement : e.target) && dateJson.state == "hide")) {
                        dateJson.year = dd.getFullYear();
                        dateJson.month = dd.getMonth() + 1;
                    }
                } catch (e) {
                    dateJson.year = curDate.getFullYear();
                    dateJson.month = curDate.getMonth() + 1;
                }
            } else {
                if ((dateJson.eTarget != ((e.srcElement) ? e.srcElement : e.target)) || (dateJson.eTarget == ((e.srcElement) ? e.srcElement : e.target) && dateJson.state == "hide")) {
                    dateJson.year = curDate.getFullYear();
                    dateJson.month = curDate.getMonth() + 1;
                };
            };
        };
        showDays(dateJson.year, dateJson.month);
        changeSelectTitle();
        $(".xuhjDate").css({
            "position": "absolute",
            "z-index": "9999",
            "top": $(this).offset().top + $(this).height() + 7,
            "left": $(this).offset().left
        }).show();
        dateJson.state = "show";
        dateJson.eTarget = ((e.srcElement) ? e.srcElement : e.target);
    };
    function daySelected(e) {
        if ($(this).hasClass("xuhj-disable-day")) {
            return false;
        } else {
            $(".xuhjDate").find("table").removeClass("xuhj-selected-day");
            $(this).addClass("xuhj-selected-day");
            var d = $(this).text();
            var dt = dateJson.year + "-" + (dateJson.month > 9 ? dateJson.month : ("0" + dateJson.month)) + "-" + (d > 9 ? d : ("0" + d));
            if (dateJson.elementObj.is("input") || dateJson.elementObj.is("textarea")) {
                dateJson.elementObj.val(dt);
            } else {
                dateJson.elementObj.html(dt);
            };
            if (dateJson.parameter.onOkClick) {
                dateJson.parameter.onOkClick(dt);
            };
            $(".xuhjDate").hide();
            dateJson.state = "hide";
        };
    };
    function dayHover(e) {
        $(this).toggleClass("xuhj-hover-day");
    };
    function showDays(year, month) {
        var len = $(".xuhjDate .xuhj-select-year")[0].options.length;
        var minYear = $(".xuhjDate .xuhj-select-year")[0].options[0].value;
        var maxYear = parseInt(len) + parseInt(minYear) - 1;
        if (year < minYear || year > maxYear) {
            return false;
        };
        var ht = getDaysHtml(year, month);
        $(".xuhjDate").find("table").find(".xuhj-select-year").val(year);
        $(".xuhjDate").find("table").find(".xuhj-select-month").val(month);
        $(".xuhjDate").find("table").find(".xuhj-days-tr").remove();
        $(".xuhjDate").find("table").append(ht);
        $(".xuhj-days-tr").find(".xuhj-normal-day").hover(dayHover).click(daySelected);
        return true;
    };
    function yearUpAndNext(e) {
        if ($(this).hasClass("xuhj-select-yearnext")) {
            var yy = parseInt(dateJson.year) + 1;
            if (showDays(yy, dateJson.month)) {
                dateJson.year = parseInt(dateJson.year) + 1;
            };
        } else {
            var yy = parseInt(dateJson.year) - 1;
            if (showDays(yy, dateJson.month)) {
                dateJson.year = parseInt(dateJson.year) - 1;
            };
        };
        changeSelectTitle();
    };
    Date.prototype.addMonths = function (m) {
        var d = this.getDate();
        this.setMonth(this.getMonth() + m);

        if (this.getDate() < d)
            this.setDate(0);
    };
    function monthUpAndNext(e) {
        var dd = new Date(dateJson.year, dateJson.month - 1, 1);
        if ($(this).hasClass("xuhj-select-monthnext")) {
            dd.addMonths(1);
        } else {
            dd.addMonths(-1);
        };
        if (showDays(dd.getFullYear(), dd.getMonth() + 1)) {
            dateJson.year = dd.getFullYear();
            dateJson.month = dd.getMonth() + 1;
        };
        changeSelectTitle();
    };
    function btnTodayClick(e) {
        dateJson.year = curDate.getFullYear();
        dateJson.month = curDate.getMonth() + 1;
        var d = curDate.getDate();
        var dt = dateJson.year + "-" + (dateJson.month > 9 ? dateJson.month : ("0" + dateJson.month)) + "-" + (d > 9 ? d : ("0" + d));
        if (dateJson.elementObj.is("input") || dateJson.elementObj.is("textarea")) {
            dateJson.elementObj.val(dt);
        } else {
            dateJson.elementObj.html(dt);
        };
        if (dateJson.parameter.onTodayClick) {
            dateJson.parameter.onTodayClick(dt);
        };
        $(".xuhjDate").hide();
        dateJson.state = "hide";
    };
    function btnClearClick(e) {
        $(".xuhjDate").hide();
        dateJson.state = "hide";
        if (dateJson.elementObj.is("input") || dateJson.elementObj.is("textarea")) {
            dateJson.elementObj.val("");
        } else {
            dateJson.elementObj.html("");
        };
        if (dateJson.parameter.onClearClick) {
            dateJson.parameter.onClearClick();
        };
    };
    function btnCloseClick(e) {
        $(".xuhjDate").hide();
        dateJson.state = "hide";
        if (dateJson.parameter.onCloseClick) {
            dateJson.parameter.onCloseClick();
        };
    };
    function selectEmHover(e) {
        $(this).toggleClass("xuhj-select-emhover");
    };
    function selectEmClick(e) {
        if ($(this).hasClass("xuhj-select-y-em")) {
            $(".xuhj-select-y-em").hide();
            $(".xuhj-select-year").show().focus();
        } else {
            $(".xuhj-select-m-em").hide();
            $(".xuhj-select-month").show().focus();
        };
    };
    function changeSelectTitle() {
        var y = $(".xuhj-select-year").val() + "年";
        var m = $(".xuhj-select-month").val();
        m = (m > 9 ? m : ("0" + m)) + "月";
        $(".xuhj-select-y-em").text(y).show();
        $(".xuhj-select-year").hide();
        $(".xuhj-select-m-em").text(m).show();
        $(".xuhj-select-month").hide();
    };
})(jQuery);

//获取文档高宽度、窗口可见高宽度
; (function ($) {
    $.extend({
        //获取文档高度
        lpDocHt: function () {
            return $(document).height();
        },
        //获取文档宽度
        lpDocWh: function () {
            return $(document).width();
        },
        //获取窗口可见高度
        lpWinHt: function () {
            return $(window).height();
        },
        //获取窗口可见宽度
        lpWinWh: function () {
            return $(window).width();
        }
    })
})(jQuery);


//日期起止时间
var dateRangeUtil = (function () {
    /***
     * 获得当前时间
     */
    this.getCurrentDate = function () {
        return new Date();
    };

    /***
     * 获得本周起止时间
     */
    this.getCurrentWeek = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //返回date是一周中的某一天
        var week = currentDate.getDay();
        //返回date是一个月中的某一天
        var month = currentDate.getDate();

        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //减去的天数
        var minusDay = week != 0 ? week - 1 : 6;
        //alert(minusDay);
        //本周 周一
        var monday = new Date(currentDate.getTime() - (minusDay * millisecond));
        //本周 周日
        var sunday = new Date(monday.getTime() + (6 * millisecond));
        //添加本周时间
        startStop.push(monday); //本周起始时间
        //添加本周最后一天时间
        startStop.push(sunday); //本周终止时间
        //返回
        return startStop;
    };

    /***
     * 获得本月的起止时间
     */
    this.getCurrentMonth = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //求出本月第一天
        var firstDay = new Date(currentYear, currentMonth, 1);


        //当为12月的时候年份需要加1
        //月份需要更新为0 也就是下一年的第一个月
        if (currentMonth == 11) {
            currentYear++;
            currentMonth = 0; //就为
        } else {
            //否则只是月份增加,以便求的下一月的第一天
            currentMonth++;
        }


        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //下月的第一天
        var nextMonthDayOne = new Date(currentYear, currentMonth, 1);
        //求出上月的最后一天
        var lastDay = new Date(nextMonthDayOne.getTime() - millisecond);

        //添加至数组中返回
        startStop.push(firstDay);
        startStop.push(lastDay);
        //返回
        return startStop;
    };

    /**
     * 得到本季度开始的月份
     * @param month 需要计算的月份
     ***/
    this.getQuarterSeasonStartMonth = function (month) {
        var quarterMonthStart = 0;
        var spring = 0; //春
        var summer = 3; //夏
        var fall = 6;   //秋
        var winter = 9; //冬
        //月份从0-11
        if (month < 3) {
            return spring;
        }

        if (month < 6) {
            return summer;
        }

        if (month < 9) {
            return fall;
        }

        return winter;
    };

    /**
     * 获得该月的天数
     * @param year年份
     * @param month月份
     * */
    this.getMonthDays = function (year, month) {
        //本月第一天 1-31
        var relativeDate = new Date(year, month, 1);
        //获得当前月份0-11
        var relativeMonth = relativeDate.getMonth();
        //获得当前年份4位年
        var relativeYear = relativeDate.getFullYear();

        //当为12月的时候年份需要加1
        //月份需要更新为0 也就是下一年的第一个月
        if (relativeMonth == 11) {
            relativeYear++;
            relativeMonth = 0;
        } else {
            //否则只是月份增加,以便求的下一月的第一天
            relativeMonth++;
        }
        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //下月的第一天
        var nextMonthDayOne = new Date(relativeYear, relativeMonth, 1);
        //返回得到上月的最后一天,也就是本月总天数
        return new Date(nextMonthDayOne.getTime() - millisecond).getDate();
    };

    /**
     * 获得本季度的起止日期
     */
    this.getCurrentSeason = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //获得本季度开始月份
        var quarterSeasonStartMonth = this.getQuarterSeasonStartMonth(currentMonth);
        //获得本季度结束月份
        var quarterSeasonEndMonth = quarterSeasonStartMonth + 2;

        //获得本季度开始的日期
        var quarterSeasonStartDate = new Date(currentYear, quarterSeasonStartMonth, 1);
        //获得本季度结束的日期
        var quarterSeasonEndDate = new Date(currentYear, quarterSeasonEndMonth, this.getMonthDays(currentYear, quarterSeasonEndMonth));
        //加入数组返回
        startStop.push(quarterSeasonStartDate);
        startStop.push(quarterSeasonEndDate);
        //返回
        return startStop;
    };

    /***
     * 得到本年的起止日期
     *
     */
    this.getCurrentYear = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();

        //本年第一天
        var currentYearFirstDate = new Date(currentYear, 0, 1);
        //本年最后一天
        var currentYearLastDate = new Date(currentYear, 11, 31);
        //添加至数组
        startStop.push(currentYearFirstDate);
        startStop.push(currentYearLastDate);
        //返回
        return startStop;
    };

    /**
     * 返回上一个月的第一天Date类型
     * @param year 年
     * @param month 月
     **/
    this.getPriorMonthFirstDay = function (year, month) {
        //年份为0代表,是本年的第一月,所以不能减
        if (month == 0) {
            month = 11; //月份为上年的最后月份
            year--; //年份减1
            return new Date(year, month, 1);
        }
        //否则,只减去月份
        month--;
        return new Date(year, month, 1); ;
    };

    /**
     * 获得上一月的起止日期
     * ***/
    this.getPreviousMonth = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //获得上一个月的第一天
        var priorMonthFirstDay = this.getPriorMonthFirstDay(currentYear, currentMonth);
        //获得上一月的最后一天
        var priorMonthLastDay = new Date(priorMonthFirstDay.getFullYear(), priorMonthFirstDay.getMonth(), this.getMonthDays(priorMonthFirstDay.getFullYear(), priorMonthFirstDay.getMonth()));
        //添加至数组
        startStop.push(priorMonthFirstDay);
        startStop.push(priorMonthLastDay);
        //返回
        return startStop;
    };


    /**
     * 获得上一周的起止日期
     * **/
    this.getPreviousWeek = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //返回date是一周中的某一天
        var week = currentDate.getDay();
        //返回date是一个月中的某一天
        var month = currentDate.getDate();
        //一天的毫秒数
        var millisecond = 1000 * 60 * 60 * 24;
        //减去的天数
        var minusDay = week != 0 ? week - 1 : 6;
        //获得当前周的第一天
        var currentWeekDayOne = new Date(currentDate.getTime() - (millisecond * minusDay));
        //上周最后一天即本周开始的前一天
        var priorWeekLastDay = new Date(currentWeekDayOne.getTime() - millisecond);
        //上周的第一天
        var priorWeekFirstDay = new Date(priorWeekLastDay.getTime() - (millisecond * 6));

        //添加至数组
        startStop.push(priorWeekFirstDay);
        startStop.push(priorWeekLastDay);

        return startStop;
    };

    /**
     * 得到上季度的起始日期
     * year 这个年应该是运算后得到的当前本季度的年份
     * month 这个应该是运算后得到的当前季度的开始月份
     * */
    this.getPriorSeasonFirstDay = function (year, month) {
        var quarterMonthStart = 0;
        var spring = 0; //春
        var summer = 3; //夏
        var fall = 6;   //秋
        var winter = 9; //冬
        //月份从0-11
        switch (month) {//季度的其实月份
            case spring:
                //如果是第一季度则应该到去年的冬季
                year--;
                month = winter;
                break;
            case summer:
                month = spring;
                break;
            case fall:
                month = summer;
                break;
            case winter:
                month = fall;
                break;

        };

        return new Date(year, month, 1);
    };

    /**
     * 得到上季度的起止日期
     * **/
    this.getPreviousSeason = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前月份0-11
        var currentMonth = currentDate.getMonth();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        //上季度的第一天
        var priorSeasonFirstDay = this.getPriorSeasonFirstDay(currentYear, currentMonth);
        //上季度的最后一天
        var priorSeasonLastDay = new Date(priorSeasonFirstDay.getFullYear(), priorSeasonFirstDay.getMonth() + 2, this.getMonthDays(priorSeasonFirstDay.getFullYear(), priorSeasonFirstDay.getMonth() + 2));
        //添加至数组
        startStop.push(priorSeasonFirstDay);
        startStop.push(priorSeasonLastDay);
        return startStop;
    };

    /**
     * 得到去年的起止日期
     * **/
    this.getPreviousYear = function () {
        //起止日期数组
        var startStop = new Array();
        //获取当前时间
        var currentDate = this.getCurrentDate();
        //获得当前年份4位年
        var currentYear = currentDate.getFullYear();
        currentYear--;
        var priorYearFirstDay = new Date(currentYear, 0, 1);
        var priorYearLastDay = new Date(currentYear, 11, 1);
        //添加至数组
        startStop.push(priorYearFirstDay);
        startStop.push(priorYearLastDay);
        return startStop;
    };

    return this;
})();






