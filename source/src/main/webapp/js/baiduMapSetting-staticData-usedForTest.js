//========================================================================== 地图上绘制标注点 ==========================================================================
if(document.getElementById("mapmarkers")){
    var map = new BMap.Map("mapmarkers");                 //创建地图容器
    var point = new BMap.Point(data.clng, data.clat); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point, data.zoom);
    drawMarkers(data);
}
//draw markers on the map
function drawMarkers(data){
    var markerArr = data.markerinfo;
    for (var i = 0; i < markerArr.length; i++) {
        addMarker(markerArr[i]);
    }
};
//create marker and label description
function addMarker(tsmarker){
    //======= draw icon
    var m_ic = new BMap.Icon("images/icon_marker_car_32×32.png", new BMap.Size(32,32));  //marker label
    var m_pt = new BMap.Point(tsmarker.lng,tsmarker.lat);                                //marker(lng,lat)

    //======= draw label
    var label_opts = {                                //add label to the marker
        position : m_pt,
        offset   : new BMap.Size(-30, -40)
    };
    var label = new BMap.Label(tsmarker.label, label_opts);
    var focused_style = {color : "#fff",fontSize : "12px",height : "20px",lineHeight : "20px",fontFamily:"微软雅黑","borderColor":"#1caf9a",background:'#1caf9a'};
    var nofocus_style = {color : "#fff",fontSize : "12px",height : "20px",lineHeight : "20px",fontFamily:"微软雅黑","borderColor":"#3e4a5a",background:'#3e4a5a'};
    var label_style = tsmarker.focus ?  focused_style :  nofocus_style;
    label.setStyle(label_style);
    map.addOverlay(label);


    //======= draw searchInfoWindow
    var coninfo = tsmarker.content;
    var btn_focus_txt = tsmarker.focus ? '取消关注' : '关注此车';
    var conhtml = '<div class="markerbox">' +
        '<p><label>定位时间：</label>'+ coninfo.loctime +'</p>'+
        '<p><label>即时速度：</label>'+ coninfo.speed +'</p>'+
        '<p><label>平均油耗：</label>'+ coninfo.avgoil +'</p>'+
        '<p><label>相关信息：</label>'+ coninfo.others +'</p>'+
        '<p class="markerbox-btn"><a href="javascript:;" name='+tsmarker.label+' id='+tsmarker.tboxid +' onclick="updateFocus(this)">'+ btn_focus_txt +'</a><a name="'+ tsmarker.trackurl +'" href="javascript:;" onclick="newWinShowTrack(this)">查询轨迹</a><a href="javascript:;">查询状态</a></p>' +
        '</div>';
    var searchInfoWindow = null;
    searchInfoWindow = new BMapLib.SearchInfoWindow(map, conhtml, {
        title  : tsmarker.label,                       //标题
        width  : 310,                                  //宽度
        height : 160,                                  //高度
        enableAutoPan : true,                          //自动平移
        searchTypes   :[]
    });
    var marker = new BMap.Marker(m_pt,{icon:m_ic});    //create marker
    marker.addEventListener("click", function(e){      //addEventListener on click
        searchInfoWindow.open(marker);
    });
    map.addOverlay(marker);                            //add marker on the map
}
//规定每次只能查看一个车子的轨迹，只新打开一个窗口trackWin
function newWinShowTrack(ts){
    var url = ts.name;//get url
    window.open(url,"trackWin");
}


