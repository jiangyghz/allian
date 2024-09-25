//========================================================================== 地图上绘制标注点 ==========================================================================
if (document.getElementById("mapmarkers")) {
    var map = new BMap.Map("mapmarkers");                 //创建地图容器
    var point = new BMap.Point(data.clng, data.clat); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point, data.zoom);
    drawMarkers(data);
}
//draw markers on the map
function drawMarkers(data) {
    var markerArr = data.markerinfo;
    for (var i = 0; i < markerArr.length; i++) {
        addMarker(markerArr[i]);
    }
};
//create marker and label description
function addMarker(tsmarker) {
    //======= draw icon
    var m_ic = new BMap.Icon("images/icon_marker_car_32×32.png", new BMap.Size(32, 32));  //marker label
    var m_pt = new BMap.Point(tsmarker.lng, tsmarker.lat);                                //marker(lng,lat)

    //======= draw label
    var label_opts = {                                //add label to the marker
        position: m_pt,
        offset: new BMap.Size(-30, -40)
    };
    var label = new BMap.Label(tsmarker.label, label_opts);
    var focused_style = { color: "#fff", fontSize: "12px", height: "20px", lineHeight: "20px", fontFamily: "微软雅黑", "borderColor": "#1caf9a", background: '#1caf9a' };
    var nofocus_style = { color: "#fff", fontSize: "12px", height: "20px", lineHeight: "20px", fontFamily: "微软雅黑", "borderColor": "#3e4a5a", background: '#3e4a5a' };
    var label_style = tsmarker.focus ? focused_style : nofocus_style;
    label.setStyle(label_style);
    map.addOverlay(label);


    //======= draw searchInfoWindow
    var coninfo = tsmarker.content;
    var btn_focus_txt = tsmarker.focus ? '取消关注' : '关注此车';
    //var
    var conhtml = '<div class="markerbox">' +
        '<p><label>定位时间：</label>' + coninfo.loctime + '</p>' +
        '<p><label>即时速度：</label>' + coninfo.speed + '</p>' +
        '<p><label>平均油耗：</label>' + coninfo.avgoil + '</p>' +
        '<p><label>相关信息：</label>' + coninfo.others + '</p>' +
        '<p class="markerbox-btn"><a href="javascript:;" name=' + tsmarker.label + ' title=' + tsmarker.focus + ' id=' + tsmarker.tboxid + ' onclick="updateFocus(this)">' + btn_focus_txt + '</a><a name="' + tsmarker.trackurl + '" href="javascript:;" onclick="newWinShowTrack(this)">查询轨迹</a><a href="javascript:;">查询状态</a></p>' +
        '</div>';
    var searchInfoWindow = null;
    searchInfoWindow = new BMapLib.SearchInfoWindow(map, conhtml, {
        title: tsmarker.label,                       //标题
        width: 310,                                  //宽度
        height: 160,                                  //高度
        enableAutoPan: true,                          //自动平移
        searchTypes: []
    });
    var marker = new BMap.Marker(m_pt, { icon: m_ic });    //create marker
    searchInfoWindow.addEventListener("open", function (e) {
        console.log(e);
    });
    marker.addEventListener("click", function (e) {      //addEventListener on click
        searchInfoWindow.open(marker);
    });
    map.addOverlay(marker);                            //add marker on the map
}
//规定每次只能查看一个车子的轨迹，只新打开一个窗口trackWin
function newWinShowTrack(ts) {
    var url = ts.name;//get url
    window.open(url, "_blank");
    //window.open(url,"trackWin");
}
//ajax:修改收藏状态
function updateFocus(ts) {
    var labeltxt = ts.name; //tboxid
    //关注-取消:set label样式
    var focus, label_style, btn_focus_txt;

    //当前为未关注状态，点击关注高亮，按钮文字变取消
    if (ts.title == "false") {
        focus = true;
        label_style = { color: "#fff", fontSize: "12px", height: "20px", lineHeight: "20px", fontFamily: "微软雅黑", "borderColor": "#1caf9a", background: '#1caf9a' };
        btn_focus_txt = "取消关注";

        //当前为关注状态，点击取消高亮，按钮文字变回关注
    } else {
        focus = false;
        label_style = { color: "#fff", fontSize: "12px", height: "20px", lineHeight: "20px", fontFamily: "微软雅黑", "borderColor": "#3e4a5a", background: '#3e4a5a' };
        btn_focus_txt = "关注此车";
    }

    //修改点label样式
    var allOverlay = map.getOverlays();
    for (var i = 0; i < allOverlay.length - 1; i++) {
        if (allOverlay[i].toString() == "[object Label]") {
            if (allOverlay[i].content == labeltxt) {
                allOverlay[i].setStyle(label_style);
                ts.innerHTML = btn_focus_txt;
                return;
            }
        }
    }

    //关闭弹层
    $(".BMapLib_bubble_close").trigger("click");

    //打开弹层时需要更改关注状态（未完成）

    //ajax提交当前点tboxid给后台，并返回最新点信息到页面变量中
    $.ajax({
        "type": "POST",
        "url": "getMarkerInfo.aspx",
        "data": { "tboxid": ts.id, "focus": focus },  //提交tboxid 和 当前tboxid 被关注状态给后台
        "dataType": "json",
        "success": function (data) {
            console.log(data);
        },
        "error": function () {
            console.log("出错了")
        }
    })
}



