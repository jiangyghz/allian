//========================== 公用 ===============================
$(function(){
    setFontSize(document,window);//设置font-size,用于css rem布局
    $(window).resize(function(){setFontSize(document,window);})
});

function setFontSize(doc, win) {
    var docEl = doc.documentElement,
        isIOS = navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
        dpr = isIOS ? Math.min(win.devicePixelRatio, 3) : 1,
        dpr = window.top === window.self ? dpr : 1, //被iframe引用时，禁止缩放
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize';
    docEl.dataset.dpr = dpr;
    var recalc = function () {
        var width = docEl.clientWidth;
        if (width / dpr > 750) {
            width = 750 * dpr;
        }
        docEl.dataset.width = width;
        docEl.dataset.percent = 100 * (width / 750);
        docEl.style.fontSize = 100 * (width / 750) + 'px';
    };
    recalc();
    if (!doc.addEventListener) return;
    win.addEventListener(resizeEvt, recalc, false);
}

//=============================================【车辆当前状态】========================================
 $(function(){
     //【page:选择已有车辆连接】渲染已绑定的车辆
     ($(".list-linkcar").length>0 && drawBindCar(data));

     //【page:车辆当前状态】
     $(".carcurrent-state").on("click",".btn-toggle",stateboxToggle);//展开、收拢车辆信息
     if($(".carcurrent-state").length>0 && onloadByAjax());//动态渲染车辆当前状态及实时定位
 });

