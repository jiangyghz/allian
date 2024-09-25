//百度流量统计
if (window.location.protocol!='file:'&&!window.location.hostname.includes("127.0.0.1") && !window.location.hostname.includes("localhost")) {
    var hash = window.location.hostname.split('.').slice(1).join('.');
    document.domain = hash;
}
(function() {
    var hm = document.createElement("script"); //创建script标签
  //  hm.src = "//hm.baidu.com/hm.js?ab3baaa579f771d051a6b0baad5a8cfe";  //给script设置src属性
    var s = document.getElementsByTagName("script")[0]; //找到页面中第一个script标签
    s.parentNode.insertBefore(hm, s);  //把新建的script标签插入到最前面
})();
$(function () {
    loadjs((window.location.protocol=='file:'||window.location.hostname=='localhost'||!window.location.hostname.includes("127.0.0.1"))?"../js/colResizable-1.6.js":'/js/colResizable-1.6.js', function () {
        // setTimeout(function(){
        var option = {
            minWidth: 30
        };
        if ( $("#GridView1").length>0) {
            var width = $("#GridView1")[0].clientWidth;
            $("#GridView1")[0].style.width = width + "px";
            if ($('#GridView1 [data-field="MEMO"]').length > 0) {
                $('#GridView1 [data-field="MEMO"]')[0].style.width = "auto";
            }
            if ($('#GridView1 th[data-field="0"]').length > 0) {
                $('#GridView1 th[data-field="0"]')[0].style.textAlign="center";
            }
            $("#GridView1").colResizable(option);
            if ($('#GridView1 th[data-field="0"]').length > 0) {
                $('#GridView1 th[data-field="0"]')[0].style.width="36px";
            }
            $("#GridView1").css("cssText", "table-layout:fixed;word-break: break-all;");
        }

        // },1000)
    });
});

function loadjs(src, func) {
    //判断这个js文件存在直接执行回调
    var scripts = document.getElementsByTagName("script");
    for (i in scripts)
        if (scripts[i].src == src) return func();
    if (typeof func != "function") {
        console.log("param 2 is not a function!!");
        return false;
    }
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.src = src;
    var head = document.getElementsByTagName("head").item(0);
    head.appendChild(script);

    script.onload = function () {
        func();
    };
}
$(function(){
    //tabDefaultSelected(tid);
    $(".tab-title").on("click","li",tabCon);
    resetIframeHt();
    $(window).resize(resetIframeHt);
    $(".couponList").on("click","input[type='checkbox']",function(event){ postBackByObject(event)});//维修-工单-结算-优惠券勾选更新左侧数值

    var docHt = $(document).height();
    $(".popup-mask").height(docHt);//mask height 100%

    $(".addMask").click(addLoadingMask);//显示遮罩
    //$(".hideMask").click(removeLoadingMask);//隐藏遮罩

});

//图表相关
$(function(){
   //自定义radiobox绑定
    ($(".radio-list li>input").length>0 && $(".radio-list li>input").lpRadioBox());

    //表格划过变色
    setTrHighLight();

   //单选：按日期删选
    ($(".radio-list").length>0 && $(".radio-list").on("click","li",passDate));
    //页面一加载的时候默认填充日期文本框
    ($(".radio-list").length>0 && fillDate());

   //日期框change时联动单选
    $(".set_date .txt_date").blur(fillRadio);

});
function fillRadio(){
    var startDate,endDate;
    startDate = $(".set_date .txt_date:first").val();//当前手动更新后的时间开始值
    endDate = $(".set_date .txt_date:last").val(); //当前手动更新后的时间结束值
    var $ckdInput = $(".radio-list input[name='selectDate']:checked"); //选中的input

    //当后台未传入起止时间时，设置默认时间；
    (!startDate && (startDate = $ckdInput.attr("date")));
    (!endDate && (endDate = getToday()));

    radioIsChecked(startDate,endDate);//判断单选是否选中
}
function fillDate(){
    var startDate,endDate;//后台自定义值
    startDate = $(".set_date .txt_date:first").attr("start_time");//后台隐藏的开始值
    endDate = $(".set_date .txt_date:last").attr("end_time"); //后台隐藏的、结束值
    var $ckdInput = $(".radio-list input[name='selectDate']:checked"); //选中的input

    //当后台未传入起止时间时，设置默认时间；
    (!startDate && (startDate = $ckdInput.attr("date")));
    (!endDate && (endDate = getToday()));

    radioIsChecked(startDate,endDate);//判断单选是否选中
    //填充日期框
    $(".set_date .txt_date:first").attr({"value":startDate,"Text":startDate});
    $(".set_date .txt_date:last").attr({"value":endDate,"Text":endDate});

}


