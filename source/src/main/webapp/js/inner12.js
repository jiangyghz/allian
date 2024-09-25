//=============================== 百度流量统计 ===================================
(function () {
    var hm = document.createElement("script"); //创建script标签
    hm.src = "//hm.baidu.com/hm.js?ab3baaa579f771d051a6b0baad5a8cfe";  //给script设置src属性
    var s = document.getElementsByTagName("script")[0]; //找到页面中第一个script标签
    s.parentNode.insertBefore(hm, s);  //把新建的script标签插入到最前面
})();

//=============================== 内页基本操作 ===================================
$(function () {
    $(".tab-title").on("click", "li", tabCon);
    resetIframeHt();
    $(window).resize(resetIframeHt);
    $(".couponList").on("click", "input[type='checkbox']", function (event) { postBackByObject(event) }); //维修-工单-结算-优惠券勾选更新左侧数值
    var docHt = $(document).height();
    $(".popup-mask").height(docHt); //mask height 100%
    $(".addMask").click(addLoadingMask); //显示遮罩
    $(".select-car").on("click", showChooseType); //打开选车型大弹层
    $(".btn-closebox").on("click", hideChooseType); //关闭选车型大弹层
    ListenInputBox(); //监听输入框，清除默认文字，加高亮样式
    $(".car-infolist").on("click", "li", checkedCarType); //【资料页弹层】选中列表项赋值
    $(".pop-choose-car").on("click", ".ic-closeself", hideChoosePop); //【资料页弹层】关闭小弹层
    ($("body.srhinfo-page").length > 0 && drawHotGroupName(data_groupName)); //【资料页】最新车型
    ($("body.result-page").length > 0 && getCarTypeOnLoad()); //【结果页】加载已选车型
    $(".result-page").on("click", ".btn-srh-bychoose", { type: 1 }, drawKeyWordsOnClick); //通过车型搜索
    $(".result-page").on("click", ".btn_srh_bysrhtxt", { type: 2 }, drawKeyWordsOnClick); //通过输入内容搜索
    $(".sidebar-srhresult").on("click", "a", { type: 3 }, drawKeyWordsOnClick); //通过侧边栏资料类别搜索
    $(".btn-clearkeywords").click(clearKeyWords); //【资料页】清除关键词
    $(".keywords-list").on("click", ".ic-removekw", removeTsKeyWords); //【资料页】删除当前关键词
    $(".btn_srh_byvin").on("click", checkCarTypeAuto); //【资料页】按vin码自动确定车型
    $(".srh-vin").on("focus", ".txt_nobg", removeErrorTips); //【资料页】获得焦点时移除错误提示；
    $(".choose-brand").on("click", { popname: "pop-choose-brand", basedobj: $('.choose-brand'), defaultshow: true }, showChoosePop); //【资料页】点击打开选择品牌小弹层
    $(".brand-byabc").on("click", "a", drawBrandList); //【资料页】tab页卡：按字母删选车型品牌
    $(".choose-company").on("click", { popname: "pop-choose-company", basedobj: $('.choose-brand') }, showChoosePop); //【资料页】点击打开选择厂家小弹层
    $(".choose-type").on("click", { popname: "pop-choose-type", basedobj: $('.choose-company') }, showChoosePop); //【资料页】点击打开选择车系小弹层
    $(".choose-group").on("click", { popname: "pop-choose-group", basedobj: $('.choose-type') }, showChoosePop); //【资料页】点击打开选择车组小弹层
    $(".choose-pl").on("click", { popname: "pop-choose-pl", basedobj: $('.choose-group') }, showChoosePop); //【资料页】点击打开选择排量小弹层
    $(".choose-year").on("click", { popname: "pop-choose-year", basedobj: $('.choose-pl') }, showChoosePop); //【资料页】点击打开选择型号年款小弹层
    $(".btn-srh-vehicle-byvin").click(srhVehicleByVin); //【资料页】按vincode 查询 车型参数
    $(".btn-srh-vehicle-bycartype").click(srhVehicleByCartype); //【资料页】按车型 查询 车型参数
    ($(".btn-srh-vehicle-bycartype").length > 0 && showInfo()); //首次进入车型参数查询页面
    $(".btn-srh-error-bycartype").click(srhErrorCodeByCartype); //【资料页】按故障代码 查询 故障代码信息
    ($(".btn-srh-error-bycartype").length > 0 && DrawErrorCodeHtml('load', null)); //首次进入错误代码信息查询页面
    ($(".showmore").length > 0 && cutString()); //车型字符串截取
    ($(".onfocus-showbig").length > 0 && drawThumbList()); //画img
    $(".onfocus-showbig").mouseenter(showBigImg).mouseleave(removeBigImg); //鼠标悬浮状态控制显示大图
    $(".onfocus-showbig").on("click", "img", toggleThumb); //点击切换缩略图,改变当前可见cls,且让当前img移动到最后
    ($(".autocomplete_name").length>0 && controlAutoComplete("autocomplete_name",data_name));//自动匹配名称
    ($(".autocomplete_type").length>0 && controlAutoComplete("autocomplete_type",data_type));//自动匹配车型
    ($(".autocomplete_pbrand").length>0 && controlAutoComplete("autocomplete_pbrand",data_pbrand));//自动匹配品牌
    $(".sel-groupname").on("change", getPlByAjax); //根据所选车组获取对应排量信息
    $(".sel-pl").on("change", function () {
        $(this).siblings(".hidden-txt").val($(this).val());
    }); //select 值赋给隐藏域
});
//====================================== 由于计时器作用域问题，只能放一起 ==========================================
$(function () {
    var timr;
    $(".showmore").mouseenter(showMore).mouseleave(isHideMore); //鼠标悬浮状态控制显示全部
    function isHideMore() {
        //设置计时器，移开后3秒内若未进入列表区域，则自动关闭下拉窗
        startTimr();
        $(".showmore-con").mouseenter(stopTimr).mouseleave(hideMore);
    }

    function startTimr() {
        timr = setTimeout(hideMore, 500);
    }

    //停止计时器
    function stopTimr() {
        clearTimeout(timr);
        timr = null;
    }

    function showMore() {
        stopTimr();
        var tstxt = $(this).data("txt"); //获取字符长度

        //有截图的字符串显示全部
        if (tstxt.length > 25) {
            //新建一个弹层显示全部内容
            hideMore(); //先清除先前的
            var $more = $('<div class="showmore-con">' + tstxt + '</div>');
            var left = $(this).offset().left;
            var ht = $(this).height();
            var top = $(this).offset().top + ht + 10;
            $("body").append($more);
            $(".showmore-con").css({ "top": top, "left": left })
        }
    }

    function hideMore() {
        $(".showmore-con").remove();
    }
});