//ajax:修改收藏状态（只允许收藏一个标注点,后台根据当前提交的tboxid来更新整段json返回给前台）
function updateFocus(ts) {
    //关闭弹层
    $(".BMapLib_bubble_close").trigger("click");

    //删除所有标注点
    var allOverlay = map.getOverlays();
    for (var i = 0; i < allOverlay.length; i++) {
        map.removeOverlay(allOverlay[i]);
    }

    console.log("传给后台：当前车辆tboxid="+ts.id);
    //ajax提交当前点tboxid给后台，并返回最新点信息
    /*
     $.ajax({
     "type":"POST",
     "url":"getMarkerInfo.aspx",
     "data":{"tboxid":ts.id},
     "dataType":"json",
     "success":function(data){
     */
    //模拟data
    var data = {
        "clng":"116.404",                      //map center lng
        "clat":"39.915",                       //map center lat
        "zoom":15,                             //map zoom
        "markerinfo":[{
            "tboxid":"t1",                      //唯一标识
            "trackurl":"carTrack.html?tboxid=t1",  //查询轨迹页面链接跳转,页面地址及参数可自定义
            //"stateurl":"carState.html?tboxid=t1",//查询状态页面，暂不开放
            "label" :"浙A88888",                //marker label
            "lng":"116.38062231538079",         //marker lng
            "lat":"39.91096905271941",          //marker lat
            "focus" : false,                     //focus state
            "content":{
                "loctime":"2016-10-15 09:30:02",//定位时间
                "speed":"65km/h",               //即时速度
                "avgoil":"7.6",                 //平均油耗
                "others":"LNBMDBAH6GU115567"    //相关信息：车架号
            }

        },{
            "tboxid":"t2",
            "trackurl":"carTrack.html?tboxid=t2",
            //"stateurl":"carState.html?tboxid=t2",
            "label" :"浙A66666",
            "lng":"116.373972818003",
            "lat":"39.92771759267555",
            "focus" : true,
            "content":{
                "loctime":"2016-11-10 07:00:02",
                "speed":"80km/h",
                "avgoil":"6",
                "others":"LNBMDBAH6GU888888"
            }

        },{
            "tboxid":"t3",
            "trackurl":"carTrack.html?tboxid=t3",
            //"stateurl":"carState.html?tboxid=t3
            "label" :"浙A11111",
            "lng":"116.41702964706361",
            "lat":"39.91169685937829",
            "focus" : false,
            "content":{
                "loctime":"2016-11-10 07:00:08",
                "speed":"70km/h",
                "avgoil":"8.8",
                "others":"LNBMDBAH6GU111111"
            }

        }]
    };
    //重新绘制点信息
    drawMarkers(data);
    /*
     },
     "error":function(){}
     })
     */
}




