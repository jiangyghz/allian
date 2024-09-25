var chartsArr = [];                      //用来存放需要自适应屏幕宽度的图表实例

    window.onresize = function(){
    //【***图表在浏览器缩放的时候 宽度自适应***】
        if(chartsArr==undefined){
            chartsArr=[];
        }
        for(var i in chartsArr) {
            chartsArr[i].resize();
        }
    };
    
function checkDomValid(domId){
    //【验证id=domId是否存在】
    var r = true;
    if(!document.getElementById(domId)) r = false;
    return r;
}
function setTheme(){
    var theme = {
        color1:['#0194da', '#4fde88', '#ee6167','#44ced8'], //
        color2:['#ee6167','#44ced8','#0194da', '#4fde88'],
        color3:['#f7707c','#ebc54a','#44ad60', '#2ccdcf', '#cb83e7', '#387ac5', '#ff7e50'],
        color5:['#ee6262','#ecc648','#4ee088','#bc66e0'],
        color4:['#ee6262','#2ccdcf','#1794db','#bc66e0']
    };
    return theme;
}
function setLegendName(seriesarr){
    //【***动态获取legend名称***】
    var legendArr = [];
    for(var i in seriesarr){
        if(seriesarr[i].name){
            legendArr.push(seriesarr[i].name)
        }
    }
    return legendArr;
}
function drawChart_barAndLine(domId,labels,data){
    //【***柱状+折线组合图***】
    //参数定义：图表实例id, x轴名称数组，y轴数据
    var myChart = echarts.init(document.getElementById(domId));
    var colors = ['#0194da', '#4fde88', '#ee6167','#44ced8'];
    var option = {
        color: colors,
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        grid: {
            left: '10',
            right: '10',
            bottom: '5%',
            containLabel: true
        },
        legend: {
            left:30,
            top:6,
            itemWidth:15,
            itemHeight:10,
            data:setLegendName(data)
        },
        xAxis:
            {
                type: 'category',
                splitLine:{
                    show:true
                },
                axisTick: {
                    alignWithLabel: true
                },
                data:labels
            },
        yAxis: [
            {
                type: 'value',
                name: '数量',
                offset:0,
                axisLine: {
                    lineStyle: {
                        color: colors[0]
                    }
                },
                axisTick:{
                    show:false
                }
            },
            {
                type: 'value',
                name: '百分比',
                axisLine: {
                    lineStyle: {
                        color: colors[0]
                    }
                },
                axisTick:{
                    show:false
                }
            }
        ],
        series: [
            {
                name:data[0].name,
                type:'bar',
                label:{normal:{show:true}},
                data:data[0].data
            }
        ]
    };
    if(data.length>1){
        option.series.push({
                name:data[1].name,
                type:'bar',
                label:{normal:{show:true}},
                data:data[1].data
            });
    }
    if(data.length>2){
        option.series.push({
                name:data[2].name,
                type:'line',
                yAxisIndex: 1,
                label:{normal:{show:true}},
                data:data[2].data
            });
    }
    myChart.setOption(option);
    //console.info(option);
    chartsArr.push(myChart);//把当前实例放在需要缩放自适应的图表数组中
}
function drawChart_barAndLinewbfb(domId, labels, data) {
    //【***柱状图***】
    //参数定义：图表实例id, x轴名称数组，y轴数据
    var myChart = echarts.init(document.getElementById(domId));
    var colors = ['#0194da', '#4fde88', '#ee6167', '#44ced8'];
    var option = {
        color: colors,
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        grid: {
            left: '10',
            right: '10',
            bottom: '5%',
            containLabel: true
        },
        legend: {
            left: 30,
            top: 6,
            itemWidth: 15,
            itemHeight: 10,
            data: setLegendName(data)
        },
        xAxis:
        {
            type: 'category',
            splitLine: {
                show: true
            },
            axisTick: {
                alignWithLabel: true
            },
            data: labels
        },
        yAxis: [
            {
                type: 'value',
                name: '',
                offset: 0,
                axisLine: {
                    lineStyle: {
                        color: colors[0]
                    }
                },
                axisTick: {
                    show: false
                }
            }
        ],
        series: [
            {
                name: data[0].name,
                type: 'bar',
                label: { normal: { show: true } },
                data: data[0].data
            }
        ]
    };
    if (data.length > 1) {
        option.series.push({
            name: data[1].name,
            type: 'bar',
            label: { normal: { show: true } },
            data: data[1].data
        });
    }
    if (data.length > 2) {
        option.series.push({
            name: data[2].name,
            type: 'line',
            yAxisIndex: 1,
            label: { normal: { show: true } },
            data: data[2].data
        });
    }
    myChart.setOption(option);
    //console.info(option);
    chartsArr.push(myChart);//把当前实例放在需要缩放自适应的图表数组中
}
function drawChart_barHorizontal(domId,labels,data,isShowLegend){
    //【***水平柱状图***】
    //参数定义：图表实例id, x轴名称数组，y轴数据,是否显示图例
    var myChart = echarts.init(document.getElementById(domId));
    var colors = ['#ee6167','#44ced8','#0194da', '#4fde88'];
    var option = {
        color: colors,
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            show:isShowLegend,
            left:30,
            top:0,
            itemWidth:15,
            itemHeight:10,
            data: setLegendName(data)
        },
        grid: {
            left: 10,
            right: 10,
            bottom: '3%',
            top:25,
            containLabel: true
        },
        xAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value} %'
            }
        },
        yAxis: {
            type: 'category',
            splitLine:{
                show:true
            },
            data: labels
        },
        series: [
            {
                name: data[0].name,
                type: 'bar',
                label: {
                    normal: {
                        show: true,
                        formatter: '{c}'
                    }
                },
                data:data[0].data
            }
            //,
            //{
            //    name: data[1].name,
            //    type: 'bar',
            //    label: {
            //        normal: {
            //            show: true,
            //            formatter: '{c}'
            //        }
            //    },
            //    data: data[1].data
            //},
            //{
            //    name: data[2].name,
            //    type: 'bar',
            //    label: {
            //        normal: {
            //            show: true,
            //            formatter: '{c}'
            //        }
            //    },
            //    data: data[2].data
            //}
        ]
    };
    myChart.setOption(option);
    chartsArr.push(myChart);//把当前实例放在需要缩放自适应的图表数组中
}
function drawChart_funnel(domId,data){
    //【***漏斗图***】
    //参数定义：图表实例id,y轴数据

    //个性化定义漏斗图，让漏斗呈现一个标准的倒三角，而不是梯形
    var count = 1;
    for(var k in data){
        data[k].value = count;
        count++;
    }
    // console.log(data);
    var myChart = echarts.init(document.getElementById(domId));
    var option = {
        color:setTheme().color3,
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'item',
            formatter:function(params){
                var idx = params.dataIndex;
                var count = params.data.count;
                var name = params.data.name;
                var r = "漏斗图<br/>" + name + ":" + count;
                return r;
            }
        },
        legend: {
            show:false
        },
        calculable: true,
        series: [
            {
                name:'漏斗图',
                type:'funnel',
                left: '10%',
                top: 0,
                bottom: '10',
                minSize:"12%",
                sort: 'descending',
                gap: 2,
                label: {
                    normal: {
                        show: true,
                        position: 'inside',
                        formatter:function(params){
                            var idx = params.dataIndex;
                            var count = params.data.count;
                            var name = params.data.name;
                            var r = name + ":" + count;
                            return r;
                        }
                    },
                    emphasis: {
                        textStyle: {
                            fontSize: 20
                        }
                    }
                },
                labelLine: {
                    normal: {
                        length: 10,
                        lineStyle: {
                            width: 1,
                            type: 'solid'
                        }
                    }
                },
                itemStyle: {
                    normal: {
                        borderColor: '#fff',
                        borderWidth: 1
                    }
                },
                data: data
            }
        ]
    };
    myChart.setOption(option);
    chartsArr.push(myChart);
}
function drawChart_LineArea(domId,labels,data,color){
    //【*** 折线区域图 ***】
    //参数定义：图表实例id,x轴名称数组, y轴数据 ,区域图填充颜色
    var myChart = echarts.init(document.getElementById(domId));
    var option = {
        title: {
            text: ''
        },
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        legend: {
            itemHeight:8,
            itemWidth:20,
            data: setLegendName(data)
        },
        grid: {
            left: '3%',
            right: '4%',
            top:25,
            bottom:'6%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                axisTick: {
                    interval:3
                },
                boundaryGap: false,
                splitLine:{
                    show:true
                },
                data : labels
            }
        ],
        yAxis : [
            {
                type : 'value',
            }
        ],
        series :{
            name:data[0].name,
            color:[color],
            type:'line',
            symbol:'circle',
            symbolSize:6,
            areaStyle: {normal: {
                color:color,
                opacity:0.3
            }},
            smooth:true,
            data:data[0].data
        }
    };
    myChart.setOption(option);
    chartsArr.push(myChart);
}
function drawChart_barCompare(domId,labels,data){
    //【*** 横向柱状对比图 ***】
    //参数定义：图表实例id,x轴名称数组, y轴数据
    var myChart = echarts.init(document.getElementById(domId));
    var option = {
        title: {text: ''},
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: setLegendName(data)
        },
        grid:[
            {x: '9%', y: '7%', width: '40%'},
            {x2: '9%', y: '7%', width: '40%'}
        ],
        xAxis:[{
            gridIndex:0,
            position:'top',
            axisTick:{show:false},
            axisLabel:{show:false},
            splitLine:{show:false},
            axisLine:{lineStyle:{color:'#cecdcd'}},
            type: 'value'

        },{
            gridIndex:1,
            position:'top',
            axisTick:{show:false},
            axisLabel:{show:false},
            splitLine:{show:false},
            axisLine:{lineStyle:{color:'#cecdcd'}},
            type: 'value'

        }],
        yAxis:[ {
            gridIndex:0,
            type: 'category',
            axisTick:{show:false},
            axisLabel:{
                color:function(){
                    return 'red'
                }
            },
            axisLine:{lineStyle:{color:'#333'}},
            data:labels
        },{
            gridIndex:1,
            type: 'category',
            axisTick:{show:false},
            axisLine:{lineStyle:{color:'#333'}},
            data: labels,
            show:false,
            offset:-500
        }],
        series: [
            {
                name: data[0].name,
                type: 'bar',
                xAxisIndex: 0,
                yAxisIndex: 0,
                color:[setTheme().color2[1]],
                data: data[0].data
            },
            {
                name: data[1].name,
                type: 'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                color:[setTheme().color2[0]],
                data: data[1].data
            }
        ]
    };
    myChart.setOption(option);
    chartsArr.push(myChart);
}