var lushu;
//function start()
//{
//    lushu.start();
//}
//function stop() {
//    lushu.stop();
//}
//========================================================================== 地图上绘制行驶轨迹 ==========================================================================
if (document.getElementById("maptrack")) {
    //初始化地图（未标注点）
    var map = new BMap.Map("maptrack");
    var point2 = new BMap.Point(107.895785, 35.530622); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, 5);
    map.enableScrollWheelZoom();

    updateTrack(data);//页面一加载就渲染轨迹
}
function updateTrack(data) {
    if (data.selectedtrackinfo != "{}" && data.selectedtrackinfo != "") {
        drawTrack(map, data);          //页面一加载 初始化track
        //setTrackHL(data.selectedtrackinfo.trackid);     //高亮当前轨迹
    } else {
        resetMap(map);
    }

}

function drawTrack(map, data) {
    var point2 = new BMap.Point(data.trackinfo[0].lng, data.trackinfo[0].lat); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, data.zoom);

    //	取出坐标点
    var pointArr = data.trackinfo;
    console.log(pointArr);
    var arr = [];
    var bPoints = [];
    //控制轨迹路线坐标点位个数

    //var number = Math.floor(pointArr.length / 11);// pointArr.length < 25 ? pointArr.length : 25;
    //var p_count = pointArr.length % 11;// pointArr.length % number == 0 ? pointArr.length / number : parseInt(pointArr.length / number);

    var index = 0;//点位所处下标
    var count = 0;//点位push次数

    var lng, lat, pointobj;
    for (var i = 0; i < pointArr.length; i++) {
        if (i % 11 == 0)
        {
            lng = pointArr[i].lng; //经度
            lat = pointArr[i].lat; //纬度
            pointobj = new BMap.Point(lng, lat);
            arr.push(pointobj);
        }
        
        //if(i==0)
        //{
        //    bPoints.push(pointobj);
        //}
        //if(i==pointArr.length-1)
        //{
        //    bPoints.push(pointobj);
        //}
    }
    lng = pointArr[pointArr.length - 1].lng; //经度
    lat = pointArr[pointArr.length - 1].lat; //纬度
    pointobj = new BMap.Point(lng, lat);
    arr.push(pointobj);
    //清除地图行的覆盖物
    map.clearOverlays();
    var drv = new BMap.DrivingRoute('北京', {
        onSearchComplete: function (res) {
            if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
                var plan = res.getPlan(0);
                var arrPois = [];
                for (var j = 0; j < plan.getNumRoutes() ; j++) {
                    var route = plan.getRoute(j);
                    arrPois = arrPois.concat(route.getPath());
                }
                map.addOverlay(new BMap.Polyline(arrPois, { strokeColor: '#1cb199' }));
                map.setViewport(arrPois);//自动视野

                lushu = new BMapLib.LuShu(map, arrPois, {
                    defaultContent: "",//"从天安门到百度大厦"
                    autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                    icon: new BMap.Icon('images/carbar.png', new BMap.Size(52, 26), { anchor: new BMap.Size(27, 13) }),
                    speed: 1000,
                    enableRotation: true,//是否设置marker随着道路的走向进行旋转
                    landmarkPois: [
	                   { lng: 116.314782, lat: 39.913508, html: '加油站', pauseTime: 2 },
	                   { lng: 116.315391, lat: 39.964429, html: '高速公路收费<div><img src="http://map.baidu.com/img/logo-map.gif"/></div>', pauseTime: 3 },
	                   { lng: 116.381476, lat: 39.974073, html: '肯德基早餐<div><img src="http://ishouji.baidu.com/resource/images/map/show_pic04.gif"/></div>', pauseTime: 2 }
                    ]
                });
            }
        }
    });
    drv.search(arr[0], arr[arr.length - 1], { waypoints: arr });
    //for (var i = 0; i < number; i++) {
    //    var waypoints = arr.slice(i * 11 + 1, (i + 1) * 11 - 1);
    //    //drv.search(arr[0], arr[arr.length - 1], { waypoints: waypoints });
    //    //注意这里的终点如果是11的倍数的时候，数组可是取不到最后一位的，所以要注意终点-1喔。感谢song141的提醒，怪我太粗心大意了~
    //    drv.search(arr[i * 11], arr[(i + 1) * 11], { waypoints: waypoints });//waypoints表示途经点
    //}
    //if (p_count != 0) {
    //    var waypoints = arr.slice(number * 11, arr.length - 1);//多出的一段单独进行search
    //    drv.search(arr[number * 11 - 1], arr[arr.length - 1], { waypoints: waypoints });
    //}
    drawCharts(data.trackinfo);
    drawTrack2(data);
    //setZoom(bPoints);
    map.addEventListener("click", startlushu);//给地图注册点击事件
}
function startlushu() {
    console.log('启动路书')
    lushu.start();//启动路书函数
}
// 根据点的数组自动调整缩放级别
function setZoom(bPoints) {
    var view = map.getViewport(eval(bPoints));
    var mapZoom = view.zoom;
    var centerPoint = view.center;
    map.centerAndZoom(centerPoint, mapZoom);
}
function newPoint(pointArr) {
    var arr = [];
    for (var i = 0; i < pointArr.length; i++) {
        var lng = pointArr[i].lng; //经
        var lat = pointArr[i].lat; //纬
        var pointobj = new BMap.Point(lng, lat);
        arr.push(pointobj);
    }
    return arr;
}
function drawTrack2(data) {
    var point2 = new BMap.Point(data.clng, data.clat);      //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, data.zoom);                   //地图放大倍数
    //		var maap = new BMap.Map('map_canvas');
    map.enableScrollWheelZoom();

    for (var i in data.trackinfo) {
        var icon = "images/mapcar.png";
        //起点marker的自定义图标icon
        if (i == 0) {
            icon = "images/start.png";
            //终点marker的自定义图标icon
        } else if (i == data.trackinfo.length - 1) {
            icon = "images/end.png";
        }
        addMarkers(data.trackinfo[i], icon, i);               //添加所有标记点
    }

}
function addMarkers(data, icon, idx) {
    //【添加覆盖物】
    var point = new BMap.Point(data.lng, data.lat);
    var m_ic = new BMap.Icon(icon, new BMap.Size(32, 32));
    var marker = new BMap.Marker(point, { icon: m_ic });
    map.addOverlay(marker);                                 //增加点覆盖物
    var content = "行驶速度:" + data.speed + "km/h<br/>时间:" + data.speed.time + "<br/>地点:" + data.addr;
    marker.addEventListener("mouseover", function () {        //鼠标经过标记点位置显示信息窗口
        var infoWindow = new BMap.InfoWindow(content);      //创建信息窗口对象
        map.openInfoWindow(infoWindow, point); 	            //在point点显示信息窗口
        chart.tooltip.refresh(chart.series[0].data[idx]);   //联动charts 的tooltip

    });

}


