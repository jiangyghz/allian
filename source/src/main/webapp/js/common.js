if (
    window.location.host != "127.0.0.1" &&window.location.host != "localhost"
) {
    try {
        var hash = window.location.host.split(".")
        .slice(1)
        .join(".");
        document.domain = hash;}
    catch(e){};
}

$(function () {
    loadjs("../js/colResizable-1.6.js", function () {
        // setTimeout(function(){
        var option = {
            minWidth: 30
        };
        if ( $("#table1").length>0) {
            var width = $("#table1")[0].clientWidth;
            $("#table1")[0].style.width = width + "px";
            if ($('#table1 [data-field="MEMO"]').length > 0) {
                $('#table1 [data-field="MEMO"]')[0].style.width = "auto";
            }
            if ($('#table1 th[data-field="0"]').length > 0) {
                $('#table1 th[data-field="0"]')[0].style.textAlign="center";
             }
            $("#table1").colResizable(option);
            if ($('#table1 th[data-field="0"]').length > 0) {
                $('#table1 th[data-field="0"]')[0].style.width="36px";
            }
            $("#table1").css("cssText", "table-layout:fixed;word-break: break-all;");
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

//弹框清除
$("body").on("hidden.bs.modal", ".modal", function () {
    $(this)
        .find(".modal-content")
        .empty();
    $(this).removeData("bs.modal");
    $(this).remove();
    if ($("body").find(".modal").length > 0) {
        //弹2个的时候，样式不能修改
        $("body").addClass("modal-open");
    }
});
//$('body').on('hide.bs.modal',".modal" ,function () {
//    $(".modal-content").empty();
//});
//弹框关闭
var closemodal = function (id) {
    if (id == undefined) {
        id = "myModal";
    }
    $("#" + id).modal("hide");
};

//时间格式化
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        S: this.getMilliseconds() //毫秒
    };
    var week = {
        "0": "\u65e5",
        "1": "\u4e00",
        "2": "\u4e8c",
        "3": "\u4e09",
        "4": "\u56db",
        "5": "\u4e94",
        "6": "\u516d"
    };
    var month = {
        "1": "一月",
        "2": "二月",
        "3": "三月",
        "4": "四月",
        "5": "五月",
        "6": "六月",
        "7": "七月",
        "8": "八月",
        "9": "九月",
        "10": "十月",
        "11": "十一月",
        "12": "十二月"
    };
    var quarter = {
        "1": "一季度",
        "2": "二季度",
        "3": "三季度",
        "4": "四季度"
    };

    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(
            RegExp.$1,
            (this.getFullYear() + "").substr(4 - RegExp.$1.length)
        );
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(
            RegExp.$1,
            (RegExp.$1.length > 1 ?
                RegExp.$1.length > 2 ?
                "\u661f\u671f" :
                "\u5468" :
                "") + week[this.getDay() + ""]
        );
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(
                RegExp.$1,
                RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
            );
        }
    }
    return fmt;
};
//字符串传换为时间类型
String.prototype.toDate = function () {
    var temp = this.toString();
    temp = temp.replace(/-|年|月|日/gi, "/");
    var date = temp.split(" ")[0];
    var time = temp.split(" ").length == 2 ? temp.split(" ")[1] : "";

    var y = 0,
        m = 0,
        d = 0;
    var h = 0,
        mi = 0,
        s = 0;
    if (date.indexOf("/") > 0) {
        var ymd = date.split("/");
        y = parseInt(ymd[0] * 1);
        m = parseInt(ymd[1] * 1 - 1) || 0;
        d = parseInt(ymd[2] * 1) || 1;
    } else {
        if (date.length == 6) {
            date = "20" + date;
        }
        if (date.length == 8) {
            y = parseInt(date.substr(0, 4));
            m = parseInt(date.substr(4, 2) - 1);
            d = parseInt(date.substr(6, 2));
        }
    }
    if (time != "") {
        var t = time.split(":");
        h = parseInt(t);
        mi = t.length > 1 ? parseInt(t[1]) : 0;
        s = t.length > 2 ? parseInt(t[2]) : 0;
        d = new Date(y, m, d, h, mi, s);
        return d;
    } else {
        var d = new Date(y, m, d);
        return d;
    }
};
//通过格式计算时间差
Date.prototype.DateDiff = function (strInterval, dtEnd) {
    var dtStart = this;
    if (typeof dtEnd == "string") {
        //如果是字符串转换为日期型
        dtEnd = dtEnd.toDate();
    }
    var str = "";
    var o = {
        "s+": parseInt((dtEnd - dtStart) / 1000) -
            parseInt((dtEnd - dtStart) / 60000) * 60,
        "m+": parseInt((dtEnd - dtStart) / 60000) -
            parseInt((dtEnd - dtStart) / 3600000) * 60,
        "h+": parseInt((dtEnd - dtStart) / 3600000) -
            parseInt((dtEnd - dtStart) / 86400000) * 24,
        "d+": parseInt((dtEnd - dtStart) / 86400000),
        "w+": parseInt((dtEnd - dtStart) / (86400000 * 7)),
        "M+": dtEnd.getMonth() +
            1 +
            (dtEnd.getFullYear() - dtStart.getFullYear()) * 12 -
            (dtStart.getMonth() + 1),
        "y+": dtEnd.getFullYear() - dtStart.getFullYear()
    };
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(strInterval)) {
            strInterval = strInterval.replace(
                RegExp.$1,
                RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length)
            );
        }
    }
    return strInterval;
};
//日期加减
Date.prototype.addDate = function (fmt, num) {
    var dtStart = this;
    var dtRes = new Date(this);
    var o = {
        s: dtStart.getTime() + num * 1000,
        m: dtStart.getTime() + num * 1000 * 60,
        h: dtStart.getTime() + num * 1000 * 60 * 60,
        d: dtStart.getTime() + num * 1000 * 60 * 60 * 24,
        w: dtStart.getTime() + num * 1000 * 60 * 60 * 24 * 7,
        M: new Date(this).setMonth(this.getMonth() + num),
        q: new Date(this).setMonth(this.getMonth() + num * 3),
        y: new Date(this).setFullYear(this.getFullYear() + num)
    };
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            dtRes.setTime(o[k]);
            break;
        }
    }
    return dtRes;
};
jQuery.extend({
    params: function (val) {
        var uri = window.location.search;
        var re = new RegExp("(?:\\&|\\?)" + val + "=([^&?]*)", "ig");
        return uri.match(re) ?
            decodeURIComponent(uri.match(re)[0].substr(val.length + 2)) :
            "";
    },
    contain: function (arr, val) {
        var flag = false;
        $.each(arr, function (i) {
            if (arr[i] == val) {
                flag = true;
            }
        });
        return flag;
    },
    openlink: function (url, blank) {
        //触发a点击事件，新开窗口,无blank参数是新开
        var a = $("<a></a>");
        if (blank == undefined || blank) {
            a[0].target = "_blank";
        }
        a[0].href = url;
        if (document.all) {
            $("body").append(a);
            a[0].click();
            a.remove();
        } else if (document.createEvent) {
            var ev = document.createEvent("MouseEvents");
            ev.initEvent("click", true, true);
            setTimeout(function () {
                a[0].dispatchEvent(ev);
            }, 0);
            a.remove();
        }
    },
    iframelink: function (url) {
        //框架隐藏打开
        if ($(window).find("#iframeurl").length == 0) {
            $("body").append(
                "<iframe id='iframeurl' style='display:none;width:0px; height:0px'></iframe>"
            );
        }
        $("#iframeurl").attr("src", url);
    },
    call: function (fn, args) {
        fn.apply(this, args);
    },
    ajaxQueryPage: function (tableSelector, param, callback, url) {
        var h = url;
        if (url == undefined) {
            if ($(tableSelector).attr("role")) {
                h = $(tableSelector).attr("role");
            } else {
                h = location.href;
            }
        }
        $.ajax({
            type: "POST",
            url: h,
            data: param,
            dataType: "json",
            async: true,
            success: function (obj) {
                if ($(tableSelector).attr("role") == undefined) {
                    $(tableSelector).attr("role", h);
                }
                var d;
                var t = 0;
                if (obj) {
                    if (typeof obj.Items == "undefined") {
                        //没有分页的情况
                        $(tableSelector).bootstrapTable("load", {
                            total: obj.length,
                            rows: obj,
                            time: 0
                        });
                    } else {
                        d =
                            typeof obj.Items == "object" ? obj.Items : JSON.parse(obj.Items);
                        if (obj.TotalItems) t = obj.TotalItems;
                        //使用loadData方法加载Dao层返回的数据
                        $(tableSelector).bootstrapTable("getOptions").time = obj.Time;
                        $(tableSelector).bootstrapTable("load", {
                            total: t,
                            rows: d,
                            time: obj.Time
                        });
                        //$.loading(true);
                        if (
                            callback != undefined &&
                            callback != null &&
                            typeof callback == "function"
                        ) {
                            callback(obj);
                        }
                    }
                }
            },
            beforeSend: function () {
                //$.loading("请稍等▪▪▪");
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                //$.loading(true);
            }
        });
    },
    modal: function (e) {
        var _this = e;
        var p = $("body");
        if (p.find(".container").length > 0) {
            p = p.find(".container").eq(0);
        }
        var modalid = $(_this).attr("data-target");
        if (modalid) {
            modalid = modalid.replace("#", "");
        } else {
            modalid = "myModal";
            $(_this).attr("data-target", "#" + modalid);
        }
        //$(_this).attr("data-toggle","modal");
        var size = "2";
        if ($(_this).attr("data-size")) {
            size = $(_this).attr("data-size");
        }
        var sizes = "modal-md";
        switch (size) {
            case "1":
                sizes = "modal-sm";
                break;
            case "2":
                sizes = "modal-md";
                break;
            case "3":
                sizes = "modal-lg";
                break;
            default:
                sizes = size;
                break;
        }
        if (p.find("#" + modalid).length == 0) {
            var modal =
                "<div class='modal fade' id='" +
                modalid +
                "' tabindex='-1' role='dialog'  aria-hidden='true' ><div class='modal-dialog " +
                sizes +
                "' role='document'><div class='modal-content'></div></div></div>";
            p.append(modal);
        }
        $("#" + modalid).modal();
        var href = $(_this).attr("href");
        if (href == undefined || href == "" || href.indexOf("java") > -1) {
            href = $(_this).attr("role");
        }
        $("#" + modalid)
            .find(".modal-content")
            .empty()
            .load(href);
        try {
            e.preventDefault();
        } catch (ex) {}
    }
});
//tab切换函数
var tabs = function (handles, list, isclick, callback, speed) {
    if (typeof speed != "number") {
        speed = 0;
    }
    if (typeof isclick != "boolean") {
        isclick = false;
    }
    return {
        lock: false,
        id: undefined,
        index: speed,
        choose: function (index) {
            var _this = this;
            for (var i = 0; i < handles.length; i++) {
                if (i == index) {
                    handles.eq(i).addClass("on");
                    if (0 > 0) {
                        _this.index = i;
                        if (!_this.lock) {
                            _this.lock = true;
                            list
                                .eq(i)
                                .addClass("on")
                                .find("img")
                                .eq(0)
                                .fadeIn(500, function () {
                                    _this.lock = false;
                                    if (_this.index != i) {
                                        _this.choose(_this.index);
                                    }
                                });
                        }
                    } else {
                        list
                            .eq(i)
                            .show()
                            .addClass("on");
                        _this.lock = false;
                    }
                } else {
                    handles.eq(i).removeClass("on");
                    if (0 > 0) {
                        list
                            .eq(i)
                            .removeClass("on")
                            .find("img")
                            .eq(0)
                            .fadeOut(500);
                    } else {
                        list
                            .eq(i)
                            .hide()
                            .removeClass("on");
                    }
                }
            }
            this.index = index;
            if (typeof callback == "function") {
                callback(this.index);
            }
        },
        stop: function () {
            clearInterval(this.id);
        },
        start: function () {
            if (0 > 0) {
                this.stop();
                var _this = this;
                this.id = setInterval(function () {
                    _this.choose(_this.index == handles.length - 1 ? 0 : _this.index + 1);
                }, speed);
            }
        },
        init: function () {
            var _this = this;
            if (isclick) {
                handles.each(function (i) {
                    $(this).click(function () {
                        _this.stop();
                        _this.choose(i);
                    });
                });
            } else {
                handles.each(function (i) {
                    $(this).hover(
                        function () {
                            _this.stop();
                            _this.choose(i);
                        },
                        function () {
                            _this.start();
                        }
                    );
                });
                list.each(function (i) {
                    $(this).hover(
                        function () {
                            _this.stop();
                        },
                        function () {
                            _this.start();
                        }
                    );
                });
            }
            this.choose(this.index);
            this.start();
            return this;
        }
    }.init();
};
//弹框 关闭
$(function () {
    //自动弹框模块初始化
    $("body")
        .off("click.model", ".ent-modal")
        .on("click.model", ".ent-modal", function (e) {
            var _this = this;
            if ($(_this).attr("pause")) {
                return;
            }
            var p = $("body");
            if (p.find(".container").length > 0) {
                p = p.find(".container").eq(0);
            }
            var modalid = $(_this).attr("data-target");
            if (modalid) {
                modalid = modalid.replace("#", "");
            } else {
                modalid = "myModal";
                $(_this).attr("data-target", "#" + modalid);
            }
            //$(_this).attr("data-toggle","modal");
            var size = "2";
            if ($(_this).attr("data-size")) {
                size = $(_this).attr("data-size");
            }
            var sizes = "modal-md";
            switch (size) {
                case "1":
                    sizes = "modal-sm";
                    break;
                case "2":
                    sizes = "modal-md";
                    break;
                case "3":
                    sizes = "modal-lg";
                    break;
                default:
                    sizes = size;
                    break;
            }
            if (p.find("#" + modalid).length == 0) {
                var modal =
                    "<div class='modal fade' id='" +
                    modalid +
                    "' tabindex='-1' role='dialog'  aria-hidden='true' ><div class='modal-dialog " +
                    sizes +
                    "' role='document'><div class='modal-content'></div></div></div>";
                p.append(modal);
            }
            $("#" + modalid).modal();
            var href = $(_this).attr("href");
            if (href == undefined || href == "" || href.indexOf("java") > -1) {
                href = $(_this).attr("role");
            }
            $("#" + modalid)
                .find(".modal-content")
                .empty()
                .load(href);
            e.preventDefault();
        });
    //初始化日期框
    try {
        $(".form_datetime").datetimepicker();
    } catch (e) {}
    if ($("#Province.region").length > 0) {
        //行政区级联
        $("#Province.region").each(function (i, n) {
            var p = $(this).closest(".Regionbox");
            var dft = p.find("#Province.region").attr("dft");
            p.find("#Province.region").bind("change", function () {
                var code = $(this)
                    .find("option:selected")
                    .attr("code");
                p.find("#Region.region").val(code);
                if (code != "") {
                    $.post(
                        "../WebService/Region.ashx?lv=2&code=" + code,
                        function (data) {
                            //console.info(data);
                            p.find("#City.region").empty();
                            p.find("#City.region").append(
                                "<option code='' value=''>--请选择--</option>"
                            );
                            p.find("#County.region").empty();
                            p.find("#County.region").append(
                                "<option code='' value=''>--请选择--</option>"
                            );
                            var dfts = p.find("#City.region").attr("dft");
                            for (var o in data) {
                                p.find("#City.region").append(
                                    "<option code='" +
                                    o +
                                    "' value='" +
                                    data[o] +
                                    "' " +
                                    (dfts == data[o] ? "selected=selected" : "") +
                                    ">" +
                                    data[o] +
                                    "</option>"
                                );
                            }
                            if (dfts != "") {
                                p.find("#City.region").triggerHandler("change");
                            }
                        },
                        "json"
                    );
                } else {
                    p.find("#City.region").empty();
                    p.find("#County.region").empty();
                    p.find("#City.region").append(
                        "<option code='' value=''>--请选择--</option>"
                    );
                    p.find("#County.region").append(
                        "<option code='' value=''>--请选择--</option>"
                    );
                }
            });
            p.find("#City.region").bind("change", function () {
                var code = $(this)
                    .find("option:selected")
                    .attr("code");
                p.find("#Region.region").val(code);
                if (code != "") {
                    $.post(
                        "../WebService/Region.ashx?lv=3&code=" + code,
                        function (data) {
                            p.find("#County.region").empty();
                            p.find("#County.region").append(
                                "<option code='' value=''>--请选择--</option>"
                            );
                            var dfts = p.find("#County.region").attr("dft");
                            for (var o in data) {
                                p.find("#County.region").append(
                                    "<option code='" +
                                    o +
                                    "' value='" +
                                    data[o] +
                                    "' " +
                                    (dfts == data[o] ? "selected=selected" : "") +
                                    ">" +
                                    data[o] +
                                    "</option>"
                                );
                            }
                            if (dfts != "") {
                                p.find("#County.region").triggerHandler("change");
                            }
                        },
                        "json"
                    );
                } else {
                    p.find("#County.region").empty();
                    p.find("#County.region").append(
                        "<option code='' value=''>--请选择--</option>"
                    );
                }
            });
            p.find("#County.region").bind("change", function () {
                var code = $(this)
                    .find("option:selected")
                    .attr("code");
                p.find("#Region.region").val(code);
            });
            $.post(
                "../WebService/Region.ashx?lv=1",
                function (data) {
                    p.find("#Province.region").empty();
                    p.find("#Province.region").append(
                        "<option code='' value=''>--请选择--</option>"
                    );
                    for (var o in data) {
                        p.find("#Province.region").append(
                            "<option code='" +
                            o +
                            "' value='" +
                            data[o] +
                            "' " +
                            (dft == data[o] ? "selected=selected" : "") +
                            ">" +
                            data[o] +
                            "</option>"
                        );
                    }
                    if (dft != "") {
                        p.find("#Province.region").triggerHandler("change");
                    }
                    //console.info(data);
                },
                "json"
            );
        });
    }
});