//========================================================================== 地图上绘制行驶轨迹 ==========================================================================
if(document.getElementById("maptrack")){
    //初始化地图（未标注点）
    var map = new BMap.Map("maptrack");
    var point2 = new BMap.Point(107.895785, 35.530622); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, 5);
    map.enableScrollWheelZoom();

    //ajax:页面一加载 初始化table 、 track
    updateTable(map,"load");

    //绑定事件
    $(".row-sel-date").on("click",".btn-srhtrack",function(){updateTable(map,"click")});  //ajax:更新表格数据
    $("#tracklist").on("click",".btn-showtrack",function(){
        var trackid = $(this).attr("id");
        updateTrack(map,"click",trackid); //ajax:查看轨迹
    });
}
//拼接表格字符串
function drawTableHtml(data){
    //渲染标题
    $("h3.track-title").text(data.title);

    //渲染时间
    $("input.srh-starttime").val(data.srhstart);
    $("input.srh-endtime").val(data.srhend);

    //渲染列表
    var trackarr = [];
    var html = "";
    for(var i=0; i<data.list.length; i++){
        html = '<tr>'+
            '<td class="text-center">'+ data.list[i].starttime +'</td>'+
            '<td class="text-center">'+ data.list[i].mileage +'</td>'+
            '<td class="text-center">'+ data.list[i].runtime +'</td>'+
            '<td><button id="'+ data.list[i].trackid +'" class="btn btn-primary btn-sm btn-showtrack">查看</button></td>'+
            '</tr>';
        trackarr.push(html);
    }
    $("#tracklist").html(trackarr.join(""));
}
function drawTrack(map,data){
    var point2 = new BMap.Point(data.clng, data.clat); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, data.zoom);

    //==============创建轨迹虚线
    var trackarr = newPoint(data.trackinfo);
    var polyline = new BMap.Polyline(trackarr,{strokeColor:"#f000cf",strokeStyle:"dashed", strokeWeight:3, strokeOpacity:1});   //创建折线
    map.addOverlay(polyline);   //增加折线

    function newPoint(pointArr){
        var arr = [];
        for(var i=0; i<pointArr.length; i++){
            var lng = pointArr[i].lng; //经
            var lat = pointArr[i].lat; //纬
            var pointobj = new BMap.Point(lng, lat);
            arr.push(pointobj);
        }
        return arr;
    }

    //============== 创建marker+label+searchInfoWindow
    //得到第一个和最后一个点坐标作为起点和终点信息
    var twopoint = [{
        "lng":data.trackinfo[0].lng,
        "lat":data.trackinfo[0].lat,
        "title":"起点："+data.starttime
    },{
        "lng":data.trackinfo[data.trackinfo.length-1].lng,
        "lat":data.trackinfo[data.trackinfo.length-1].lat,
        "title":"终点："+data.endtime
    }];
    for(var k=0; k<twopoint.length; k++){
        addMarkerStartAndEnd(map,data,twopoint[k]); //添加起点和终点marker
    }

}
function addMarkerStartAndEnd(map,data,pinfo){
    var m_ic = new BMap.Icon("images/icon_marker_car_32×32.png", new BMap.Size(32,32));
    var lb_style = {background:"#1caf9a",color:"#fff",borderWidth:"1px",borderColor:"#1caf9a","maxWidth":"none","padding":"5px"};
    var point = new BMap.Point(pinfo.lng, pinfo.lat);
    var marker = new BMap.Marker(point,{icon:m_ic});
    var s_conhtml = '<div class="markerbox">' +
        '<p><label>开始：</label>'+ data.starttime +'</p>'+
        '<p><label>结束：</label>'+ data.endtime +'</p>'+
        '<p><label>里程：</label>'+ data.mileage +'</p>'+
        '<p><label>时长：</label>'+ data.runtime +'</p>'+
        '</div>';
    var searchInfoWindow = null;
    searchInfoWindow = new BMapLib.SearchInfoWindow(map, s_conhtml, {
        title  : pinfo.title,                 //标题
        width  : 310,                                  //宽度
        height : 120,                                  //高度
        enableAutoPan : true,                          //自动平移
        searchTypes   :[]
    });
    marker.addEventListener("click", function(e){      //addEventListener on click
        searchInfoWindow.open(marker);
    });
    map.addOverlay(marker);
    var label = new BMap.Label(pinfo.title,{offset:new BMap.Size(-50,-25)});
    label.setStyle(lb_style);
    marker.setLabel(label);
}
function resetMap(map){
    var point2 = new BMap.Point(107.895785, 35.530622); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, 5);

    //删除所有标注点
    var allOverlay = map.getOverlays();
    for (var i = 0; i < allOverlay.length; i++) {
        map.removeOverlay(allOverlay[i]);
    }
}
function getTboxId(){
    var url = location.href;
    var idx = url.indexOf("tboxid");
    var tboxid = "";
    if(idx > 0){
        tboxid = url.substring(idx+7);
    }
    return tboxid;
}
function getToday(){
    var d = new Date();
    d = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
    return d;
}
function writeCookie(name, value, hours){
    var expire = "";
    if(hours != null){
        expire = new Date((new Date()).getTime() + hours * 3600000);
        expire = "; expires=" + expire.toGMTString();
    }
    //写cookie时务必设置cookie保存目录，防止多个同名值的出现
    document.cookie = name + "=" + escape(value) + expire+";path=/";
}
function readCookie(name){
    var cookieValue = "";
    var search = name + "=";
    if(document.cookie.length > 0){
        var offset = document.cookie.indexOf(search);
        if (offset != -1){
            offset += search.length;
            var end = document.cookie.indexOf(";", offset);
            if (end == -1) end = document.cookie.length;
            cookieValue = unescape(document.cookie.substring(offset, end))
        }
    }
    return cookieValue;
}
function getCarInfo(){
    var carinfo = {};
    carinfo.tboxid = getTboxId();
    carinfo.srhstart = readCookie("srhstart");
    carinfo.srhend = readCookie("srhend");
    carinfo.page = readCookie("page");
    carinfo.trackid = readCookie("trackid");
    return carinfo;
}