//联动单选选中效果
function radioIsChecked(startDate,endDate){
    var today = getToday(); //当天
    var $radio = $(".radio-list input");
    //默认先把所有的都取消选中
    $radio.attr("checked",false).parent("em").removeClass("radio_checked");
    //找到符合日期的项勾选
    for(var i = 0; i<$radio.length; i++){
        //后台传值在单选范围内
        if(startDate == $radio.eq(i).attr("date") && endDate == today){
            $radio.eq(i).attr("checked",true).parent("em").addClass("radio_checked");
        }
    }
}

function passDate(){
    var startDate = $(this).find("input").attr("date");  //获取开始日期为当前选中项
    $(".set_date .txt_date:first").val(startDate);
    var today = getToday(); //获取截止日期
    $(".set_date .txt_date:last").val(today);
}
//获取当天日期
function getToday(){
    var myDate = new Date();
    var y = myDate.getFullYear();    //获取完整的年份(4位,1970-????)
    var m = myDate.getMonth()+1;     //获取当前月份(0-11,0代表1月)
    m = m<10 ? ('0'+m) : m;             //月份保持两位数
    var d = myDate.getDate();        //获取当前日(1-31)
    d = d<10 ? ('0'+d) : d;
    var today = y+"-"+m+"-"+d;
    return today;
}
function setTrHighLight(){
   $(".table tbody tr").hover(function(){
       $(this).toggleClass("active");
   });
}



//动态生成遮罩
function addLoadingMask(){
    //新建loading遮罩
    var $loadingMask = $('<div class="loading-mask"><p class="loading-maskbg"></p><div class="loading"> <img src="../images/loading.gif" class="loading-pic" alt=""/><span class="loading-des">正在加载中......</span></div></div>')
    var ht = $(window).height();
    var wh = $(window).width();
    $(".loading-mask").height(ht).width(wh);
    $loadingMask.appendTo($("body"));

    //removeLoadingMaskAuto();
}
//移除遮罩
function removeLoadingMask(){
    $(".loading-mask").remove();
}
function removeLoadingMaskAuto(){
    var t = setTimeout(removeLoadingMask,2000);
}

//显示弹层；
function showPopupBox(){
 var o_popup = getClass('div','popup');
 o_popup[0].style.display = "block";
}
//关闭弹层；
function hidePopupBox(){
 var o_popup = getClass('div','popup');
 o_popup[0].style.display = "none";
}

//获得标签名为tagName,类名className的元素
function getClass(tagName,className){
     //当浏览器支持getElementsByClassName这个函数时
     if(document.getElementsByClassName){
         return document.getElementsByClassName(className);
     }else{
         var tags=document.getElementsByTagName(tagName);//获取标签
         var tagArr=[];//用于返回类名为className的元素
         for(var i=0;i < tags.length; i++){
             if(tags[i].className == className){
                 tagArr[tagArr.length] = tags[i];//保存满足条件的元素
             }
         }
         return tagArr;
     }
 }

//打印
//******* jq插件 printArea 打印 ********//
(function($) {
    $.fn.printArea = function() {
        var ele = $(this);
        var printCss = '';
        
        //获取原页面表格样式重新link在新打开的窗口中
        $(document).find("link").filter(function() {
            return $(this).attr("rel").toLowerCase() == "stylesheet";
        }).each(
            function() {
                printCss += '<link type="text/css" rel="stylesheet" href="' + $(this).attr("href") + '" >';
            });
		
		console.log("引入打印的样式表html是"+printCss);

        var printContent = '<div class="' + $(ele).attr("class") + '">' + $(ele).html() + '</div>';
        var windowUrl = 'about:blank';
        var uniqueName = new Date();
        var windowName = 'Print' + uniqueName.getTime();

        var wh = 0, ht = 0;
        //判读是否为ie浏览器,ie就控制窗口大小，其他浏览器自适应
        var is_IE = navigator.userAgent.toLowerCase();
        if (is_IE.indexOf("msie") > -1 || is_IE.indexOf("rv:11") > -1) {
            is_IE = true;
        }

        if(is_IE){
            wh = 595;
            ht = 842;
        }


        var printWindow = window.open(windowUrl, windowName, 'height='+ht+', width='+wh+', top=-99999, left=-99999');
        var BodyHtml = '<body>';
        var BodyEnd = "</body>";
        printCss = '<head>'+ printCss +'</head>' ;
        printWindow.document.write(printCss + BodyHtml + printContent + BodyEnd);
        printWindow.document.close();
        printWindow.focus();
        printWindow.print();
        printWindow.close();
    }
})(jQuery);
//打印
function print(msg){
    //若页面有多个打印任务，则先要清除掉页面上原有的print标签
    if($(".print").length){$(".print").remove();}

    //动态创建一个隐藏的div用来存放打印内容
    var o_print = $('<div class="print" style="display:none;"></div>');
    o_print.html(msg);//把需要打印的目标文档在当前页隐藏起来
    $("body").append(o_print)
    $(".print").printArea();
}