//====================================== 内页：资料相关 ==========================================
/* ======= ajax start ======= */
//通过vincode获取车型
function checkCarTypeAuto() {
    var vincode = $(this).prev("input").val();

    //==============ajax前后台交互：通过vincode获取车型
    $.ajax({
        type: "POST",
        url: "ApiMethod.aspx",
        data: { "vinCode": vincode, "dtype": "json" },
        success: function (data) {
            if (!data) {
                removeErrorTips(); //移除错误提示
                var $tips = '<p class="error-tips"><em class="ic-error"></em>vin码不存在！</p>';
                $(".srh-byvin").append($tips); //添加错误提示：vin码不存在
            } else {
                removeErrorTips(); //移除错误提示
                $(".btn-srh-bychoose").removeClass("btn-theme1_disabled");
                jumpToRusult(data); //跳转到结果页
            }
            filledAutoByVin(data);

        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
function jumpToRusult(data) {
    if (typeof data == "string") {
        data = $.parseJSON(data);
    }

    var key = ""; //搜索车型参数
    if (data[0].brand) { key += "brand=" + data[0].brand; } //品牌
    if (data[0].brandName) { key += "&brandname=" + data[0].brandName; } //厂家
    if (data[0].familyName) { key += "&classify=" + data[0].familyName; } //车系
    if (data[0].groupName) { key += "&groupName=" + data[0].groupName; } //车组
    if (data[0].engineDesc) { key += "&pl=" + data[0].engineDesc; } //排量
    var url = 'SearchResult.aspx?' + key;
    window.location.href = url; //跳转到搜索结果列表页
}
//通过首字母获取品牌
function drawBrandList() {
    tabByABC_Ht($(this)); //按字母删选品牌tab高亮
    var pym = $(this).data("pym"); //获取当前选择的字母范围
    var $obj = $(".pop-choose-brand .car-infolist"); //需要替换html的弹层列表容器
    $obj.html("");

    //==============ajax前后台交互：通过首字母获取品牌
    $.ajax({
        type: "POST",
        url: "ApiMethod.aspx",
        data: { "pym": pym },
        success: function (data) {
            //替换列表内容
            data = $.parseJSON(data);
            drawHtml(data, 1, $obj);
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//获取厂家
function drawCompanyList() {
    var $obj = $(".pop-choose-company .car-infolist"); //需要替换html的弹层列表容器
    $obj.html("");
    var brand = $(".choose-brand").find("span").text(); //获取已选品牌

    //==============ajax前后台交互：获取厂家
    $.ajax({
        type: "GET",
        url: "ApiMethod.aspx",
        data: { "brand": brand },
        success: function (data) {
            //替换列表内容
            data = $.parseJSON(data);
            drawHtml(data, 2, $obj);
            var brandnameauto = $(".pop-choose-company").find("li").eq(0).text();
            $(".choose-company").attr("data-checked", true).find(".sim-select").text(brandnameauto).attr("data-isdefval", "0").prev(".txt_storeval").val(brandnameauto); //自动匹配第一项
            $(".btn-srh-bychoose").removeClass("btn-theme1_disabled");
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//获取车系
function drawTypeList() {
    var $obj = $(".pop-choose-type .car-infolist"); //需要替换html的弹层列表容器
    $obj.html("");
    var brandname = $(".choose-company").find("span").text(); //获取已选厂家

    //==============ajax前后台交互：获取车系
    $.ajax({
        type: "POST",
        url: "ApiMethod.aspx",
        data: { "brandname": brandname },
        success: function (data) {
            //替换列表内容
            data = $.parseJSON(data);
            drawHtml(data, 3, $obj);
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//获取车组
function drawGroupList() {
    var $obj = $(".pop-choose-group .car-infolist"); //需要替换html的弹层列表容器
    $obj.html("");
    var brandname = $(".choose-company").find("span").text(); //获取已选厂家
    var classify = $(".choose-type").find("span").text(); //获取已选车系

    //==============ajax前后台交互：获取车组
    $.ajax({
        type: "POST",
        url: "ApiMethod.aspx",
        data: { "brandname": brandname, "classify": classify },
        success: function (data) {
            //替换列表内容
            data = $.parseJSON(data);
            drawHtml(data, 4, $obj);
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//获取排量
function drawPlList() {
    var $obj = $(".pop-choose-pl .car-infolist"); //需要替换html的弹层列表容器
    $obj.html("");
    var brandname = $(".choose-company").find("span").text(); //获取已选厂家
    var classify = $(".choose-type").find("span").text(); //获取已选车系
    var groupName = $(".choose-group").find("span").text(); //获取已选车组

    //==============ajax前后台交互：获取排量
    $.ajax({
        type: "POST",
        url: "ApiMethod.aspx",
        data: { "brandname": brandname, "classify": classify, "groupName": groupName },
        success: function (data) {
            //替换列表内容
            if (!data) {
                data = "[]";
            }
            data = $.parseJSON(data);
            drawHtml(data, 5, $obj);
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//获取型号年款
function drawYearList() {
    var $obj = $(".pop-choose-year .car-infolist"); //需要替换html的弹层列表容器
    $obj.html("");
    var groupName = $(".choose-group").find("span").text(); //获取已选车组
    var pl = $(".choose-pl").find("span").text(); //获取已选排量

    //==============真实环境获取data;
    $.ajax({
        type: "POST",
        url: "EpcGetCarName.aspx",
        data: { "groupName": groupName, "pl": pl },
        dataType: "json",
        success: function (data) {
            //替换列表内容
            //var data = '[{"carname":"宝来BORA 1.6轿车"}]';
            //if(typeof data == "string"){data = $.parseJSON(data);}
            console.log(typeof data);
            console.log("后台返回的型号年款数据为：" + data);
            drawHtml(data, 6, $obj);
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//删除关键词列表
function removeTsKeyWords() {
    var carinfo = getHideInput(); //获取隐藏域input存放的搜索条件
    var len = $(".keywords-list li").length;
    $(this).parent("li.type").remove(); //移除当前关键词
    var type = $(this).parent("li").data("type");
    //删除车型选择关键词
    if (type == 1) {
        $(".car-brand,.car-brandname,.car-classify,.car-group,.car-pl").empty(); //清空当前车型行
        carinfo.brand = carinfo.brandname = carinfo.classify = carinfo.groupName = carinfo.pl = "";

        //删除输入框搜索关键词
    } else if (type == 2) {
        carinfo.searchTxt = "";
        $(".srh input.txt_nobg").val("");

        //删除资料类别选择关键词
    } else if (type == 3) {
        carinfo.type2 = carinfo.type = "";
    }
    carinfo = JSON.stringify(carinfo); //不能直接一个json扔给后台，转成string后才行
    updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了
    addLoadingMask(); //增加遮罩

    //==============ajax前后台交互：删除关键词列表
    $.ajax({
        type: "POST",
        url: "searchresult.aspx",
        data: { "carinfo": carinfo },
        success: function (data) {
            //替换列表内容
            updateResult(data);
            updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
    if (len == 1) {
        $(".btn-clearkeywords").trigger("click");
    }
}
//清空关键词
function clearKeyWords() {
    var carinfo = "{\"brand\":\"\",\"brandname\":\"\",\"classify\":\"\",\"groupName\":\"\",\"pl\":\"\",\"type\":\"\",\"type2\":\"\",\"searchTxt\":\"\",\"page\":\"0\"}";  //page属性存在时必须给值，不然接口获取数据会报错
    updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了

    //==============ajax前后台交互：清空关键词
    $.ajax({
        type: "POST",
        url: "searchresult.aspx",
        data: { "carinfo": carinfo },
        success: function (data) {
            data = "";
            $(".keywords-list").empty(); //清空关键词列表
            $(".car-brand,.car-brandname,.car-classify,.car-group,.car-pl").empty(); //清空当前车型
            $(".techinfo").html(data); //替换搜索结果列表
            $("p.info-count span.ft-hlight").text("0"); //数据总条数渲染到页面
            data = data.split("");
            drawPage(data); //分页
        },
        error: function () {
            alert("sorry,出错了！");
        }
    });
}
//点击关键词局部刷新结果页及关键词列表
function drawKeyWordsOnClick(event) {
    var type, $ts, carinfo, kwords;
    type = event.data.type; //获取当前选择关键词类型：type:1 车型选择搜索； type:2 输入框搜索； type3:资料类别搜索
    $ts = $(this);

    //获取当前关键词车型信息
    carinfo = getHideInput();
    //根据车型来搜索
    if (type == 1) {
        removeErrorTips(); //移除错误提示
        //判断当前按钮样式是否为disabled 是的话就不让关闭
        var isdisabled = $ts.hasClass("btn-theme1_disabled");
        if (!isdisabled) {
            hideChooseType(); //关闭车型选择弹层；
            addLoadingMask(); //增加遮罩
            kwords = getCarInfo("click").cartypekeyword; //关键词
            carinfo = getCarInfo("click"); //更新搜索条件
            carinfo = JSON.stringify(carinfo); //传递string格式数据给后台

            //==============ajax前后台交互：选择车型来搜索
            $.ajax({
                type: "POST",
                url: "searchresult.aspx",
                data: { "carinfo": carinfo },
                success: function (data) {
                    updateResult(data); //替换列表结果
                    fillSelectedCarType("click"); //自动填充已选车型
                    updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了
                },
                error: function () {
                    alert("sorry,出错了！");
                }
            });
        }


        //根据搜索输入框内容搜搜
    } else if (type == 2) {
        hideChooseType(); //关闭车型选择弹层；
        addLoadingMask(); //增加遮罩
        if ($ts.prev("input").data("defval") != $ts.prev("input").val()) {
            kwords = carinfo.searchTxt = $ts.prev("input").val(); //非默认值时替换成输入框值
        }
        carinfo = JSON.stringify(carinfo); //传递字符串格式的数据给后台

        //==============ajax前后台交互：输入框内容搜索
        $.ajax({
            type: "POST",
            url: "searchresult.aspx",
            data: { "carinfo": carinfo },
            success: function (data) {
                updateResult(data); //替换列表结果
                updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了
            },
            error: function () {
                alert("sorry,出错了！");
            }
        });


        //点击侧边栏资料类别关键字搜索
    } else if (type == 3) {
        hideChooseType(); //关闭车型选择弹层；
        addLoadingMask(); //增加遮罩
        //清空所有高亮样式
        $(".sidebar-srhresult a").removeClass("active");
        $(this).addClass("active"); //当前项高亮
        var kwords1, kwords2, ts_type;
        ts_type = $ts.data("type"); //获取当前点击对象菜单类型，type:1 一级菜单； type:2 二级菜单；

        //一级
        if (ts_type == "1") {
            kwords1 = $ts.data("name");
            kwords2 = "";

            //二级
        } else if (ts_type == "2") {
            var $prev_a = $ts.parents("ul.info-list").prev("h3").find("a");
            $prev_a.addClass("active");
            kwords1 = $prev_a.data("name");
            kwords2 = $ts.data("name");
        }
        kwords = kwords2 || kwords1;
        carinfo.type = kwords1;
        carinfo.type2 = kwords2;
        carinfo = JSON.stringify(carinfo); //传递字符串格式的数据给后台

        //==============ajax前后台交互：点击侧边栏资料类别关键字搜索
        $.ajax({
            type: "POST",
            url: "searchresult.aspx",
            data: { "carinfo": carinfo },
            success: function (data) {
                //替换列表结果
                updateResult(data);
                updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了
            },
            error: function () {
                alert("sorry,出错了！");
            }
        });
    }

    (kwords && updateKwords(type, kwords)); //带关键字的话就新增或替换
    return false; //阻止a标签href默认链接跳转
}
//前端分页控件(data_arr是array格式obj)
function drawPage(data_arr) {
    var recordcount;
    //获取到后台的数组为空
    if (data_arr.length == 0) {
        recordcount = 0;
        $("p.info-count span.ft-hlight").text(recordcount); //数据总条数渲染到页面
        //分页控件
        $(".pagerArea").cypager({
            pg_size: 10, //每页显示条数
            pg_nav_count: 8, //导航栏显示页码最大个数
            pg_total_count: recordcount, //数据条数
            pg_prev_name: '前页',
            pg_next_name: '后页'
        });

    } else {
        recordcount = data_arr[0].recordcount; //数据总条数
        //分页控件
        $(".pagerArea").cypager({
            pg_size: 10, //每页显示条数
            pg_nav_count: 8, //导航栏显示页码最大个数
            pg_total_count: recordcount, //数据条数
            pg_prev_name: '前页',
            pg_next_name: '后页',
            pg_call_fun: function (count) {  //点击事件
                var carinfo = getHideInput();
                carinfo.page = parseInt(count) - 1;
                carinfo = JSON.stringify(carinfo);
                //上下翻页的时候
                $.ajax({
                    type: "POST",
                    url: "searchresult.aspx",
                    data: { "carinfo": carinfo }, //(后台第一页的索引默认是0，该控件是从1开始的，所以要调后台当前页，即要在该控件当前页减1)
                    success: function (data) {
                        data = $.parseJSON(data);
                        drawResultHtml(data); //拼接字符串
                    },
                    error: function () {
                        alert("sorry,出错了！");
                    }
                });
            }
        });
    }
    $("p.info-count span.ft-hlight").text(recordcount); //数据总条数渲染到页面
}
//根据vincode查询参数
function srhVehicleByVin() {
    var val = ($(".con-side .srh-vin .txt_nobg").val() != "输入17位VIN码") ? $(".con-side .srh-vin .txt_nobg").val() : "";
    if (val) {
        var postval = '{"name":"根据vincode查询参数","type":"0","val":"' + val + '"}';   //type:0 根据vincode查询参数
        getVehicleInfoByajax(postval); //根据vincode 获取车型参数信息
    }
}
//根据车型年款查询参数
function srhVehicleByCartype() {
    var val = ($(".choose-year .txt_nobg").attr("data-isdefval") == 0) ? $(".choose-year .txt_nobg").text() : "";
    removeErrorTips(); //移除错误提示
    if (!val) {
        var $tips = '<p class="error-tips"><em class="ic-error"></em>sorry，请选择型号年款后查询！</p>';
        $(this).after($tips); //添加错误提示
        return;
    }
    var postval = '{"name":"根据车型查询参数","type":"1","val":"' + val + '"}'; //type:1 根据车型查询参数
    getVehicleInfoByajax(postval); //根据cartype 获取车型参数信息
}
function srhErrorCodeByCartype() {
    var errorcode = $(".srh-errorcode .txt_nobg").attr("data-defval") == $(".srh-errorcode .txt_nobg").val() ? false : $(".srh-errorcode .txt_nobg").val();
    var brandname = ($(".choose-company .txt_nobg").attr("data-isdefval") == 0) ? $(".choose-company .txt_nobg").text() : "";
    removeErrorTips(); //移除错误提示
    if (!errorcode) {
        var $tips = '<p class="error-tips"><em class="ic-error"></em>sorry，请输入错误代码后查询！</p>';
        $(this).after($tips); //添加错误提示
        return;
    }
    var postval = '{"errorcode":"' + errorcode + '","brandname":"' + brandname + '"}'; //传入故障代码和厂家
    getErrorCodeInfoByajax(postval); //根据品牌厂家 获取故障代码信息
}
//获取车型车型参数
function getVehicleInfoByajax(postval) {
    $.ajax({
        "type": "POST",
        "url": "ZDBSXY.aspx",
        "data": { "data": postval }, //【1】：'{"name":"根据vincode查询参数","type":"0","val":'+val+'}'    ;【2】：'{"name":"根据车型查询参数","type":"1","val":'+val+'}'
        "dataType": "json",
        "beforeSend": function () {
            var html = '<div class="con-main-info">' +
						'<h2 class="ft-theme9 ft-cr1">车型信息</h2>' +
						'<p class="vehicle-result-tips"><img src="images/loading.gif" class="loading-pic" alt=""/><span class="loading-des">正在加载中......</span></p>' +
						'</div>';
            $(".con-main").html(html);
        },
        "success": function (data) {
            console.log(data);
            DrawVehicleHtml("click", data);
        },
        "error": function () {
            console.log("抱歉，提交失败！");
        }
    });

}
//获取故障代码信息（通过错误代码  和 品牌厂家查询）
function getErrorCodeInfoByajax(postval) {
    $.ajax({
        "type": "POST",
        "url": "main-SearchBugCodeInfo.aspx",        //故障代码信息查询接口
        "data": { "data": postval }, //'{"errorcode":"'+errorcode+'","brandname":"'+brandname+'"}'; //传入故障代码和厂家
        "dataType": "json",
        "beforeSend": function () {
            var html = '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">故障代码信息</h2>' +
                '<p class="errorcode-result-tips"><img src="images/loading.gif" class="loading-pic" alt=""/><span class="loading-des">正在加载中......</span></p>' +
                '</div>';
            $(".con-main").html(html);
        },
        "success": function (data) {
            DrawErrorCodeHtml("click", data);
        },
        "error": function () {
            console.log("抱歉，提交失败！");
        }
    });
}
/* ======= ajax end ======= */


//页面一加载获取后台传入的默认关键词搜索信息，有传值的话就模拟点击3种类型的关键词搜索
function getCarTypeOnLoad() {
    drawKeyWordsOnLoad(1); //填充关键词类型1：车型搜索
    drawKeyWordsOnLoad(2); //类型2：输入框搜索
    drawKeyWordsOnLoad(3); //类型3：资料类别搜索
    updateResult(data_list);
}
//拼接结果列表字符串(data获取到的是obj格式)
function drawResultHtml(data) {
    var resultArr = [];
    var html = "";
    for (var i = 0; i < data.length; i++) {
        var filename = data[i].filename.toLowerCase();
        var picno = data[i].picno;
        var isvideo = filename.indexOf(".mp4") > -1 ? true : false;
        if (isvideo) {
            html = '<dl class="techinfo-list">' +
                '<dt><em class="ic-video"></em><a href="articleshowdms.aspx?tid=' + data[i].tid + '" target="mainFrame">' + data[i].name + '</a></dt>' +
                '<dd class="cols1"><em class="ic-cartype"></em>' + data[i].brand + '-' + data[i].cartype + '</dd>' +
                '<dd class="cols2"><em class="ic-repair"></em>' + data[i].parts + '</dd>' +
                '<dd class="cols3"><em class="ic-doc"></em>' + data[i].type + '-' + data[i].type2 + '</dd>' +
                '<dd class="cols4"><em class="ic-eye"></em>' + data[i].rcount + '</dd>' +
                '</dl>';
        } else {
            html = '<dl class="techinfo-list">' +
                '<dt><a href="imgshow.html?filename=' + data[i].filename + '&picno='+ picno +'">' + data[i].name + '</a></dt>' +
                '<dd class="cols1"><em class="ic-cartype"></em>' + data[i].brand + '-' + data[i].cartype + '</dd>' +
                '<dd class="cols2"><em class="ic-repair"></em>' + data[i].parts + '</dd>' +
                '<dd class="cols3"><em class="ic-doc"></em>' + data[i].type + '-' + data[i].type2 + '</dd>' +
                '<dd class="cols4"><em class="ic-eye"></em>' + data[i].rcount + '</dd>' +
                '</dl>';
        }

        resultArr.push(html);
    }

    var resultList = resultArr.join("");
    $(".techinfo").html(resultList); //替换搜索结果列表
}
//更新结果列表
function updateResult(data) {
    var datatype = typeof data; //判断data类型
    if (datatype == "object") {
        //data 不为空
        if (data.length > 0) {
            drawResultHtml(data); //拼接字符串

            //data为空[]
        } else {
            $(".techinfo").html(""); //替换搜索结果列表
            $("p.info-count span.ft-hlight").text("0"); //数据总条数渲染到页面
        }
        drawPage(data); //分页


    } else if (datatype == "string") {
        //data不为空
        if (data) {
            data = $.parseJSON(data);
            drawResultHtml(data); //拼接字符串

            //data为空“”
        } else {
            $(".techinfo").html(""); //替换搜索结果列表
            $("p.info-count span.ft-hlight").text("0"); //数据总条数渲染到页面
            data = data.split(""); //string to array
        }
        drawPage(data); //分页
    }

    removeLoadingMask();
}
//更新车型参数查询结果
function DrawVehicleHtml(action, data) {
    var html = "";
    //还未选择车型（首次进页面）
    if (action == "load") {
        html = '<div class="con-main-info">' +
            '<h2 class="ft-theme9 ft-cr1">车型信息</h2>' +
            '<p class="vehicle-result-tips">请选择车型后查询</p>' +
            '</div>';

        //点击左侧选择车型后
    } else {
        //后台返回车型参数为空
        if (data.length == 0) {
            html = '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">车型信息</h2>' +
                '<p class="vehicle-result-tips">抱歉，暂无数据</p>' +
                '</div>';

            //后台返回车型参数
        } else {
            var ishasimg = '';
            if (data[0].JFYKWZ) {
                ishasimg = '<h3 class="ft-theme10">加放油口位置</h3><div class="con-box txt-c"><img src="http://epc.hks360.com:88/' + data[0].JFYKWZ + '" alt=""/></div>';
            }

            html = '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">车型信息</h2>' +
                '<div class="con-box">' +
                '<table class="table-theme2">' +
                '<tr><th class="txt-r" style="width:20%;">品牌</th><td>' + data[0].BrandName + '</td><th class="txt-r" style="width:20%;">车系</th><td>' + data[0].FamilyName + '</td></tr>' +
                '<tr><th class="txt-r">车组</th><td>' + data[0].GroupName + '</td><th class="txt-r">排量</th><td>' + data[0].PL + '</td></tr>' +
                '<tr><th class="txt-r">年款</th><td>' + data[0].Nian + '</td><th class="txt-r">车型</th><td>' + data[0].VehicleName + '</td></tr>' +
                '<tr><th class="txt-r">变速箱型号</th><td>' + data[0].BSXXH + '</td><th class="txt-r">变速箱</th><td>' + data[0].BSX + '</td></tr>' +
                '<tr><th class="txt-r">档位数</th><td>' + data[0].DWS + '</td><th class="txt-r"></th><td></td></tr>' +
                '</table>' +
                '</div>' +
                '</div>' +

                '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">技术参数</h2>' +
                '<h3 class="ft-theme10">保养方式</h3>' +
                '<div class="con-box">' +
                '<table class="table-theme2">' +
                '<tr><th class="txt-c" colspan="2">自然重力</th><th class="txt-c" colspan="3">循环清洗</th><th class="txt-c" colspan="5">深度保养</th></tr>' +
                '<tr><td class="txt-c">加注量</td><td class="txt-c">工时</td><td class="txt-c">是否允许</td><td class="txt-c">12L</td><td class="txt-c">工时</td><td class="txt-c">垫片</td><td class="txt-c">滤网</td><td class="txt-c">垫片胶</td> <td class="txt-c">清洗剂</td><td class="txt-c">工时</td></tr>' +
                '<tr><td class="txt-c">' + data[0].ZRZLJZ + '</td><td class="txt-c">' + data[0].ZRZLGS + '</td><td class="txt-c">' + data[0].XHQX_SFYX + '</td><td class="txt-c">' + data[0].XHQX_12L + '</td><td class="txt-c">' + data[0].XHQX_GS + '</td><td class="txt-c">' + data[0].SDYH_DP + '</td><td class="txt-c">' + data[0].SDYH_HLW + '</td><td class="txt-c">' + data[0].SDYH_DPJ + '</td><td class="txt-c">' + data[0].SDYH_QXJ + '</td><td class="txt-c">' + data[0].SDYH_GS + '</td></tr>' +
                '</table>' +
                '</div>' +
                '<h3 class="ft-theme10">其他参数</h3>' +
                '<div class="con-box">' +
                '<table class="table-theme2">' +
                '<tr><th class="txt-r" style="width:20%;">更换里程</th><td>' + data[0].GHLC + ' km</td><th class="txt-r" style="width:20%;">总容量</th><td>' + data[0].ZRL + '</td></tr>' +
                '<tr><th class="txt-r">加注量</th><td>' + data[0].JZL + '</td><th class="txt-r">加放油口扭力</th><td>' + data[0].JFYKNL + '</td></tr>' +
                '</table>' +
                '</div>' +
                ishasimg +
                '</div>' +

                '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">设备</h2>' +
                '<div class="con-box">' +
                '<table class="table-theme2">' +
                '<tr><th class="txt-r" style="width:20%;">自动换油机</th><td>' + data[0].ZDHYJ + '</td><th class="txt-r" style="width:20%;">重力更换加油壶</th><td>' + data[0].ZLGHJYH + '</td></tr>' +
                '</table>' +
                '</div>' +
                '</div>' +

                '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">商品</h2>' +
                '<div class="con-box">' +
                '<table class="table-theme2">' +
            //'<tr><th class="txt-r" style="width:20%;">费比油号-1L</th><td>'+data[0].FBYL1L+'</td><th class="txt-r" style="width:20%;">费比油号-5L</th><td>'+data[0].FBYL5L+'</td></tr>'+
                '<tr><th class="txt-r">垫片</th><td>' + data[0].DP + '</td><th class="txt-r">滤网</th><td>' + data[0].LW + '</td></tr>' +
                '<tr><th class="txt-r">垫片胶</th><td>' + data[0].DPJ + '</td><th class="txt-r">清洗剂</th><td>' + data[0].QXJ + '</td></tr>' +
                '<tr><th class="txt-r">备注</th><td>' + data[0].BZ + '</td><th class="txt-r"></th><td></td></tr>' +
                '</table>' +
                '</div>' +
                '</div>' +
                '</div>';
        }
    }
    $(".con-main").html(html);
}
function DrawErrorCodeHtml(action, data) {
    var html = "";
    //还未输入故障代码（首次进页面）
    if (action == "load") {
        html = '<div class="con-main-info">' +
            '<h2 class="ft-theme9 ft-cr1">故障代码信息</h2>' +
            '<p class="errorcode-result-tips">请输入故障代码后查询</p>' +
            '</div>';

        //点击左侧选择车型后
    } else {
        //后台返回错误代码信息为空
        if (data.length == 0) {
            html = '<div class="con-main-info">' +
                '<h2 class="ft-theme9 ft-cr1">故障代码信息</h2>' +
                '<p class="errorcode-result-tips">抱歉，暂无数据</p>' +
                '</div>';

            //后台返回错误代码信息
        } else {
            console.log(data);
            var related_list = "";
            for (var k = 0; k < data[0].pname.length; k++) {
                related_list += '<li><a href="' + data[0].pname[k].url + '" class="btn-buy">购买</a>' + data[0].pname[k].name + '</li>';
            }


            html = '<div class="con-main-info">' +
                    '<h2 class="ft-theme9 ft-cr1">故障代码信息</h2>' +
                    '<div class="con-box con-box-errorcode">' +
                        '<table class="table-theme2">' +
                            '<tr><th class="txt-r" style="width:20%;">汽车品牌</th><td>' + data[0].brandname + '</td></tr>' +
                            '<tr><th class="txt-r">故障代码</th><td>' + data[0].bugcode + '</td></tr>' +
                            '<tr><th class="txt-r">故障含义</th><td>' + data[0].buginfo + '</td></tr>' +
                            '<tr><th class="txt-r">系统范畴</th><td>' + data[0].knowledge + '</td></tr>' +
                            '<tr><th class="txt-r">背景知识</th><td>' + data[0].category + '</td></tr>' +
                            '<tr>' +
                                '<th class="txt-r">相关配件</th>' +
                                '<td>' +
                                    '<ul class="related-parts-vertical">' + related_list + '</ul>' +
                                '</td>' +
                            '</tr>' +
                        '</table>' +
                    '</div>' +
                '</div>';
        }
    }
    $(".con-main").html(html);
}
function showInfo() {
    var startidx = location.href.indexOf("VIN=");
    if (startidx > 0) {
        var vincode = location.href.substr(startidx + 4, 17); //取17位vincode码
        $(".con-side .srh-vin .txt_nobg").val(vincode);
        var postval = '{"name":"根据vincode查询参数","type":"0","val":"' + vincode + '"}';   //type:0 根据vincode查询参数
        console.log(postval);
        getVehicleInfoByajax(postval); //根据vincode 获取车型参数信息
    } else {
        DrawVehicleHtml("load", null);
    }
}
//获取部分搜索条件
function getCarInfo(action) {
    var carinfo = $("input.hide_data").val();
    carinfo = carinfo ? $.parseJSON(carinfo) : {};
    //点击获取车型信息
    if (action == "click") {
        carinfo.brand = getValOnClick("choose-brand"); //品牌
        carinfo.brandname = getValOnClick("choose-company"); //厂家
        carinfo.classify = getValOnClick("choose-type"); //车系
        carinfo.groupName = getValOnClick("choose-group"); //车组
        carinfo.pl = getValOnClick("choose-pl"); //排量
        carinfo.cartypekeyword = carinfo.pl && carinfo.groupName + carinfo.pl || carinfo.groupName || carinfo.classify || carinfo.brandname || carinfo.brand; //车型关键词
        return carinfo;

        //页面加载获取默认车型信息
    } else if (action == "load") {
        carinfo.searchTxt = carinfo.searchTxt;
        carinfo.type = carinfo.type;
        carinfo.type2 = carinfo.type2;
        return carinfo;
    }
}
//搜索时传入选中的值，有非空判断
function getValOnClick(parentcls) {
    var keyval;
    var isdefval = $("." + parentcls).find("span").attr("data-isdefval");
    if (isdefval == "0") {
        keyval = $("." + parentcls).find("span").text(); //品牌
    } else {
        keyval = "";
    }
    return keyval;
}
//移除错误提示
function removeErrorTips() {
    $(".error-tips").remove();
}
//获取隐藏input中存放的搜索条件
function getHideInput() {
    var carinfo = $("input.hide_data").val();
    carinfo = $.parseJSON(carinfo);
    return carinfo;
}
//更新隐藏域值
function updateHideInputVal(string) {
    $(".hide_data").val(string);
}
//页面一加载默认加载的数据（关键词、列表）
function drawKeyWordsOnLoad(type) {
    var kwords;
    if (type == 1) {
        kwords = getCarInfo("load").pl && getCarInfo("load").groupName + getCarInfo("load").pl || getCarInfo("load").groupName || getCarInfo("load").classify || getCarInfo("load").brandname || getCarInfo("load").brand;
        fillSelectedCarType("load"); //自动填充已选车型
        filledAutoByInfoPage(getCarInfo("load")); //filledAutoByInfoPage(data)；data为obj类型

        //根据搜索输入框内容搜搜
    } else if (type == 2) {
        kwords = getCarInfo("load").searchTxt;

        //点击侧边栏资料类别关键字搜索
    } else if (type == 3) {
        kwords = getCarInfo("load").type2 || getCarInfo("load").type;

    } else {
        kwords = null;
    }

    //带关键字的话就新增或替换
    (kwords && updateKwords(type, kwords));
}
//更新关键词
function updateKwords(type, kwords) {
    //带关键字的话就新增或替换
    var html, datatype, licls;
    var typelist = $("ul.keywords-list li.type");
    for (var i = 0; i < typelist.length; i++) {
        datatype = typelist.eq(i).data("type");
        //能匹配到的类型都是直接替换
        if (type == datatype) {
            licls = "type" + type;
            $(".keywords-list").find("." + licls).find("span").text("关键词：" + kwords);
            return;
        }
    }
    //匹配不到的新增(注意新增的时候，不同类型的关键词id、classname不一样，一共三个类型)
    switch (type) {
        case 1:
            licls = "type type1";
            break;

        case 2:
            licls = "type type2";
            break;

        case 3:
            licls = "type type3";
            break;

        default:
            break;
    }
    html = '<li class="' + licls + '" data-type="' + type + '"><span>关键词：' + kwords + '</span><em class="ic-removekw"></em></li>';
    $(".keywords-list").append(html); //更新关键词列表
}
//点击小弹层列表赋值文本框
function checkedCarType() {
    var checkedtxt = $(this).find("span").text() || $(this).text();
    var classname = $(this).parents(".pop-choose-car").data("classname");
    $("." + classname).find(".sim-select").text(checkedtxt).attr("data-isdefval", "0"); //赋值后改变data-isdefval属性为0,即false
    $("." + classname).find("input.txt_storeval").val(checkedtxt); //赋值联动隐藏域，便于后台获取
    $("." + classname).attr("data-checked", true); //控制是否显示下一个弹层的属性
    hideChoosePop(); //选中后关闭弹层

    var needclearcls, needactivecls;
    switch (classname) {
        case "choose-brand":
            $(".choose-company").trigger("click"); //若修改了品牌，则自动弹出第二个弹层选厂家
            needactivecls = "choose-brand,.choose-company";
            needclearcls = "choose-type,.choose-group,.choose-pl,.choose-year";
            $(".choose-type").find(".sim-select").text("请选择车系"); //车系
            $(".choose-group").find(".sim-select").text("请选择车组"); //车组
            $(".choose-pl").find(".sim-select").text("请选择排量"); //排量
            $(".choose-year").find(".sim-select").text("全部型号年款"); //型号年款
            break;
        case "choose-company":
            needactivecls = "choose-company";
            needclearcls = "choose-type,.choose-group,.choose-pl,.choose-year";
            $(".choose-type").find(".sim-select").text("请选择车系"); //车系
            $(".choose-group").find(".sim-select").text("请选择车组"); //车组
            $(".choose-pl").find(".sim-select").text("请选择排量"); //排量
            $(".choose-year").find(".sim-select").text("全部型号年款"); //型号年款
            break;
        case "choose-type":
            needactivecls = "choose-type";
            needclearcls = "choose-group,.choose-pl,.choose-year";
            $(".choose-group").find(".sim-select").text("请选择车组"); //车组
            $(".choose-pl").find(".sim-select").text("请选择排量"); //排量
            $(".choose-year").find(".sim-select").text("全部型号年款"); //型号年款
            break;
        case "choose-group":
            needactivecls = "choose-group";
            needclearcls = "choose-pl,.choose-year";
            $(".choose-pl").find(".sim-select").text("请选择排量"); //排量
            $(".choose-year").find(".sim-select").text("全部型号年款"); //型号年款
            break;
        case "choose-pl":
            needactivecls = "choose-pl";
            needclearcls = "choose-year";
            $(".choose-year").find(".sim-select").text("全部型号年款"); //型号年款
            break;
        case "choose-year":
            needactivecls = "choose-year";
            break;
        default:
            break;
    }
    $("." + needactivecls).addClass("active");
    $("." + needclearcls).attr("data-checked", false).find(".sim-select").attr("data-isdefval", "1"); //公共设置
    $("." + needclearcls).removeClass("active").find(".txt_storeval").val(""); //清空对应存放车型搜索条件的隐藏input

}
//清空车型选择框及其隐藏域
function backToBlank() {
    $(".srh-right .sim-txt").attr("data-checked", false).find(".sim-select").attr("data-isdefval", "1"); //公共设置
    $(".choose-brand").find(".sim-select").text("请选择品牌"); //品牌
    $(".choose-company").find(".sim-select").text("请选择厂家"); //厂家
    $(".choose-type").find(".sim-select").text("请选择车系"); //车系
    $(".choose-group").find(".sim-select").text("请选择车组"); //车组
    $(".choose-pl").find(".sim-select").text("请选择排量"); //排量
    $(".sim-txt").find("input.txt_storeval").val(""); //清空所有存放车型搜索条件的隐藏input
    clearCarTypeInHideInput();
    hideChoosePop(); //关闭所有小弹层
}
//url编码加密encodeURIComponent
function enCodeTxt(type, txt) {
    txt = encodeURIComponent(txt);
    var url = "SearchResult.aspx?" + type + "=" + txt;
    window.location.href = url;
}
//获取最新车组
function drawHotGroupName(data_groupName) {
    //获得最新车组信息-渲染页面
    var arr = [];
    var html = "";
    var groupName;
    for (var i = 0; i < data_groupName.length; i++) {
        groupName = encodeURIComponent(data_groupName[i].groupName);
        html = '<li><a href="SearchResult.aspx?groupName=' + groupName + '" target="mainFrame">' + data_groupName[i].groupName + '<span>' + returnDate(data_groupName[i].tt) + '</span></a></li>';
        arr.push(html);
    }
    var arrList = arr.join("");
    $(".hot-groupName").html(arrList); //填充最新车组20条
}

function returnDate(converteddate) {
    converteddate = Number(converteddate) * 1000; //时间戳秒转毫秒
    var date, Y, M, D, result;
    date = new Date(converteddate);
    Y = date.getFullYear() + '-';
    M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
    result = Y + M + D;
    return result;
}
//显示大弹层
function showChooseType() {
    //backToBlank();
    $(".srh-vin .txt_nobg").val("输入17位VIN码");
    //$(".btn-srh-bychoose").addClass("btn-theme1_disabled");
    $(".box-choosetype").show();
}
//隐藏大弹层
function hideChooseType() {
    $(".box-choosetype").hide();
}
//显示小弹层
function showChoosePop(event) {
    var $loading = $('<li class="waiting"><img src="images/loading.gif" class="loading-pic" alt=""/><span class="loading-des">正在加载中......</span></li>');
    $(".pre-select").html(""); //清空所有弹层标题
    $(".car-infolist,.pre-select").html($loading); //列表显示加载中
    removeErrorTips(); //移除错误提示
    $(".pop-choose-car").hide(); //关闭全部小弹层
    var isShowPop; //点击是否显示弹层
    if (event.data.defaultshow) {
        isShowPop = "true";
    } else {
        isShowPop = event.data.basedobj.attr('data-checked'); //basedobj:依据上一个选项的data-checked是否已选的属性
    }

    if (isShowPop == "true") {
        var popname = event.data.popname; //弹层class名
        //给当前选项对应弹层列表赋值（即拼html）
        switch (popname) {
            case "pop-choose-brand":
                $(".brand-byabc a").first().trigger("click"); //模拟点击第一项,即给品牌列表tab页卡赋初始值
                break;
            case "pop-choose-company":
                drawCompanyList(); //读取厂家列表信息
                break;
            case "pop-choose-type":
                drawTypeList(); //读取车系列表信息
                break;
            case "pop-choose-group":
                drawGroupList(); //读取车组列表信息
                break;
            case "pop-choose-pl":
                drawPlList();   //读取排量列表信息
                break;
            case "pop-choose-year":
                drawYearList();  //读取型号年款列表信息
                break;
            default:
                break;
        }
        $("." + popname).show(); //仅显示当前小弹层
    }
}
//隐藏小弹层
function hideChoosePop() {
    $(".pop-choose-car").hide();
}
//清空存放关键词的隐藏域里的车型信息
function clearCarTypeInHideInput() {
    var carinfo = $("input.hide_data").val();
    if (carinfo) {
        carinfo = $.parseJSON(carinfo);
        carinfo.brand = carinfo.brandname = carinfo.classify = carinfo.groupName = carinfo.pl = "";
        carinfo = JSON.stringify(carinfo); //不能直接一个json扔给后台，转成string后才行
        updateHideInputVal(carinfo); //这一步同时也需要后台配合完成，不然刷新后就没用了
    }
}
//根据首字母选品牌
function tabByABC_Ht(ts) {
    $(".brand-byabc a").removeClass("active");
    ts.addClass("active"); //按字母删选品牌页卡高亮
}
//列表隔行变色
function popListZebra($op) {
    //车型选择表格隔行变色
    var carInfoList = $op.find("ul.car-infolist li");
    for (var i = 0; i < carInfoList.length; i += 4) {
        carInfoList.eq(i + 2).addClass("evenrow");
        carInfoList.eq(i + 3).addClass("evenrow");
    };
}
//替换小弹层内容
function drawHtml(data, type, $obj) {
    //data:得到的后台数据； type=1:品牌； type=2:厂家； type=3:车系； type=4:车组； type=5:排量； $obj:需要替换html的标签容器
    var carListArr = [];
    var list = "";
    var precheckedval, $op;

    switch (type) {
        case 1: //品牌
            for (var i = 0; i < data.length; i++) {
                list = "<li class='lp1'><label>" + data[i].pym + "</label><span>" + data[i].brand + "</span></li>";
                carListArr.push(list);
            }
            $op = $(".pop-choose-brand");
            break;

        case 2: //厂家
            for (var i = 0; i < data.length; i++) {
                list = "<li>" + data[i].brandname + "</li>";
                carListArr.push(list);
            }
            precheckedval = $(".choose-brand").find("span").text();
            $op = $(".pop-choose-company");
            break;

        case 3: //车系
            for (var i = 0; i < data.length; i++) {
                list = "<li>" + data[i].classify + "</li>";
                carListArr.push(list);
            }
            precheckedval = $(".choose-company").find("span").text();
            $op = $(".pop-choose-type");
            break;

        case 4: //车组
            for (var i = 0; i < data.length; i++) {
                list = "<li>" + data[i].groupName + "</li>";
                carListArr.push(list);
            }
            precheckedval = $(".choose-type").find("span").text();
            $op = $(".pop-choose-group");
            break;

        case 5: //排量
            for (var i = 0; i < data.length; i++) {
                list = "<li>" + data[i].engineDesc + "</li>";
                carListArr.push(list);
            }
            precheckedval = $(".choose-group").find("span").text();
            $op = $(".pop-choose-pl");
            break;

        case 6: //型号年款
            for (var i = 0; i < data.length; i++) {
                list = "<li>" + data[i].carname + "</li>";
                carListArr.push(list);
            }
            precheckedval = $(".choose-pl").find("span").text();
            $op = $(".pop-choose-year");
            break;

        default:
            break;
    }

    var carList = carListArr.join("");
    $("h3.pre-select").text(precheckedval); //替换弹层标题
    $obj.html(carList); //替换弹层列表
    popListZebra($op); //li模拟的表格隔行变色
    $(".list-container").mCustomScrollbar();
}
//根据vin码自动填充选择车型框内容
function filledAutoByVin(data) {
    if (!data) {
        //不存在对应车型时清空上次选择
        backToBlank();
        $(".sim-txt").removeClass("active");

    } else {
        data = $.parseJSON(data);
        $(".srh-right .sim-txt").attr("data-checked", true).find(".sim-select").attr("data-isdefval", "0"); //公共设置
        $(".choose-brand").find(".sim-select").text(data[0].brand).prev("input.txt_storeval").val(data[0].brand); //品牌
        $(".choose-company").find(".sim-select").text(data[0].brandName).prev("input.txt_storeval").val(data[0].brandName); //厂家
        $(".choose-type").find(".sim-select").text(data[0].familyName).prev("input.txt_storeval").val(data[0].familyName); //车系
        $(".choose-group").find(".sim-select").text(data[0].groupName).prev("input.txt_storeval").val(data[0].groupName); //车组
        $(".choose-pl").find(".sim-select").text(data[0].engineDesc).prev("input.txt_storeval").val(data[0].engineDesc); //排量
    }
}
//根据资料页传过来data为obj类型
function filledAutoByInfoPage(data) {
    if (data.length != 0) {
        (data.brand && $(".choose-brand").addClass("active").attr("data-checked", true).find(".sim-select").text(data.brand).attr("data-isdefval", "0")); //品牌
        (data.brandname && $(".choose-company").addClass("active").attr("data-checked", true).find(".sim-select").text(data.brandname).attr("data-isdefval", "0")); //厂家
        (data.classify && $(".choose-type").addClass("active").attr("data-checked", true).find(".sim-select").text(data.classify).attr("data-isdefval", "0")); //车系
        (data.groupName && $(".choose-group").addClass("active").attr("data-checked", true).find(".sim-select").text(data.groupName).attr("data-isdefval", "0")); //车组
        (data.pl && $(".choose-pl").addClass("active").attr("data-checked", true).find(".sim-select").text(data.pl).attr("data-isdefval", "0")); //排量
    }
}
//填充关键词下面的已选车型
function fillSelectedCarType(action) {
    $("li.car-brand").text(getCarInfo(action).brand);
    $("li.car-brandname").text(getCarInfo(action).brandname);
    $("li.car-classify").text(getCarInfo(action).classify);
    $("li.car-group").text(getCarInfo(action).groupName);
    $("li.car-pl").text(getCarInfo(action).pl);
}


//======================================== 内页其他 ==============================================
function controlAutoComplete(clsname,data){
    $("."+clsname).on("keyup", { myjson: data }, autoComplete);
    $("."+clsname).on("focus", { myjson: data }, autoComplete);
    $(document).click(function (e) { ulHide(e); });
}

function ulHide(e) {
    //获取当前点击目标的class
    var cls = $(e.target).attr("class");
    if (cls != "txt autocomplete_type listenInput" && cls != "txt autocomplete_name listenInput" && cls != "txt autocomplete_pbrand listenInput" && cls != "namelist") {
        removeCompleteResult();
    }
    return false;
}

function autoComplete(event) {
    var data = event.data.myjson;
    if (typeof data == "string") {
        data = $.parseJSON(data);
    };
    var $ts = $(this);
    var $op = $(".oparent").length > 0 ? $(".oparent") : $("body");
    removeCompleteResult(); //清空记录
    var defval = $ts.data("defval"); //输入框默认值
    var kws = $ts.val(); //获取当前输入框的实时值
    kws = kws.toUpperCase();
    if (kws == "" || kws == defval) {
        kws = "";
        removeCompleteResult(); //清空记录
    } else {
        if (typeof data != "object") {
            data = $.parseJSON(data);
        }
        (data.length > 0 && drawCompleteResult(kws, data, $ts, $op)); //绘制匹配结果列表
    }
}

function removeCompleteResult() {
    $(".namelist").remove();
}

function drawCompleteResult(kws, data, $ts, $op) {
    var html = "";
    var arr = [];
    var patrn = new RegExp(".*" + kws + ".*");
    for (var i = 0; i < data.length; i++) {
        if (patrn.test(data[i].name) || patrn.test(data[i].pym)) {
            html = '<li>' + data[i].name + '</li>';
            arr.push(html);
        }
    }
    if (arr.length > 0) {
        var wh = $ts.innerWidth;
        var x = $ts.offset().left;
        var y = $ts.offset().top + 27;

        var $ul = $('<ul class="namelist">' + arr.join("") + '</ul>');
        $op.append($ul);
        $(".namelist").css({ "left": x, "top": y, "width": wh }).on("click", "li", { obj: $ts }, changeVal);
        if ($(".sel-groupname").length > 0) {
            $(".namelist").on("click", "li", getGroupNameByAjax); //根据所选车型获取对应车组信息
        }
    }
}
function getGroupNameByAjax() {
    var classify = $(this).text();
    var $op = $(this).parents(".oparent");
    $.ajax({
        "type": "POST",
        "url": "EPCGetCarGroup.aspx",
        "data": { "data": classify }, //把当前车系传给后台
        "dataType": "json",
        success: function (data) {
            //模拟后台返回的data
            //var data = [{"groupName":"别克GL8 陆尊(05/07-)"},{"groupName":"别克GL8(10/11-)"},{"groupName":"别克GL8(99-10)"}];
            filledSelectOpts("sel-groupname", data, $op);
            filledSelectOpts("sel-pl", [], $op);
            removeCompleteResult(); //清空记录
        },
        error: function () {
            console.log("失败");
        }
    })
}
function getPlByAjax() {
    var groupname = $(this).val();
    $(this).siblings(".hidden-txt").val(groupname);
    var $op = $(this).parents(".oparent");
    $.ajax({
        "type": "POST",
        "url": "EPCGetCarPl.aspx",
        "data": { "data": groupname }, //把当前车组传给后台
        "dataType": "json",
        success: function (data) {
            //模拟后台返回的data
            //var data = [{"pl":"2.5L"},{"pl":"3.0L"}];
            filledSelectOpts("sel-pl", data, $op);
            $op.find(".sel-pl").siblings(".hidden-txt").val("");
        },
        error: function () {
            console.log("失败");
        }
    })
}
function filledSelectOpts(clsname, data, $op) {
    var arr = ['<option value="">全部</option>'];
    var html = "";
    if (clsname == "sel-groupname") {
        for (var i = 0; i < data.length; i++) {
            html = '<option value="' + data[i].groupName + '">' + data[i].groupName + '</option>';
            arr.push(html);
        }
    } else {
        for (var i = 0; i < data.length; i++) {
            html = '<option value="' + data[i].pl + '">' + data[i].pl + '</option>';
            arr.push(html);
        }
    }
    if (data.length == 0) { $op.find("." + clsname).siblings(".hidden-txt").val(""); }
    $op.find("." + clsname).html(arr.join(""));
};
function changeVal(event) {
    var $tsinput = event.data.obj;
    var myval = $(this).text();
    $tsinput.val(myval)
}


//给input加class="listenInput",data-objclsname="需要高亮的对象唯一标识class名字" ,data-highlightcls="需要高亮对象的高亮class名"
function ListenInputBox() {
    $(".listenInput").each(function () {
        var deftval, objname, $obj, highlightcls;
        deftval = $(this).data("defval");
        objname = $(this).data("objclsname");
        $obj = $("." + objname);
        highlightcls = $(this).data("highlightcls");
        $(this).focus(function () {
            $obj.addClass(highlightcls);
            if ($(this).val() == deftval)
                $(this).val("");
        }).blur(function () {
            $obj.removeClass(highlightcls);
            if ($(this).val() == "")
                $(this).val(deftval);
        });
    })
};
//动态生成遮罩
function addLoadingMask() {
    //新建loading遮罩
    var $loadingMask = $('<div class="loading-mask"><p class="loading-maskbg"></p><div class="loading"> <img src="../images/loading.gif" class="loading-pic" alt=""/><span class="loading-des">正在加载中......</span></div></div>');
    var ht = $(document).height();
    var wh = $(window).width();
    $("body").css("overflow-y", "hidden");
    $loadingMask.appendTo($("body"));
    $(".loading-mask,.loading-maskbg").height(ht).width(wh);
}
//移除遮罩
function removeLoadingMask() {
    $(".loading-mask").remove();
    $("body").css("overflow-y", "auto");
}
//自动移除loading遮罩
function removeLoadingMaskAuto() {
    var t = setTimeout(removeLoadingMask, 2000);
}
//显示弹层；
function showPopupBox() {
    var o_popup = getClass('div', 'popup');
    o_popup[0].style.display = "block";
}
//关闭弹层；
function hidePopupBox() {
    var o_popup = getClass('div', 'popup');
    o_popup[0].style.display = "none";
}
//获得标签名为tagName,类名className的元素
function getClass(tagName, className) {
    //当浏览器支持getElementsByClassName这个函数时
    if (document.getElementsByClassName) {
        return document.getElementsByClassName(className);
    } else {
        var tags = document.getElementsByTagName(tagName); //获取标签
        var tagArr = []; //用于返回类名为className的元素
        for (var i = 0; i < tags.length; i++) {
            if (tags[i].className == className) {
                tagArr[tagArr.length] = tags[i]; //保存满足条件的元素
            }
        }
        return tagArr;
    }
}
//tab选项卡；
function tabCon() {
    removeActive();

    //设置当前高亮样式
    $(".tab-title li").removeClass("active");
    $(this).addClass("active");
    var idx = $(this).index();
    $(".con").removeClass("active").eq(idx).addClass("active");

}
//tab选项卡默认选中；
function tabDefaultSelected(tid) {
    removeActive();

    tid = tid ? tid : 0;
    if (tid) {
        $("#" + tid).addClass("active");
        $("#con_" + tid).addClass("active");

    } else {
        //通过索引默认选中第一项
        $(".tab-title li").first().addClass("active");
        $(".con").first().addClass("active");
    }
}
//移除高亮
function removeActive() {
    //去除所有高亮样式
    $(".tab-title li,.con").removeClass("active");
}
//重置iframe高度
function resetIframeHt() {
    var pHt = $(top.window).height();
    $(".conFrame").height(pHt - 185);
}
function cutString() {
    $(".showmore").each(function () {
        //截取字符串前把完整的字符保存一遍
        var tstxt = $(this).text();
        //收尾去空格
        tstxt = trimStr(tstxt);
        $(this).attr("data-txt", tstxt);

        //截取字符串并替换原内容
        if (tstxt.length > 25) {
            tstxt = tstxt.substring(0, 25);
            $(this).text(tstxt + '…');
        }

    })
}
function trimStr(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}
function drawThumbList() {
    var $thumblist = $(".onfocus-showbig");
    $thumblist.each(function () {
        var arr = [];
        var html = "";
        var url = $(this).data("url");
        url = url.replace(/\+/g, ' ');
        if (url) {
            url = url.split(",");
            for (var i = 0; i < url.length; i++) {
                html = '<img src="' + url[i] + '" alt="" />';
                arr.push(html);
            }
            $(this).html(arr.join(""));
        }
    });
}
function showBigImg() {
    //图片存在
    var imglen = $(this).find("img").length;
    if (imglen != 0) {
        var url = $(this).find("img").first().attr("src");
        var $bigimg;
        if (imglen == 1) {
            $bigimg = $('<div class="showbig"><img src="' + url + '" alt=""/></div>');

            //多图显示点击缩略图切换
        } else {
            $bigimg = $('<div class="showbig"><span class="toggle-tips"><em class="icon-arrow-r"></em>点击缩略图切换</span><img src="' + url + '" alt=""/></div>');
        }
        $("body").append($bigimg);
        var wh = $(".showbig").width();
        var left = $(this).offset().left - wh - 8;
        var top = $(this).offset().top - 290;
        $(".showbig").css({ "top": top, "left": left });
    }
}

function removeBigImg() {
    $(".showbig").remove();
}
function toggleThumb() {
    var tsimg = $(this).clone(); //复制当前img，移动到最后
    $(this).parent(".onfocus-showbig").append(tsimg);
    $(this).remove(); //移除当前，防止重复
}

//========================== 资料页车型搜索cookie设置 ===============================
//资料页菜单cookie设置
$(function () {
    //页面一加载就尝试读取页面中的cookie名为srhcarhistory的看是否存在
    var cookieval = readCookie("srhcarhistory") ? 1 : 0; //cookie默认第一次没有的就0
    ($(".srh-history").length > 0 && fillCarInfo(cookieval, 1));

    //点击搜索获取品牌、厂家、车系、车组、排量信息，存入cookie
    $(".btn-srh-bychoose").click(function () {
        var carType = getValOnClick("choose-pl") && getValOnClick("choose-group") + getValOnClick("choose-pl") || getValOnClick("choose-group") || getValOnClick("choose-type") || getValOnClick("choose-company") || getValOnClick("choose-brand"); ;
        if (carType) {
            fillCarInfo(carType, 0);
        }
    })

});
function fillCarInfo(cookieval, cl) {
    if (!cl) {
        writeCookie("srhcarhistory", cookieval);
    }
    cookieval = readCookie("srhcarhistory") ? readCookie("srhcarhistory") : cookieval;
    var html;
    if (!cookieval) {
        html = '您还可以通过以下两种方式确定车型';
    } else {
        html = '之前查找过：<span class="ft-hlight">' + cookieval + '</span>您还可以通过以下两种方式确定车型';
    }
    $(".srh-history").html(html);
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

//=============================== 内页报表相关 ==================================
//图表相关
$(function () {
    ($(".radio-list li>input").length > 0 && $(".radio-list li>input").lpRadioBox()); //自定义radiobox绑定
    setTrHighLight(); //表格划过变色
    ($(".radio-list").length > 0 && $(".radio-list").on("click", "li", passDate));  //单选：按日期删选
    ($(".radio-list").length > 0 && fillDate()); //页面一加载的时候默认填充日期文本框
    $(".set_date .txt_date").blur(fillRadio); //日期框change时联动单选
    $(".chart-tabt").on("click", "li", chartTab);
    ($(".select_m").length > 0 && drawDate()); //门店报表按月份查询
});
function drawDate() {
    $(".select_m").lpComboBox(); //绑定下拉框控件
    $(".select_long").lpComboBox({
        "comboCls": "combo2"
    }); //绑定下拉框控件
};
function chartTab() {
    var idx = $(this).index();
    $(this).addClass("active").siblings("li").removeClass("active");
    $(this).parent().next(".chart-tabcon").find("li").addClass("e-hide").eq(idx).removeClass("e-hide");
}
function fillRadio() {
    var startDate, endDate;
    startDate = $(".set_date .txt_date:first").val(); //当前手动更新后的时间开始值
    endDate = $(".set_date .txt_date:last").val(); //当前手动更新后的时间结束值
    var $ckdInput = $(".radio-list input[name='selectDate']:checked"); //选中的input

    //当后台未传入起止时间时，设置默认时间；
    (!startDate && (startDate = $ckdInput.attr("date")));
    (!endDate && (endDate = getToday()));

    radioIsChecked(startDate, endDate); //判断单选是否选中
}
function fillDate() {
    var startDate, endDate; //后台自定义值
    startDate = $(".set_date .txt_date:first").attr("start_time"); //后台隐藏的开始值
    endDate = $(".set_date .txt_date:last").attr("end_time"); //后台隐藏的、结束值
    var $ckdInput = $(".radio-list input[name='selectDate']:checked"); //选中的input

    //当后台未传入起止时间时，设置默认时间；
    (!startDate && (startDate = $ckdInput.attr("date")));
    (!endDate && (endDate = getToday()));

    radioIsChecked(startDate, endDate); //判断单选是否选中
    //填充日期框
    $(".set_date .txt_date:first").attr({ "value": startDate, "Text": startDate });
    $(".set_date .txt_date:last").attr({ "value": endDate, "Text": endDate });

}
//联动单选选中效果
function radioIsChecked(startDate, endDate) {
    var today = getToday(); //当天
    var $radio = $(".radio-list input");
    //默认先把所有的都取消选中
    $radio.attr("checked", false).parent("em").removeClass("radio_checked");
    //找到符合日期的项勾选
    for (var i = 0; i < $radio.length; i++) {
        //后台传值在单选范围内
        if (startDate == $radio.eq(i).attr("date") && endDate == today) {
            $radio.eq(i).attr("checked", true).parent("em").addClass("radio_checked");
        }
    }
}
function passDate() {
    var startDate = $(this).find("input").attr("date");  //获取开始日期为当前选中项
    $(".set_date .txt_date:first").val(startDate);
    var today = getToday(); //获取截止日期
    $(".set_date .txt_date:last").val(today);
}
//获取当天日期
function getToday() {
    var myDate = new Date();
    var y = myDate.getFullYear();    //获取完整的年份(4位,1970-????)
    var m = myDate.getMonth() + 1;     //获取当前月份(0-11,0代表1月)
    m = m < 10 ? ('0' + m) : m;             //月份保持两位数
    var d = myDate.getDate();        //获取当前日(1-31)
    d = d < 10 ? ('0' + d) : d;
    var today = y + "-" + m + "-" + d;
    return today;
}
function setTrHighLight() {
    $(".table tbody tr").hover(function () {
        $(this).toggleClass("active");
    });
}


//============================================ 打印 ============================================
//******* jq插件 printArea 打印 ********//
(function ($) {
    $.fn.printArea = function () {
        var ele = $(this);
        var printCss = '';

        //获取原页面表格样式重新link在新打开的窗口中
        $(document).find("link").filter(function () {
            return $(this).attr("rel").toLowerCase() == "stylesheet";
        }).each(
            function () {
                printCss += '<link type="text/css" rel="stylesheet" href="' + $(this).attr("href") + '" >';
            });

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

        if (is_IE) {
            wh = 595;
            ht = 842;
        }


        var printWindow = window.open(windowUrl, windowName, 'height=' + ht + ', width=' + wh + ', top=-99999, left=-99999');
        var result = '<head>' + printCss + '</head><body>' + printContent + '</body>';
        printWindow.document.write(result);
        setTimeout(delayPrint, 100); //延时打印：保证页面参数已获取成功
        function delayPrint() {
            printWindow.document.close();
            printWindow.focus();
            printWindow.print();
            printWindow.close();
        }
    }
})(jQuery);
//打印
function print(msg) {
    //若页面有多个打印任务，则先要清除掉页面上原有的print标签
    if ($(".print").length) { $(".print").remove(); }

    //动态创建一个隐藏的div用来存放打印内容
    var o_print = $('<div class="print" style="display:none;"></div>');
    o_print.html(msg); //把需要打印的目标文档在当前页隐藏起来
    $("body").append(o_print);
    $(".print").printArea();
}
















