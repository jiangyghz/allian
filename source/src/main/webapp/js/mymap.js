//************************* 渲染地图 ******************************//
//中国地图
function chinaMap(data){
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('chinamap'));

    var option = {
        title : {
            text: data[0].title||'品牌分布',
            subtext: '',
            x:'center',
            y:35,
            textStyle:{
                fontSize:20,
                color:'#fff'
            }
        },
        tooltip : {
            trigger: 'item',//item
            backgroundColor:'rgba(0,0,0,0.8)',
            textStyle:{
                color:'#fff'
            },
            //formatter: data[0].desc+'：{a}<br /><br />{b}：{c}'
            //formatter:'{b}：{c}'
            formatter:function(params,ticket,callback){
                if(!params.value){ params.value = 0; }
                var res = params.name+'： '+ params.value;
                return res;
            }
        },
        legend: {
            orient: 'vertical',
            x:'left',
            icon: 'circle',
            data: getLegendDataArr(data),
            textStyle:{
                color:'#fff'
            }
        },
        visualMap: {  //渐变条
            min: data[0].min,
            max: data[0].max,
            x: 'left',
            y: 'bottom',
            text:['高','低'],
            calculable : true,//是否隐藏拖动手柄
            textStyle:{
                color:'#fff'
            },
            color:['#1556a1','#136bd4','#6c98d5','#9fccff','#dcecff'] //能拖动的渐变色条色值
        },
        toolbox: {
            show: false,
            orient : 'vertical',
            x: 'right',
            y: 'center',
            feature : {
                mark : {show: false},
                dataView: { show: false, readOnly: false },
                restore: { show: false },
                saveAsImage: { show: false }
            }
        },
        roamController: {
            show: true,
            x: 'right',
            mapTypeControl: {
                'china': true
            }
        },
        series :data
    };

    // 为echarts对象加载数据
    myChart.setOption(option);
    //点击高亮区域跳转到对应各省页面
    myChart.on('click', function (params) {
        var name = params.name;//省名称
        var py = provNameToPy(name);//省中文转拼音
        name = encodeURIComponent(name);//中文加密后通过url传值，防止乱码
        var url = data[0].jumpurl+'?prov='+name+'&py='+py; //eg: url = 'map-province.html?prov=河北&py=hebei';
        window.location.href=url;
    });
   
    //页面缩放图表自适应
    window.onresize = myChart.resize;
}
//省份地图
function provinceMap(data){
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('provmap'));
    var option = {
        title: {
            text: getProvNameByUrl(), //中文省份
            subtext: '',
            x: 'center',
            y: 15,
            textStyle: {
                fontSize: 20,
                color: '#fff'
            }
        },
        tooltip: {
            trigger: 'item',//item
            backgroundColor: 'rgba(0,0,0,0.8)',
            textStyle: {
                color: '#fff'
            },
            formatter:function(params,ticket,callback){
                if(!params.value){ params.value = 0; }
                var res = params.name+'： '+ params.value;
                return res;
            }
        },
        legend: {
            orient: 'vertical',
            x: 'left',
            icon: 'circle',
            data: getLegendDataArr(data),
            textStyle: {
                color: '#fff'
            }
        },
        visualMap: {  //渐变条
            min: data[0].min,
            max: data[0].max,
            x: 'left',
            y: 'bottom',
            text: ['高', '低'],
            calculable: true,//是否隐藏拖动手柄
            textStyle: {
                color: '#fff'
            },
            color: ['#1556a1', '#136bd4', '#6c98d5', '#9fccff', '#dcecff'] //能拖动的渐变色条色值
        },
        toolbox: {
            show: true,
            orient: 'vertical',
            x: 'right',
            y: 'center',
            feature: {
                mark: {show: true},
                dataView: {show: true, readOnly: false},
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        roamController: {
            show: true,
            x: 'right',
            mapTypeControl: {
                'china': true
            }
        },
        series: data
    };
    myChart.setOption(option);
    //点击高亮区域跳转到对应各省页面
    myChart.on('click', function (params) {
        //var name = params.name;//省名称
        //var py = provNameToPy(name);//省中文转拼音
        //name = encodeURIComponent(name);//中文加密后通过url传值，防止乱码
        var url = "chat_map.aspx"; //eg: url = 'map-province.html?prov=河北&py=hebei';
        window.location.href = url;
    });
    //页面缩放图表自适应
    window.onresize = myChart.resize;
}




//************************* public fun **************************//
function getLegendDataArr(data){
    //legend data 根据后台动态传入的data取值：data[i].name
    var legend_data = [];
    for(var i=0; i<data.length; i++){
        legend_data.push(data[i].name);
    }
    return  legend_data;
}
function provNameToPy(name){
    //根据省中文信息得到拼音
    var provinces = [{
        "py": 'shanghai',
        "name": "上海"
    },{
        "py": 'hebei',
        "name": "河北"
    },{
        "py": 'shanxi',
        "name": "山西"
    },{
        "py": 'neimenggu',
        "name": "内蒙古"
    },{
        "py": 'liaoning',
        "name": "辽宁"
    },{
        "py": 'jilin',
        "name": "吉林"
    },{
        "py": 'heilongjiang',
        "name": "黑龙江"
    },{
        "py": 'jiangsu',
        "name": "江苏"
    },{
        "py": 'zhejiang',
        "name": "浙江"
    },{
        "py": 'anhui',
        "name": "安徽"
    },{
        "py": 'fujian',
        "name": "福建"
    },{
        "py": 'jiangxi',
        "name": "江西"
    },{
        "py": 'shandong',
        "name": "山东"
    },{
        "py": 'henan',
        "name": "河南"
    },{
        "py": 'hubei',
        "name": "湖北"
    },{
        "py": 'hunan',
        "name": "湖南"
    },{
        "py": 'guangdong',
        "name": "广东"
    },{
        "py": 'guangxi',
        "name": "广西"
    },{
        "py": 'hainan',
        "name": "海南"
    },{
        "py": 'sichuan',
        "name": "四川"
    },{
        "py": 'guizhou',
        "name": "贵州"
    },{
        "py": 'yunnan',
        "name": "云南"
    },{
        "py": 'xizang',
        "name": "西藏"
    },{
        "py": 'shanxi1',
        "name": "陕西"
    },{
        "py": 'gansu',
        "name": "甘肃"
    },{
        "py": 'qinghai',
        "name": "青海"
    },{
        "py": 'ningxia',
        "name": "宁夏"
    },{
        "py": 'xinjiang',
        "name": "新疆"
    },{
        "py": 'beijing',
        "name": "北京"
    },{
        "py": 'tianjin',
        "name": "天津"
    },{
        "py": 'chongqing',
        "name": "重庆"
    },{
        "py": 'xianggang',
        "name": "香港"
    }, {
        "py": 'aomen',
        "name": "澳门"
    }];
    var py;
    for(var x in provinces){
        //匹配省份名
        if(provinces[x].name == name){
            py = provinces[x].py;//返回对应省拼音
        }
    }
    return py;
}
function getProvNameByUrl(){
    var url = window.location.href;//eg: url = 'map-province.html?prov=河北&py=hebei';
    var start = url.indexOf("prov=");
    var end = url.indexOf("&py=");
    var name = url.substring(start+5,end); //得到当前省
    name = decodeURIComponent(name);//解密
    return name;
}
//模拟后台数据：随机数
function randomData() { return Math.round(Math.random()*1000);}








