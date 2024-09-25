/**
 * Created by liping on 15-6-8.
 */
$(function () {
    //响应式设计框架变化
    setLayout();
    $(window).resize(setLayout);

    //主菜单控制：去除最后项二级列表下边框
    $(".sub-menu:last").addClass("e-noborder");

    //主菜单控制：二级列表显隐
    $(".menu").on("click", ".first-menu", controlSub);

    //主菜单控制：高亮选中项，切换右侧引导栏链接及文字
    $(".menu").on("click", ".second-menu", setGuideInfo);

    //主菜单控制：控制主菜单伸缩
    $(".btn-menu-toggle").on("click", fold_Toggle);

    //主菜单控制：控制主菜单显隐
    $(".btn-menu-toggle2").on("click", menuToggle);

    //顶部菜单控制
    $(".top-menu").on("mouseenter", ".btn", topSubListShow);
    $(".top-menu").on("mouseleave", $(this), topSubListHide);
    $(".top-sublist").on("click", "a", hideList)
    $(".ic-msg-count").text(countInfo);
});


function countInfo() {
    var len = $(".top-sublist li").length;
    return len;
}
function controlSub() {
    $(".menu>li").removeClass("open");
    var subIsOpen = $(this).find(".ic-left").hasClass("ic-down");
    //对应二级当前是展开的状态,点击收拢
    if (subIsOpen) {
        $(this).find(".ic-left").removeClass("ic-down").end().next(".sub-menu").slideUp();

        //对应二级当前是收拢的状态,点击展开
    } else {
        $(".ic-left").removeClass("ic-down");
        $(".sub-menu").hide();
        $(this).parent("li").addClass("open").find(".ic-left").addClass("ic-down").end().find(".sub-menu").slideDown();
    }

    setLayout();
}
function fold_Toggle() {
    //检测主菜单是否是折叠的状态，带fold是当前已折叠
    var hasfold = $("#container").hasClass("fold");
    if (hasfold) {
        $("#container").removeClass("fold");
        $(".history-open").find(".sub-menu").show();
        $(".menu>li").removeClass("history-open")
    } else {
        $("#container").addClass("fold");
        $(".ic-down").parents("li").addClass("history-open");
        $(".sub-menu").hide();
    }
}
function menuToggle() {
    $(".sidebar").slideToggle();
}
function setLayout() {
    var screenWh = $(window).width();
    var screenHt = $(window).height();
    var siderbarHt;
    //小屏幕菜单移动到顶部的效果
    //if(screenWh == "992" || screenWh < "992"){
    //    $("#container").removeClass("fold");
    //    $(".main").addClass("full-main");
    //    siderbarHt = $(".sidebar").height();
    //    $(".mainFrame").height(screenHt-siderbarHt-120)
    //}else{
    //    $(".sidebar").height(screenHt-79).show();
    //    $(".main").removeClass("full-main");
    //    siderbarHt = $(".sidebar").height();
    //    $(".mainFrame").height(siderbarHt-42)
    //}


    //菜单没有任何变化
    $(".sidebar").height(screenHt - 79);
    siderbarHt = $(".sidebar").height();
    $(".mainFrame").height(siderbarHt);//-42


    $(".sidebar").mCustomScrollbar();
}
function topSubListShow() {
    $(".top-menu .btn").removeClass("cur");
    $(".top-menu-sub").hide();
    $(this).addClass("cur").next(".top-menu-sub").show();
}
function topSubListHide() {
    //设置计时器，移开后3秒内若未进入列表区域，则自动关闭下拉窗
    var timr = setTimeout(hideList, 3000);
    $(".top-menu-sub").mouseenter(stopTimr).mouseleave(hideList);

    //停止计时器
    function stopTimr() {
        clearTimeout(timr);
        timr = null;
    }
}
function hideList() {
    //隐藏下拉列表
    $(".top-menu .btn").removeClass("cur");
    $(".top-menu-sub").hide();
}
function updateGuideSecond(ts) {
    //获得一级菜单text
    var $first = ts.parents(".sub-menu").prev(".first-menu");
    var first_text = $first.text();

    //获得当前点击的二级菜url及text
    var second_href = ts.attr("data-url");
    var second_text = ts.text();

    //把左侧菜单的链接及文字传到右侧引导栏
    $(".guideList li:first a").attr("href", second_href).find("span").text(first_text);
    $(".guideList li:last a").attr("href", second_href).text(second_text);

}
function hidePopupBox() {
    //关闭弹层显示；
    var $popup = $(".mainFrame").contents().find(".popup");
    $popup.remove();
}





//********************************************************************************************//


//menu菜单cookie设置
; $(function () {
    //设置菜单唯一索引，便于cookie根据索引记录当前选中项信息
    setMenuIdx();

    //页面一加载就尝试读取页面中的2个cookie，看是否存在
    var cookieVal = readCookie("firstIdx");//把一级菜单当前选中项索引当第一个cookie值存入，第一次加载默认0，即选中一级第一项
    first_menuCur(cookieVal, 1);
    var cookieVal2 = readCookie("secondIdx"); //把二级菜单当前选中项索引当第二个cookie值存入，第一次加载默认0，即选中二级第一项
    second_menuCur(cookieVal2, 1);
});

