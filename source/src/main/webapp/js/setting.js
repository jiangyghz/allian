//========================================================================== 地图上绘制行驶轨迹 ==========================================================================
var map = new BMap.Map("map_track");                       //创建map实例，map_track是存放map的容器，初始化地图(map为全局对象)
var chart;

var lushu;
fun1();
function fun1() {
    //daima
}
	//// 实例化一个驾车导航用来生成路线
    //var drv = new BMap.DrivingRoute('北京', {
    //    onSearchComplete: function(res) {
    //        if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
    //            var plan = res.getPlan(0);
    //            var arrPois =[];
    //            for(var j=0;j<plan.getNumRoutes();j++){
    //                var route = plan.getRoute(j);
    //                arrPois= arrPois.concat(route.getPath());
    //            }
    //            map.addOverlay(new BMap.Polyline(arrPois, {strokeColor: '#111'}));
    //            map.setViewport(arrPois);
                
    //            lushu = new BMapLib.LuShu(map,arrPois,{
    //            defaultContent:"",//"从天安门到百度大厦"
    //            autoView:true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
    //            icon  : new BMap.Icon('http://www.51cargoes.com/file/car.png', new BMap.Size(52,26),{anchor : new BMap.Size(27, 13)}),
    //            speed: 4500,
    //            enableRotation:true,//是否设置marker随着道路的走向进行旋转
    //          	 landmarkPois: [
    //               { lng: 116.314782, lat: 39.913508, html: '加油站', pauseTime: 2 },
    //               { lng: 116.315391, lat: 39.964429, html: '高速公路收费<div><img src="http://map.baidu.com/img/logo-map.gif"/></div>', pauseTime: 3 },
    //               { lng: 116.381476, lat: 39.974073, html: '肯德基早餐<div><img src="http://ishouji.baidu.com/resource/images/map/show_pic04.gif"/></div>', pauseTime: 2 }
    //            ]
    //          });
    //        }
    //    }
    //});
			    
//						var nums=[]
//						var num2=new BMap.Point('120.148861','30.283693');
//						var num1=new BMap.Point('120.142069','30.283451');
//          				var num3=new BMap.Point('120.153891','30.283864');
//						var num4=new BMap.Point('120.157817','30.284028');
////						nums.push(num1);
//						nums.push(num2);
//						nums.push(num3);
////						nums.push(num4);