function onloadByAjax(){
    console.log("toid="+getUrlPara().toid+"; openid="+getUrlPara().openid+"; tid="+getUrlPara().tid);
    $.ajax({
        "type":"POST",
        "url":"TboxGetInfo.aspx",//后台数据处理页面
        "data":{"toid":getUrlPara().toid,"openid":getUrlPara().openid,"tid":getUrlPara().tid,"type":"status"},//前台传给后台的参数toid, openid, tid
        "dataType":"json",
        success: function(data){
            data = returnParseJSON(data);
            var obj = setMaps(data.mapinfo);//创建并初始化地图
            setCarStatus(data.carstate);//初始化车辆当前状态
           var timr = setInterval(function(){reflashStateByAjax(obj)}, data.updateinterval);//实时ajax请求刷新车辆状态;  实时请求数据间隔:data.updateinterval
            console.log("提交成功！");
        },
        error:function(){
            console.log("提交失败！");
        }
    });
}
function reflashStateByAjax(obj){
    $.ajax({
        "type":"POST",
        "url":"TboxGetInfo.aspx",//后台数据处理页面
        "data": { "toid": getUrlPara().toid, "openid": getUrlPara().openid, "tid": getUrlPara().tid, "type": "map" },//前台传给后台的参数toid, openid, tid
        "dataType":"json",
        success: function(data){
            data = returnParseJSON(data);
            //更新地图当前位置
            var x,y;
            x = data.mapinfo.pointx;
            y = data.mapinfo.pointy;

            //改变标注点及标注文字的位置
            var center = new soso.maps.LatLng(x,y); //传入纬度,经度
            obj.marker.setPosition(center);//移动标注文字到目标位置
            obj.markerL.setPosition(center);//移动标注文字到目标位置
            setMaps(data.mapinfo);//创建并初始化地图
            setCarStatus(data.carstate);//更新车子各设备状态
            console.log(data);
        },
        error:function(){
            console.log("提交失败！");
        }
    });
}
function getUrlPara(){
    var start1,start2,start3,toid,openid,tid,url; //从url上获取
    url = window.location.href;
    //url = "TboxInfoShow.html?toid=toid1&openid=openid2&tid=tid3";//模拟当前地址栏带参数的url
    start1 = url.indexOf("toid");
    start2 = url.indexOf("openid");
    start3 = url.indexOf("tid");

    toid = url.substring(start1+5,start2-1);
    openid = url.substring(start2+7,start3-1);
    tid = url.substring(start3+4);
    var url = {"toid":toid,"openid":openid,"tid":tid};
    return url;
}
function setCarStatus(data){
    data = returnParseJSON(data);
    var type = data[3].state;
    switch (type){
        case "0000000"://全锁
            data[3].statetips = "边门关尾门关";
            break;
        case "0010000"://右后
        case "0001000"://左后
        case "0000100"://右前
        case "0000010"://左前
        case "0000110"://左前+右前
        case "0011000"://左后+右后
        case "0010010"://左前+右后
        case "0001100"://右前+左后
        case "0001010"://左前+左后
        case "0010100"://右前+右后
        case "0001110"://左前+右前+左后
        case "0010110"://左前+右前+右后
        case "0011010"://左前+左后+右后
        case "0011100"://右前+左后+右后
        case "0011110"://左前+右前+左后+右后
            data[3].statetips = "边门未关";
            break;
        case "0100000"://后
            data[3].statetips = "尾门未关";
            break;
        case "0101000"://后+左后
        case "0110000"://后+右后
        case "0100100"://后+右前
        case "0100010"://后+左前
        case "0100110"://后+左前+右前
        case "0111000"://后+左后+右后
        case "0110010"://后+左前+右后
        case "0101100"://后+右前+左后
        case "0101010"://后+左前+左后
        case "0110100"://后+右前+右后
        case "0101110"://后+左前+右前+左后
        case "0110110"://后+左前+右前+右后
        case "0111010"://后+左前+左后+右后
        case "0111100"://后+右前+左后+右后
        case "0111110"://5门全开
		case "1111110"://6门全开
            data[3].statetips = "边门尾门未关";
            break;
    }
    var html = '<tr>' +
                   '<td><em class="ic-state ic-Control-'+ data[0].state +'"></em>'+ data[0].statetips +'</td>'+
                   '<td><em class="ic-state ic-HandBrake-'+ data[1].state +'"></em>'+ data[1].statetips +'</td>'+
                   '<td><em class="ic-state ic-Gear-'+ data[2].state +'"></em>'+ data[2].statetips +'</td>'+
               '</tr>'+
               '<tr>' +
                    '<td><em class="ic-state ic-Lock-'+ data[3].state +'"></em>'+ data[3].statetips +'</td>'+
                    '<td><em class="ic-state ic-Power-'+ data[4].state +'"></em>'+ data[4].statetips +'</td>'+
                    '<td><em class="ic-state ic-Light-'+ data[5].state +'"></em>'+ data[5].statetips +'</td>'+
               '</tr>';
    $(".list-state tbody").html(html);
}
function setMaps(data){
    //创建Map
    var center = new soso.maps.LatLng(data.pointx,data.pointy); //传入纬度,经度
    var map = new soso.maps.Map(document.getElementById('map_container'), {
        center: center,
        zoom: data.zoom,//地图放大倍数
        draggable: true,
        scrollWheel: true,
        zoomInByDblClick: true
    });

    //创建Marker
    var marker = new soso.maps.Marker({
        position: center,
        animation: soso.maps.MarkerAnimation.BOUNCE,
        map: map
    });
    //创建Marker label(marker提示文字)
    var markerL = new soso.maps.Label({
        position: center,
        map: map,
        content:data.label //当前定位点提示文字
    });

    ////储存相关对象用于改变标记点Marker达到导航效果
    var obj = {};
    obj.marker = marker;
    obj.markerL = markerL;
    return obj;
}

function drawBindCar(data){
    var arr = [];
    var html = "";
    data = returnParseJSON(data);
    //无数据
    if(data.length == 0){
        html = '<li>'+
            '<a href="bindCar.html" >'+
            '<img src="images/bg_car_nolink.png" alt=""/>'+
            '<p>您还未绑定车辆，赶紧去添加车辆吧！</p>'+
            '</a>'+
            '</li>';
        $(".list-linkcar").addClass("nodata").html(html);

        //有数据
    }else{
        for(var i=0; i<data.length; i++){
            html = '<li>'+
                '<a href="'+ data[i].linkurl +'" >'+
                '<em class="btn-link2">连接</em>'+
                '<img class="thumb" src="'+ data[i].carimg +'" alt=""/>'+
                '<div class="info">'+
                '<p class="carno">'+ data[i].carno +'</p>'+
                '<p class="devicetype">'+ data[i].devicetype +'</p>'+
                '</div>'+
                '</a>'+
                '</li>';
            arr.push(html);
        }
        $(".list-linkcar").removeClass("nodata").html(arr.join(""));
    }
}
function stateboxToggle(){
    $(".carcurrent-state").toggleClass("shrink");
}
function returnParseJSON(data){
    if(typeof data == "string"){
        data = $.parseJSON(data);
    };
    return data;
}