//tab选项卡；
function tabCon(){
    removeActive();

    //设置当前高亮样式
    $(".tab-title li").removeClass("active");
    $(this).addClass("active");
    var idx = $(this).index();
    $(".con").removeClass("active").eq(idx).addClass("active");

}

//tab选项卡默认选中；
function tabDefaultSelected(tid){
    //alert(tid);
    removeActive();

    tid = tid ? tid : 0;
    if(tid){
        $("#"+tid).addClass("active");
        $("#con_"+tid).addClass("active");

    }else{
        //通过索引默认选中第一项
        $(".tab-title li").first().addClass("active");
        $(".con").first().addClass("active");
    }
}

//移除高亮
function removeActive(){
    //去除所有高亮样式
    $(".tab-title li,.con").removeClass("active");
}

//重置iframe高度
function resetIframeHt(){
    var pHt = $(top.window).height();
    $(".conFrame").height(pHt -185);
}





//=================================== 自定义流量统计代码 ======================================//
//自定义流量统计
$(function () {
    $("body").on("click", "input[type='submit']", fnSetEventInfo);//记录postback操作,unload时不需要请求接口})
})
window.onload = fnPageLoad;
window.onunload = fnPageClose;
function strToJson(str) {
    //string to json
    if (!str) {
        var url = location.href;
        // var url = "h5-dfxk-list.html?toid=1083&username=liping&level=1";
    }
    var obj = {};
    url.replace(/([^?&]+)=([^?&]+)/g, function (s, v, k) {
        obj[v] = decodeURIComponent(k);
        return k + '=' + v;
    });
    return obj;
}
function fnPageLoad() {
    //页面加载的时候
    var tid = localStorage.getItem("tid") ? localStorage.getItem("tid") : "";
    var eventresource = localStorage.getItem("eventresource") ? localStorage.getItem("eventresource") : "load"; //判断事件来源 默认当前事件来源为onload,若为postback,则重置
    var eventval = localStorage.getItem("eventval") ? localStorage.getItem("eventval") : "页面载入";
    var pagename = location.href;
    var eidx = pagename.indexOf("?");
    pagename = pagename.substring(0, eidx);
    var sidx = pagename.lastIndexOf("/");
    pagename = pagename.substring(sidx + 1);
    localStorage.setItem("pagename", pagename);

    // var apiName = "data/getTid.json?eventresource"+eventresource+"&eventval="+eventval+"&tid="+tid+"&cid="+strToJson().cid+"&username="+strToJson().username+"&pagename="+pagename;
    // var apiName = "api/SavePageEvent.aspx?eventresource=" + eventresource + "&eventval=" + eventval + "&tid=" + tid + "&cid=" + strToJson().cid + "&username=" + strToJson().username + "&pagename=" + pagename;
    // $.get(apiName, function (res) {
    //     if (typeof res == "string") res = JSON.parse(res);
    //     if (res.errcode == 0) {
    //         localStorage.setItem("tid", res.tid);    //tid为当前页标识
    //         localStorage.removeItem("eventresource");
    //         localStorage.removeItem("eventval");
    //     }
    // })
}
function fnPageClose() {
    //页面关闭前
    var tid = localStorage.getItem("tid"); //从缓存中获取tid传递给后台
    var eventresource = localStorage.getItem("eventresource"); //从缓存中获取tid传递给后台
    if (eventresource != "postback") {
        // var apiName = "data/postTid.json?eventresource=unload?eventval=页面关闭&tid="+tid;
        // var apiName = "api/SavePageEvent.aspx?eventresource=unload&eventval=页面关闭&tid=" + tid;
        // $.post(apiName, function (res) { });//不需要等回调,直接提交
    }
}
function fnSetEventInfo() {
    //记录表单提交的事件来源信息（input submit 类型的）
    var eventval = $(this).val();
    localStorage.setItem("eventval", eventval);
    localStorage.setItem("eventresource", "postback");
}
//=================================== 自定义流量统计代码 ======================================//