function setMenuIdx() {
    //设置一级菜单唯一索引
    var $Menu = $(".first-menu");
    for (var i = 0; i < $Menu.length; i++) {
        $Menu.eq(i).attr("data-idx", i);
    }
    //设置二级菜单唯一索引
    var $subMenu = $(".second-menu");
    for (var i = 0; i < $subMenu.length; i++) {
        $subMenu.eq(i).attr("data-idx", i);
    }
}
function first_menuCur(c, cl) {
    if (!cl) {
        writeCookie("firstIdx", c);
    }
    c = readCookie("firstIdx") ? readCookie("firstIdx") : c;
    removeAllActive(); //清除高亮样式
    var first_menuList = $(".first-menu");//遍历一级菜单
    for (var i = 0; i < first_menuList.length; i++) {
        if (i == c) {
            var ts = first_menuList.eq(i).parents("li");
            addFirstActive(ts)
        }
    }
}
function second_menuCur(c, cl) {
    if (!cl) {
        writeCookie("secondIdx", c);
    }
    c = readCookie("secondIdx") ? readCookie("secondIdx") : c;
    var second_menuList = $(".second-menu");//遍历二级菜单
    for (var i = 0; i < second_menuList.length; i++) {
        if (i == c) {
            second_menuList.eq(i).addClass("sub-active");//二级高亮
            var ts = second_menuList.eq(i);
            updateGuideSecond(ts);//切换右侧引导栏信息
            var ifmSrc;
            ifmSrc = second_menuList.eq(i).attr("data-url");//获取iframe框架页url
            //if(ifmSrc.indexOf(".") != -1){
            if(domainenter==0&&ifmSrc.indexOf("background")>-1){
                ifmSrc=ifmSrc.replace("background","porsche");
            }
            console.info(ifmSrc);
            try {
                if (brand != undefined && brand == "Motor") {
                    if (ifmSrc == "background/index.html#/market") {
                        ifmSrc = "background/#/markets";
                    }
                    if (ifmSrc == "background/index.html#/welcome") {
                        ifmSrc = "background/#/welcomes";
                    }
                    if (ifmSrc == "background/index.html#/report/MarketReport") {
                        ifmSrc = "background/#/reports/MarketReport";
                    }
                    if (ifmSrc == "background/index.html#/report/CliamReport") {
                        ifmSrc = "background/#/reports/CliamReport";
                    }
                }
            }
            catch (ex) {}
            if (ifmSrc) {
                $(".mainFrame").attr("src", ifmSrc); //记录当前iframe的src，刷新的时候保持停留在该页
            } else {
                return false;
            }

        } else {
            second_menuList.eq(i).removeClass("sub-active");
        }
    }
}
function removeAllActive() {
    $(".menu li").removeClass("active").find(".ic-left").removeClass("ic-down"); //去除一级菜单所有高亮样式
    $(".second-menu").removeClass("sub-active");//去除二级菜单所有高亮样式
}
function addFirstActive(ts) {
    //添加一级菜单高亮样式
    ts.addClass("active").find(".ic-left").addClass("ic-down");
}
function setGuideInfo() {
    var ts = $(this);
    //点击菜单高亮
    $(".menu>li").removeClass("active open");
    $(".second-menu").removeClass("sub-active");
    ts.addClass("sub-active").parent("li").parents("li").addClass("active");

    //点击菜单切换右侧引导栏链接及文字
    updateGuideSecond(ts);

    //记录当前高亮菜单的唯一索引当cookie存入
    var secondIdx = ts.attr("data-idx");
    var firstIdx = ts.parents("li").find(".first-menu").attr("data-idx");
    first_menuCur(firstIdx, 0)
    second_menuCur(secondIdx, 0);
}
function writeCookie(name, value, hours) {
    var expire = "";
    if (hours != null) {
        expire = new Date((new Date()).getTime() + hours * 3600000);
        expire = "; expires=" + expire.toGMTString();
    }
    //写cookie时务必设置cookie保存目录，防止多个同名值的出现
    document.cookie = name + "=" + escape(value) + expire + ";path=/";
}
function readCookie(name) {
    var cookieValue = "";
    var search = name + "=";
    if (document.cookie.length > 0) {
        var offset = document.cookie.indexOf(search);
        if (offset != -1) {
            offset += search.length;
            var end = document.cookie.indexOf(";", offset);
            if (end == -1) end = document.cookie.length;
            cookieValue = unescape(document.cookie.substring(offset, end))
        }
    }
    return cookieValue;
}
window.onload = function () {
    //添加监听事件。
    if (typeof window.addEventListener != "undefined")
        window.addEventListener("message", func, false);
    else if (typeof window.attachEvent != 'undefined')//兼容不支持addEventLinstener的IE。
        window.attachEvent("onmessage", func);
}

//监听事件回调函数。
function func(e) {
    if (e.data.type=="balance") {
        getBalance();
    }

}
function getBalance() {
    $.get('./showamount').then(function (res) {
        // consoloe.log(res);
        $("#balance").html(res);
    })
}