//ajax:获得当前选择查看的轨迹
function updateTrack(map,action,trackid){
    //重置地图
    resetMap(map);

    var tboxid = getTboxId();
    if(action == "load"){
        trackid = readCookie("trackid") ? readCookie("trackid") : "";    //读cookie里的id
        if(!trackid){
            resetMap(map);
            return;
        }
    }else if(action == "click"){
        //写cookie
        writeCookie("trackid",trackid);
        writeCookie("tboxid",tboxid);
    }
    $("#"+trackid).parent().parent().addClass("active").siblings().removeClass("active");

    var carinfo = getCarInfo();
    carinfo = JSON.stringify(carinfo);
    console.log("传给后台：当前轨迹信息carinfo="+carinfo);
    /*
     $.ajax({
     "type":"POST",
     "url":"getCarInfo.aspx",
     "data":{"carinfo":carinfo},  //carinfo = '{"tboxid":tboxid,"srhstart":srhstart,"srhend":srhend,"page":page,"trackid":trackid}'
     "dataType":"json",
     "success":function(data){
     */

    //模拟后台返回数据
    var  data = {
        "tboxid":"t3",                                 //车辆唯一标识
        "title":"浙A88888",                            //轨迹页面表格标题
        "srhstart":getCarInfo().srhstart,                           //查询开始时间
        "srhend":getCarInfo().srhend,                               //查询结束时间
        "recordcount":"2",                             //当前搜索结果数据总条数
        "selectedtrackinfo":{                          //前台传给后台的trackid = ""时，后台返回的selectedtrackinfo = {}或""
            "trackid":trackid,
            "clng":"120.148861",                       //map center lng
            "clat":"30.283693",                        //map center lat
            "zoom":17,                                 //map zoom
            "mileage":"1.5",                           //行驶里程（km）
            "runtime":"3",                             //行驶时间 (分钟)
            "starttime":"2016-11-13 10:05",            //行程开始时间
            "endtime":"2016-11-13 10:08",              //行程结束时间
            "trackinfo":[{
                "lng":"120.142069",                    //起：伟星大厦
                "lat":"30.283451"
            },{
                "lng":"120.148861",                    //途径点经度：
                "lat":"30.283693"                      //途径点纬度
            },{
                "lng":"120.153891",                    
                "lat":"30.283864"
			},{
                "lng":"120.157817",                    //终：莫干山路文三路口
                "lat":"30.284028"
            }]
        },
        "list":[{
            "trackid":"track1",                        //每条轨迹id
            "mileage":"1.5",                           //行驶里程（km）
            "runtime":"3",                             //行驶时间(分钟)
            "starttime":"2016-11-13 10:05"             //行程开始时间
        },{
            "trackid":"track2",
            "mileage":"5.5",
            "runtime":"15",
            "starttime":"2016-11-14 11:05"
        }]
    };

    drawTrack(map,data.selectedtrackinfo); //重新渲染轨迹


    /*
     },
     "error":function(){}
     })
     */
}
//ajax:更新所有数据(表格+分页+轨迹)
function updateTable(map,action){
    //重置地图
    resetMap(map);

    var tboxid = getTboxId();
    var default_s = getToday() + ' 00:00', //默认查询时间戳
        default_e = getToday() + ' 23:59';
    var srhstart,srhend,page,trackid;

    if(action == "load"){
        var cookie_tboxid = readCookie("tboxid");
        //说明是同一个tboxid 那么可以直接读cookie里的时间
        if(cookie_tboxid == getTboxId()){
            srhstart = readCookie("srhstart");
            srhend = readCookie("srhend");
            page = readCookie("page") ? readCookie("page") : 1;
            trackid = readCookie("trackid") ? readCookie("trackid") : "";//能读到就有选过轨迹，没读到就没选过
        }else{
            srhstart = default_s;
            srhend = default_e;
            page = 1;
            trackid = "";
        }
    }else if(action == "click"){                 //设置时间戳
        srhstart = $(".srh-starttime").val();
        srhend = $(".srh-endtime").val();
        //修改后都为空时用默认时间戳
        if(!srhstart && !srhend) {
            srhstart = default_s;
            srhend = default_e;
        }
        page = 1;
        trackid = "";
    }
    writeCookie("tboxid",tboxid);
    writeCookie("srhstart",srhstart);
    writeCookie("srhend",srhend);
    writeCookie("trackid",trackid);
    writeCookie("page",page);
    //console.log("cookie测试：tboxid="+tboxid+"; srhstart="+srhstart+"; srhend="+srhend+"; page="+page+"; trackid="+trackid);

    var carinfo = getCarInfo();
    carinfo = JSON.stringify(carinfo);
    console.log("传给后台：当前轨迹信息carinfo="+carinfo);
    /*
     $.ajax({
     "type":"POST",
     "url":"getCarInfo.aspx",
     "data":{"carinfo":carinfo},  //carinfo = '{"tboxid":tboxid,"srhstart":srhstart,"srhend":srhend,"page":page,"trackid":trackid}'
     "dataType":"json",
     "success":function(data){
     */

    //后台根据tboxid、srhstart、srhend模拟后台返回数据
    var data = {};
    switch (tboxid){
        case "t1":
            data = {
                "tboxid":"t1",
                "title":"浙A88888",
                "srhstart":srhstart,
                "srhend":srhend,
                "recordcount":"2",
                "selectedtrackinfo":{                          //前台传给后台的trackid = ""时，后台返回的selectedtrackinfo = {}或""
                    "trackid":trackid,
                    "clng":"120.148861",                       //map center lng
                    "clat":"30.283693",                        //map center lat
                    "zoom":17,                                 //map zoom
                    "mileage":"1.5",                           //行驶里程（km）
                    "runtime":"3",                             //行驶时间 (分钟)
                    "starttime":"2016-11-13 10:05",            //行程开始时间
                    "endtime":"2016-11-13 10:08",              //行程结束时间
                    "trackinfo":[{
                        "lng":"120.142069",                    //起：伟星大厦
                        "lat":"30.283451"
                    },{
                        "lng":"120.148861",                    //途径点经度：
                        "lat":"30.283693"                      //途径点纬度
                    },{
                        "lng":"120.153891",
                        "lat":"30.283864"
                    },{
                        "lng":"120.157817",                    //终：莫干山路文三路口
                        "lat":"30.284028"
                    }]
                },
                "list":[{
                    "trackid":"track11",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"
                },{
                    "trackid":"track12",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                }]
            };
            break;
        case "t2":
            data = {
                "tboxid":"t2",
                "title":"浙A66666",
                "srhstart":srhstart,
                "srhend":srhend,
                "recordcount":"1",
                "selectedtrackinfo":{                          //前台传给后台的trackid = ""时，后台返回的selectedtrackinfo = {}或""
                    "trackid":trackid,
                    "clng":"120.148861",                       //map center lng
                    "clat":"30.283693",                        //map center lat
                    "zoom":17,                                 //map zoom
                    "mileage":"1.5",                           //行驶里程（km）
                    "runtime":"3",                             //行驶时间 (分钟)
                    "starttime":"2016-11-13 10:05",            //行程开始时间
                    "endtime":"2016-11-13 10:08",              //行程结束时间
                    "trackinfo":[{
                        "lng":"120.142069",                    //起：伟星大厦
                        "lat":"30.283451"
                    },{
                        "lng":"120.148861",                    //途径点经度：
                        "lat":"30.283693"                      //途径点纬度
                    },{
                        "lng":"120.153891",
                        "lat":"30.283864"
                    },{
                        "lng":"120.157817",                    //终：莫干山路文三路口
                        "lat":"30.284028"
                    }]
                },
                "list":[{
                    "trackid":"track21",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"
                }]
            };
            break;
        default:
            data = {
                "tboxid":"t3",
                "title":"浙A11111",
                "srhstart":srhstart,
                "srhend":srhend,
                "recordcount":"12",
                "selectedtrackinfo":{                          //前台传给后台的trackid = ""时，后台返回的selectedtrackinfo = {}或""
                    "trackid":trackid,
                    "clng":"120.148861",                       //map center lng
                    "clat":"30.283693",                        //map center lat
                    "zoom":17,                                 //map zoom
                    "mileage":"1.5",                           //行驶里程（km）
                    "runtime":"3",                             //行驶时间 (分钟)
                    "starttime":"2016-11-13 10:05",            //行程开始时间
                    "endtime":"2016-11-13 10:08",              //行程结束时间
                    "trackinfo":[{
                        "lng":"120.142069",                    //起：伟星大厦
                        "lat":"30.283451"
                    },{
                        "lng":"120.148861",                    //途径点经度：
                        "lat":"30.283693"                      //途径点纬度
                    },{
                        "lng":"120.153891",
                        "lat":"30.283864"
                    },{
                        "lng":"120.157817",                    //终：莫干山路文三路口
                        "lat":"30.284028"
                    }]
                },
                "list":[{
                    "trackid":"track31",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"

                },{
                    "trackid":"track32",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                },{
                    "trackid":"track33",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"

                },{
                    "trackid":"track34",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                },{
                    "trackid":"track35",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"

                },{
                    "trackid":"track36",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                },{
                    "trackid":"track37",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"

                },{
                    "trackid":"track38",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                },{
                    "trackid":"track39",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"

                },{
                    "trackid":"track40",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                },{
                    "trackid":"track41",
                    "mileage":"1.1",
                    "runtime":"3",
                    "starttime":"2016-11-13 10:05"

                },{
                    "trackid":"track42",
                    "mileage":"9",
                    "runtime":"53",
                    "starttime":"2016-11-14 11:05"
                }]
            };
            break;
    }
    drawTableHtml(data); //渲染表格
    trackListPage(map,data);//渲染分页
    updateTrack(map,"load",trackid);//渲染map轨迹

    /*
     },
     "error":function(){}
     })
     */

}
//ajax:前端分页控件
function trackListPage(map,data){
    //重置地图
    resetMap(map);

    var data_arr = data.list; //返回数据条数数组
    var recordcount;          //数据总条数
    //获取到后台的数组为空
    if(data_arr.length == 0) {
        recordcount = 0;
        $("p.page-count strong").text(recordcount); //数据总条数渲染到页面
        //分页控件
        $(".pagerArea").cypager({
            pg_size: 10, //每页显示条数
            pg_nav_count: 8, //导航栏显示页码最大个数
            pg_total_count: recordcount, //数据条数
            pg_prev_name:'前页',
            pg_next_name:'后页'
        });

    }else{
        recordcount = data.recordcount; //数据总条数
        var srhstart = data.srhstart;
        var srhend = data.srhend;
        //分页控件
        $(".pagerArea").cypager({
            pg_size: 10, //每页显示条数
            pg_nav_count: 8, //导航栏显示页码最大个数
            pg_total_count: recordcount, //数据条数
            pg_prev_name:'前页',
            pg_next_name:'后页',
            pg_call_fun: function(count) {  //点击事件
                //传tboxid 时间戳 当前页码
                var tboxid = getTboxId();
                var page = parseInt(count);
                var trackid = "";
                writeCookie("page",page);
                writeCookie("trackid",trackid);

                //上下翻页的时候
                var carinfo = getCarInfo();
                carinfo = JSON.stringify(carinfo);
                console.log("传给后台：当前轨迹信息carinfo="+carinfo);
                /*
                 $.ajax({
                 "type":"POST",
                 "url":"getCarInfo.aspx",
                 "data":{"carinfo":carinfo},  //carinfo = '{"tboxid":tboxid,"srhstart":srhstart,"srhend":srhend,"page":page,"trackid":trackid}'
                 "dataType":"json",
                 "success":function(data){
                 */

                var  data = {
                    "tboxid":"t3",                                 //车辆唯一标识
                    "title":"浙A88888",                            //轨迹页面表格标题
                    "srhstart":srhstart,                           //查询开始时间
                    "srhend":srhend,                               //查询结束时间
                    "recordcount":"2",                             //当前搜索结果数据总条数
                    "selectedtrackinfo":{                          //前台传给后台的trackid = ""时，后台返回的selectedtrackinfo = {}或""
                        "trackid":trackid,
                        "clng":"120.148861",                       //map center lng
                        "clat":"30.283693",                        //map center lat
                        "zoom":17,                                 //map zoom
                        "mileage":"1.5",                           //行驶里程（km）
                        "runtime":"3",                             //行驶时间 (分钟)
                        "starttime":"2016-11-13 10:05",            //行程开始时间
                        "endtime":"2016-11-13 10:08",              //行程结束时间
                        "trackinfo":[{
                            "lng":"120.142069",                    //起：伟星大厦
                            "lat":"30.283451"
                        },{
                            "lng":"120.148861",                    //途径点经度：
                            "lat":"30.283693"                      //途径点纬度
                        },{
                            "lng":"120.153891",
                            "lat":"30.283864"
                        },{
                            "lng":"120.157817",                    //终：莫干山路文三路口
                            "lat":"30.284028"
                        }]
                    },
                    "list":[{
                        "trackid":"track1",                        //每条轨迹id
                        "mileage":"1.1",                           //行驶里程（km）
                        "runtime":"3",                             //行驶时间(分钟)
                        "starttime":"2016-11-13 10:05"             //行程开始时间
                    },{
                        "trackid":"track2",
                        "mileage":"5.5",
                        "runtime":"15",
                        "starttime":"2016-11-14 11:05"
                    }]
                };
                drawTableHtml(data);//拼接字符串
                resetMap(map);//重置地图


                /*
                 },
                 "error":function(){
                    console.log("sorry,出错了！");
                 }
                 });
                 */
            }
        });
    }
    $("p.page-count strong").text(recordcount);//数据总条数渲染到页面
}


