//		                var p1 = new BMap.Point('119.175937','36.96421');
//		                var p2 = new BMap.Point('118.991964','30.737751');
//		                drv.search(p1,p2)
//		                drv.search(num1,num4, { waypoints: nums });
//动态生成遮罩
function addLoadingMask() {
    //新建loading遮罩
    var $loadingMask = $('<div class="loading-mask"><p class="loading-maskbg"></p><div class="loading"> <img src="images/loading.gif" class="loading-pic" alt=""/><span class="loading-des">加载中......</span></div></div>');
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
function btnSeach(tbox) {
    addLoadingMask();
    $.ajax({
        type: "GET",
        url: "Handler.ashx",
        data: { "type": "getPoint", "stime": $("#reservationtime").val(), "tbox": tbox },
        dataType: "JSON",
        async: false,
        cache:false,
        error: function (data) {
            //alert("获取车辆轨迹信息失败")
            removeLoadingMask()
            console.log(data);
        },
        success: function (data) {
            if (data.trackinfo.length==0)
            {
                alert("该时间段没有轨迹信息！")
                removeLoadingMask()
                return;
            }
            var nums = [];
            var number = data.trackinfo.length < 25 ? data.trackinfo.length : 25;
            var p_count = data.trackinfo.length % number == 0 ? data.trackinfo.length / number : parseInt(data.trackinfo.length / number);

            var index = 0;//点位所处下标
            var count = 0;//点位push次数

            var lng, lat, pointobj;
            for (var i = 0; i < number - 1; i += p_count) {
                lng = data.trackinfo[i].lng; //经度
                lat = data.trackinfo[i].lat; //纬度
                pointobj = new BMap.Point(lng, lat);
                nums.push(pointobj);
            }
            lng = data.trackinfo[data.trackinfo.length - 1].lng; //终点经度
            lat = data.trackinfo[data.trackinfo.length - 1].lat; //终点纬度
            pointobj = new BMap.Point(lng, lat);
            nums.push(pointobj);
            var p1 = new BMap.Point(data.trackinfo[0].lng, data.trackinfo[0].lat);
            var p2 = new BMap.Point(data.trackinfo[data.trackinfo.length - 1].lng, data.trackinfo[data.trackinfo.length - 1].lat);
            map.clearOverlays();
            // 实例化一个驾车导航用来生成路线
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
                        map.setViewport(arrPois);

                        lushu = new BMapLib.LuShu(map, arrPois, {
                            defaultContent: "",//"从天安门到百度大厦"
                            autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                            icon: new BMap.Icon('http://www.51cargoes.com/file/car.png', new BMap.Size(52, 26), { anchor: new BMap.Size(27, 13) }),
                            speed: 4500,
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
            drv.search(p1, p2, { waypoints: nums })
            getDataByAjax(data)
            //lastmark(data.trackinfo[0].lng, data.trackinfo[0].lat);
            removeLoadingMask()

        }
    });
}

function start() {
    lushu.start();
}
function stop(){
	lushu.stop();
}
//$(function(){
//    ($("#track").length && getDataByAjax(map.chart));
//})


function getDataByAjax(data) {

    drawTrack(data);                                        //渲染map轨迹
    drawCharts(data.trackinfo);                             //渲染chart报表

}
    function drawCharts(data){
        var data_x = [],data_y = [];
        for(var x in data){
            data_x.push(data[x].time);
            data_y.push(data[x].speed);
        }
        var option = {
            title: {
                text: '车辆行驶速度'
            },
            xAxis: {
                categories: data_x,
                crosshair: {                                  //十字准星线，数组长度为1则默认绘制竖线，为2则为十字线
                    width: 1,
                    color: '#666'
                }
            },
            yAxis: {
                title: {
                    text: '行驶速度 (km/h)'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: 'km/h',
                formatter: function () {
                	
			  	triggerMapShowInfoWin(this.x,data); 		// 地图map联动chart
            	
                    return '时间' + this.x + '<br/> 速度：' + this.y;
                }

            },
            legend: {
                layout:'vertical',
                align:'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            series: [{
                name: '行驶速度',
                data: data_y
            }],
            credits: {
                enabled:false                                   //去highcharts.com水印
            },
            exporting: {
                enabled: false
            }
        };
        chart = new Highcharts.Chart('chart',option)
    }
    function drawTrack(data) {
        var point2 = new BMap.Point(data.clng, data.clat);      //设置地图中心点经纬度（经度lng，纬度lat）
        map.centerAndZoom(point2, data.zoom);                   //地图放大倍数
//		var maap = new BMap.Map('map_canvas');
		map.enableScrollWheelZoom();
//		map.centerAndZoom(new BMap.Point(116.404, 39.915), 13);
		

        //==============创建虚线轨迹
		//var trackarr = newPoint(data.trackinfo);
        ////折线覆盖物途径的点数组
        //var polyline = new BMap.Polyline(trackarr, {            //创建折线覆盖物实例，并设置样式
        //    strokeColor: "#1cb199",
        //    strokeStyle: "solid",
        //    strokeWeight: 3,
        //    strokeOpacity: 1
        //});
        //map.addOverlay(polyline);                               //增加折线覆盖物

        //实例化轨迹
		//var drv = new BMap.DrivingRoute('北京', {
		//    onSearchComplete: function (res) {
		//        if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
		//            var plan = res.getPlan(0);
		//            var arrPois = [];
		//            for (var j = 0; j < plan.getNumRoutes() ; j++) {
		//                var route = plan.getRoute(j);
		//                arrPois = arrPois.concat(route.getPath());
		//            }
		//            map.addOverlay(new BMap.Polyline(arrPois, { strokeColor: '#1cb199' }));
		//            map.setViewport(arrPois);

		//            lushu = new BMapLib.LuShu(map, arrPois, {
		//                defaultContent: "",//"从天安门到百度大厦"
		//                autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
		//                icon: new BMap.Icon('http://www.51cargoes.com/file/car.png', new BMap.Size(52, 26), { anchor: new BMap.Size(27, 13) }),
		//                speed: 4500,
		//                enableRotation: true,//是否设置marker随着道路的走向进行旋转
		//                landmarkPois: [
        //                  { lng: 116.314782, lat: 39.913508, html: '加油站', pauseTime: 2 },
        //                  { lng: 116.315391, lat: 39.964429, html: '高速公路收费<div><img src="http://map.baidu.com/img/logo-map.gif"/></div>', pauseTime: 3 },
        //                  { lng: 116.381476, lat: 39.974073, html: '肯德基早餐<div><img src="http://ishouji.baidu.com/resource/images/map/show_pic04.gif"/></div>', pauseTime: 2 }
		//                ]
		//            });
		//        }
		//    }
		//});
		
		//var p1 = new BMap.Point(data.trackinfo[0].lng, data.trackinfo[0].lat);
		//var p2 = new BMap.Point(data.trackinfo[data.trackinfo.length - 1].lng, data.trackinfo[data.trackinfo.length - 1].lat);
		//drv.search(p1, p2)
	
        //==============渲染所有标记点

		for (var i in data.trackinfo) {
            var icon = "images/car.png";
            //起点marker的自定义图标icon
            if(i==0){
                icon = "images/start.png";
                //终点marker的自定义图标icon
            }else if(i == data.trackinfo.length-1) {
                icon = "images/end.png";
            }
            addMarkers(data.trackinfo[i],icon,i);               //添加所有标记点
        }

    }
    function addMarkers(data,icon,idx){
        //【添加覆盖物】
        var point = new BMap.Point(data.lng, data.lat);
        var m_ic = new BMap.Icon(icon, new BMap.Size(32, 32));
        var marker = new BMap.Marker(point,{icon: m_ic});
        map.addOverlay(marker);                                 //增加点覆盖物
        var content = "行驶速度:"+data.speed+"km/h<br/>时间:"+data.speed.time+"<br/>地点:"+data.addr;
        marker.addEventListener("mouseover", function(){        //鼠标经过标记点位置显示信息窗口
            var infoWindow = new BMap.InfoWindow(content);      //创建信息窗口对象
            map.openInfoWindow(infoWindow,point); 	            //在point点显示信息窗口
            chart.tooltip.refresh(chart.series[0].data[idx]);   //联动charts 的tooltip
            
        });

    }
    function newPoint(pointArr) {
        //【创建虚线轨迹途径点数组】
        var arr = [];
        for (var i = 0; i < pointArr.length; i++) {
            var lng = pointArr[i].lng; //经度
            var lat = pointArr[i].lat; //纬度
            var pointobj = new BMap.Point(lng, lat);
            arr.push(pointobj);
        }
        return arr;
    }
    function triggerMapShowInfoWin(xval,data){
        //var data = {                                            //模拟数据data
        //    "clng": "120.148861",                               //map center lng
        //    "clat": "30.283693",                                //map center lat
        //    "zoom": 17,                                         //map zoom
        //    "mileage": "1.5",                                   //行驶里程（km）
        //    "runtime": "3",                                     //行驶时间 (分钟)
        //    "starttime": "2016-11-13 10:05",                    //行程开始时间
        //    "endtime": "2016-11-13 10:08",                      //行程结束时间
        //    "trackinfo": [{
        //        "lng": "120.142069",                            //起：伟星大厦
        //        "lat": "30.283451" ,
        //        "time":"2016-11-13 10:05",
        //        "addr":"伟星大厦",
        //        "speed":20
        //    }, {
        //        "lng": "120.148861", 					        //途径点经度：
        //        "lat": "30.283693" ,                            //途径点纬度
        //        "time":"2016-11-13 10:06",
        //        "addr":"某地址",
        //        "speed":50
        //    }, {
        //        "lng": "120.153891",
        //        "lat": "30.283864",
        //        "time":"2016-11-13 10:07",
        //        "addr":"某地址2",
        //        "speed":70
        //    }, {
        //        "lng": "120.157817",                            //终：莫干山路文三路口
        //        "lat": "30.284028",
        //        "time":"2016-11-13 10:08" ,
        //        "addr":"莫干山路文三路口",
        //        "speed":5
        //    }]
        //};
		
        var obj = {};                                          //chart中鼠标hover的当前点x轴数值（唯一性）对应data中的对象
        for(var x in data){
            if(xval == data[x].time){
                obj = data[x];
                break;
            }
        }
        var content = '时间：'+obj.time+';<br/>地址：'+obj.addr+';<br/> 速度：'+ obj.speed;
        var point = new BMap.Point(obj.lng, obj.lat);
        var infoWindow = new BMap.InfoWindow(content);          //创建信息窗口对象
        map.openInfoWindow(infoWindow,point);            //在point点显示信息窗口
        
    }
	
    //function lastmark(lng,lat){
    //	var point = new BMap.Point(lng,lat);
    //	var marker = new BMap.Marker(point);
    //	 var myIcon = new BMap.Icon("images/end.png", new BMap.Size(23, 25), {
    //                    offset: new BMap.Size(10, 25), // 指定定位位置
    //                    imageOffset: new BMap.Size(0, 0 - 10 * 25) // 设置图片偏移
    //                });
    //    var marker=new BMap.Marker(point,{icon:myIcon});
	//	map.addOverlay(marker);             
	//    var opts = {
	//	  width : 200,     // 信息窗口宽度
	//	  height: 100,     // 信息窗口高度
	//	  isOpen:1,
	//	  title : "海底捞王府井店"// 信息窗口标题
	//	}
	//    var end_msg='<p>停车时间:0小时 34分钟 30秒</p><p>开始时间:2017/03/26 09:53:30</p><p>结束时间:2017/03/26 10:28:00</p><p>位置:上海市 宝山区 杨行镇 共悦路26号</p><p>总里程:15061.02Km</p>'
	//	var infoWindow = new BMap.InfoWindow(end_msg,opts);  // 创建信息窗口对象 
	//	map.openInfoWindow(infoWindow,point);
	//	marker.addEventListener("click", function(){
	//		map.openInfoWindow(infoWindow,point); //开启信息窗口
	//	});
    //}
    



































