function addMarkerStartAndEnd(map, data, pinfo) {
    var m_ic = new BMap.Icon("images/icon_marker_car_32×32.png", new BMap.Size(32, 32));
    var lb_style = { background: "#1caf9a", color: "#fff", borderWidth: "1px", borderColor: "#1caf9a", "maxWidth": "none", "padding": "3px" };
    var point = new BMap.Point(pinfo.lng, pinfo.lat);
    var marker = new BMap.Marker(point, { icon: m_ic });
    var s_conhtml = '<div class="markerbox">' +
        '<p><label>开始：</label>' + data.starttime + '</p>' +
        '<p><label>结束：</label>' + data.endtime + '</p>' +
        '<p><label>里程：</label>' + data.mileage + '</p>' +
        '<p><label>时长：</label>' + data.runtime + '</p>' +
        '</div>';
    var searchInfoWindow = null;
    searchInfoWindow = new BMapLib.SearchInfoWindow(map, s_conhtml, {
        title: pinfo.title,                 //标题
        width: 310,                                  //宽度
        height: 120,                                  //高度
        enableAutoPan: true,                          //自动平移
        searchTypes: []
    });
    marker.addEventListener("click", function (e) {      //addEventListener on click
        searchInfoWindow.open(marker);
    });
    map.addOverlay(marker);
    var label = new BMap.Label(pinfo.title, { offset: new BMap.Size(-50, -25) });
    label.setStyle(lb_style);
    marker.setLabel(label);
}
function resetMap(map) {
    var point2 = new BMap.Point(107.895785, 35.530622); //设置地图中心点经纬度（经度lng，纬度lat）
    map.centerAndZoom(point2, 5);

    //删除所有标注点
    var allOverlay = map.getOverlays();
    for (var i = 0; i < allOverlay.length; i++) {
        map.removeOverlay(allOverlay[i]);
    }
}
function setTrackHL(trackid) {
    $(".track-side table").find("tr").removeClass("active");
    var $tracklist = $(".track-side table").find(".btn-showtrack");
    for (var i = 0; i < $tracklist.length; i++) {
        var $ts_trackid = $tracklist.eq(i).attr("trackid");
        if ($ts_trackid == trackid) {
            $tracklist.eq(i).parent().parent().addClass("active");
            break;
        }
    }
}
//HeighCharts
var chart;
function drawCharts(data) {
    var data_x = [], data_y = [];
    for (var x in data) {
        data_x.push(data[x].time);
        data_y.push(data[x].speed);
    }
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'chart', //图表放置的容器，DIV 
            defaultSeriesType: 'line', //图表类型line(折线图), 
            zoomType: 'x'   //x轴方向可以缩放 
        },
        credits: {
            enabled: false   //右下角不显示LOGO 
        },
        title: {
            text: '车辆行驶速度' //图表标题 
        },
        //subtitle: {
        //    text: '2011年'  //副标题 
        //},
        xAxis: {  //x轴 
            categories: data_x, //x轴标签名称 
            gridLineWidth: 1, //设置网格宽度为1 
            lineWidth: 2,  //基线宽度 
            labels: { y: 26 }  //x轴标签位置：距X轴下方26像素 
        },
        yAxis: {  //y轴 
            title: { text: '行驶速度 (km/h)' }, //标题 
            lineWidth: 2 //基线宽度 
        },
        tooltip: {
            valueSuffix: 'km/h',
            formatter: function () {

                triggerMapShowInfoWin(this.x, data); 		// 地图map联动chart

                return '时间' + this.x + '<br/> 速度：' + this.y;
            }

        },
        plotOptions: { //设置数据点 
            line: {
                dataLabels: {
                    enabled: true  //在数据点上显示对应的数据值 
                },
                enableMouseTracking: false //取消鼠标滑向触发提示框 
            }
        },
        legend: {  //图例 
            layout: 'horizontal',  //图例显示的样式：水平（horizontal）/垂直（vertical） 
            backgroundColor: '#ffc', //图例背景色 
            align: 'left',  //图例水平对齐方式 
            verticalAlign: 'top',  //图例垂直对齐方式 
            x: 100,  //相对X位移 
            y: 70,   //相对Y位移 
            floating: true, //设置可浮动 
            shadow: true  //设置阴影 
        },
        exporting: {
            enabled: false  //设置导出按钮不可用 
        },
        series: [{  //数据列 
            name: '行驶速度',
            data: data_y
        }]
    });

    function triggerMapShowInfoWin(xval, data) {
        var obj = {};                                          //chart中鼠标hover的当前点x轴数值（唯一性）对应data中的对象
        for (var x in data) {
            if (xval == data[x].time) {
                obj = data[x];
                break;
            }
        }
        var content = '时间：' + obj.time + ';<br/>地址：' + obj.addr + ';<br/> 速度：' + obj.speed;
        var point = new BMap.Point(obj.lng, obj.lat);
        var infoWindow = new BMap.InfoWindow(content);          //创建信息窗口对象
        map.openInfoWindow(infoWindow, point);            //在point点显示信息窗口

    }

    function drawTrack2(data) {
        var point2 = new BMap.Point(data.clng, data.clat);      //设置地图中心点经纬度（经度lng，纬度lat）
        map.centerAndZoom(point2, data.zoom);                   //地图放大倍数
        //		var maap = new BMap.Map('map_canvas');
        map.enableScrollWheelZoom();
        //		map.centerAndZoom(new BMap.Point(116.404, 39.915), 13);

        //渲染所有标记点
        for (var i in data.trackinfo) {
            var icon = "images/mapcar.png";
            //起点marker的自定义图标icon
            if (i == 0) {
                icon = "images/start.png";
                //终点marker的自定义图标icon
            } else if (i == data.trackinfo.length - 1) {
                icon = "images/end.png";
            }
            addMarkers(data.trackinfo[i], i);               //添加所有标记点
        }

    }

    function addMarkers(data, icon, idx) {
        //【添加覆盖物】
        var point = new BMap.Point(data.lng, data.lat);
        var m_ic = new BMap.Icon(icon, new BMap.Size(32, 32));
        var marker = new BMap.Marker(point, { icon: m_ic });
        map.addOverlay(marker);                                 //增加点覆盖物
        var content = "行驶速度:" + data.speed + "km/h<br/>时间:" + data.speed.time + "<br/>地点:" + data.addr;
        marker.addEventListener("mouseover", function () {        //鼠标经过标记点位置显示信息窗口
            var infoWindow = new BMap.InfoWindow(content);      //创建信息窗口对象
            map.openInfoWindow(infoWindow, point); 	            //在point点显示信息窗口
            chart.tooltip.refresh(chart.series[0].data[idx]);   //联动charts 的tooltip

        });

    }
};