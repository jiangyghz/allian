$(function () {
    pieThemeSet();//统一设置饼图主题
    ($(".column_zffs").length>0 && column_payType());//【支付分析】：柱状图
    ($(".pie_zffs").length>0 && pie_payType());//【支付分析】：饼图
    ($(".pie_addUser").length>0 && pie_addUser());//【客户管理：新增客户】：饼图
    ($(".pie_lossUser").length>0 && pie_lossUser());//【客户管理：流失客户】：饼图
    ($(".column_staffAdmin").length>0 && column_staffAdmin());//【员工管理】：柱状图
    ($(".column_businessAnalysis").length>0 && column_businessAnalysis());//【业务分析】：柱状图
    ($(".column_incomeAnalysis").length>0 && column_incomeAnalysis());//【业务分析】：柱状图
    ($(".column_satisfaction_Y").length>0 && column_satisfaction_Y());//【满意度调查：1.2月满意度调查】：柱状图
    ($(".column_satisfaction_M").length>0 && column_satisfaction_M());// 【满意度调查：2月客户满意层次比例】：柱状图
    ($(".pie_visitFailure").length>0 && pie_visitFailure());//【满意度调查：回访失败】：饼图
    ($(".column_executive").length>0 && column_executive());//【满意度调查：服务指标执行情况】：柱状图
    ($(".column_userFeedback").length>0 && column_userFeedback());//【满意度调查：客户反馈分类统计】：柱状图
    ($(".spline_countContections").length>0 && spline_countContections());//【连接数】：曲线图
    ($(".column_bC_countCars").length>0 && column_bC_countCars());//【营业对比：进厂台次】：柱状图
    ($(".column_bC_outputValue").length>0 && column_bC_outputValue());//【营业对比：产值】：柱状图
    ($(".column_bC_price").length>0 && column_bC_price());//【营业对比：客单价】：柱状图
    ($(".column_bC_grossProfit").length>0 && column_bC_grossProfit());//【营业对比：毛利】：柱状图
    ($(".column_allUsers").length>0 && column_allUsers());//【客户对比：总客户】：柱状图
    ($(".column_activeUsers").length>0 && column_activeUsers());//【客户对比：活跃客户】：柱状图
    ($(".column_lossUsers").length>0 && column_lossUsers());//【客户对比：流失客户】：柱状图
    ($(".column_lossRate").length>0 && column_lossRate());//【客户对比：流失率】：柱状图
    ($(".pie_countUsers").length > 0 && pie_countUsers(data_users));//【用户总数】：饼图
    ($(".pie_countnetworkingusers").length > 0 && pie_countnetworkingusers(data_networkingusers));//【非车联网用户总数】：饼图
    ($(".pie_countmasteraccountusers").length > 0 && pie_countmasteraccountusers(data_masteraccountusers));//【主帐号用户总数】：饼图
    ($(".pie_countsubaccountusers").length > 0 && pie_countsubaccountusers(data_subaccountusers));//【子帐号用户总数】：饼图
    ($(".pie_countDevices").length>0 && pie_countDevices(data_devices));//【设备总数】：饼图
    ($(".pie_smscountDevices").length > 0 && pie_smscountDevices(data_smsdevices));//【设备总数】：饼图
});
//考虑到外部传入标题的情况
function setTitleByOut(data){
    if(data.length>0 && data[0].title){
        return  data[0].title;
    }else{
        return false;
    }
}
//保留小数位数，四舍五入
function round(v,e){
    var t=1;
    for(;e>0;t*=10,e--);
    for(;e<0;t/=10,e++);
    return Math.round(v*t)/t;
}
//返回当月天数集合
function returnTsMonthsDays(){
    var  date = new Date();
    var  tsYear = date.getFullYear();
    var  tsMonth = date.getMonth()+1;
    var  days;
    if(tsMonth == 2){
        days= tsYear % 4 == 0 ? 29 : 28;
    }else if(tsMonth == 1 || tsMonth == 3 || tsMonth == 5 || tsMonth == 7 || tsMonth == 8 || tsMonth == 10 || tsMonth == 12){
        days= 31;
    }else{
        days= 30;
    }
    var daysArr = [];
    for(var i= 0; i<days; i++){
        daysArr.push(i+1);
    }
    //console.log(daysArr);
    return daysArr;

}
//动态读取x轴分类
function getCategories(data,colors){
    var  categories = [];
    for(var i=0;i<data.length;i++){
        categories.push(data[i].name);
        data[i].color = colors[i];
    };
    return categories;
}
//统一设置图表样式
function setCommonStyle(){
    var setCommonStyle = {};
    setCommonStyle.textStyle = {fontWeight:'bold',color:'#000',fontSize:'15px',fontFamily:'微软雅黑',tableLayout:'fixed',whiteSpace:'normal',wordWrap:'break-word',wordBreak:'break-all',display:'block'};
    setCommonStyle.colors = ["#e7673d", "#77c4f8", "#d054ce", "#5dc92a","#5380e9","#50bebe", "#a78ed7", "#ed743e","#f1af92","#d1f192","#ffdc72","#ffeb99", "#f95757", "#6ec98c", "#82914E", "#42A07B"];
    return setCommonStyle;
}
//饼图统一主题
function pieThemeSet(){
    Highcharts.theme = {
        //legend: {
        //    itemStyle: {
        //        color: '#000',
        //        lineHeight:'18px'
        //    },
        //    itemHoverStyle: {
        //        fontSize: '14px',
        //        color: '#3b52dd'
        //    },
        //    itemHiddenStyle: {
        //        color: 'silver'
        //    },
        //    floating:true,
        //    borderWidth:0,
        //    symbolWidth:14,   //图例标志的宽度
        //    symbolPadding:15,
        //    itemMarginTop:13,     //每行图例上边距
        //    layout: 'vertical',
        //    align: 'right',
        //    verticalAlign: 'top',
        //    padding:25,
        //    useHTML:true,
        //    labelFormatter: function() {
        //        return this.name + "<span style='display:inline-block;padding-left:15px;'>"+round(this.y,0) +"</span></a>";
        //        //return this.name + "<span style='display:inline-block;padding-left:15px;'>"+this.y.toFixed(0) +"</span></a>";
        //    },
        //    x:0,
        //    y:0
        //},
        credits: {
            enabled: false
        },

        tooltip:{
            borderWidth:2,
            borderRadius:5,
            shadow:false,
            color:'#565656',
            fontFamily:"Arial,'宋体'"
        }
    }

    var highchartsOptions = Highcharts.setOptions(Highcharts.theme);
}


//***** 【支付分析】：饼图 *****//
function pie_payType() {
    $('.pie_zffs').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor:null
        },
        title: {
            text: '支付分析',
            style:setCommonStyle().textStyle
        },
        tooltip: {
            formatter:function(){
                return'<b>'+this.point.name+'</b>: '+ this.y+'元';
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size:200,
                center:[80,80],
                //center:[100,100],
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            name: '支付分析',
            data: data

        }]
    });

}
//***** 【支付分析】：柱状图 *****//
function column_payType(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data,colors);
    $('.column_zffs').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "支付分析", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'元</b><br/>';
                return s;
            }
        },
        series: [{
            data: data
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}


//***** 【客户管理：新增客户】：饼图 *****//
function pie_addUser() {
    $('.pie_addUser').highcharts({
        colors:setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor:null
        },
        title: {
            text: '新增客户',
            style:setCommonStyle().textStyle
        },
        tooltip: {
            formatter:function(){
                return'<b>'+this.point.name+'人</b>: '+ this.y;
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size:200,
                center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            data: data_addUser

        }]
    });

}
//***** 【客户管理：流失客户】：饼图 *****//
function pie_lossUser() {
    $('.pie_lossUser').highcharts({
        colors:setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor:null
        },
        title: {
            text: '流失客户',
            style:setCommonStyle().textStyle
        },
        tooltip: {
            formatter:function(){
                return'<b>'+this.point.name+'人</b>: '+ this.y;
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size:200,
                center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            data: data_lossUser
        }]
    });

}


//***** 【员工管理】：柱状图 *****//
function column_staffAdmin(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data,colors);
    $('.column_staffAdmin').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "工时分析", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories:categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'</b><br/>';
                return s;
            }
        },
        series: [{
            data: data
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}


//***** 【业务分析】：柱状图 *****//
function column_businessAnalysis(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data,colors);
    $('.column_businessAnalysis').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images//column_bg.png"
        },
        title: {
            text: "业务分析", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字
            },
            categories:categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'元</b><br/>';
                return s;
            }
        },
        series: [{
            data: data
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}


//***** 【收益分析】：柱状图 *****//
function column_incomeAnalysis(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data,colors);
    $('.column_incomeAnalysis').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "收益分析", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'元</b><br/>';
                return s;
            }
        },
        series: [{
            data: data
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}


//***** 【满意度调查：上期、本期满意度调查】：柱状图 *****//
function column_satisfaction_Y(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data1,colors);
    $('.column_satisfaction_Y').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "上期、本期满意度调查", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y+' %' +'</b><br/>';
                return s;
            }
        },
        series: data1,
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【满意度调查：本期客户满意层次比例】：柱状图 *****//
function column_satisfaction_M(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data2,colors);
    $('.column_satisfaction_M').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "本期客户满意层次比例", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true,// x坐标文字
                rotation: -45,//标签旋转角度
                y:40

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y+' %' +'</b><br/>';
                return s;
            }
        },
        series: [{
            data: data2
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【满意度调查：回访失败】：饼图 *****//
function pie_visitFailure() {
    $('.pie_visitFailure').highcharts({
        colors:setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor:null
        },
        title: {
            text: '回访失败分类统计',
            style:setCommonStyle().textStyle

        },
        tooltip: {
            formatter:function(){
                return'<b>'+this.point.name+'</b>: '+ this.y;
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size:200,
                center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            data: data3
        }]
    });

}
//***** 【满意度调查：服务指标执行情况】：柱状图 *****//
function column_executive(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data4,colors);
    $('.column_executive').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "服务指标执行情况", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y+' %' +'</b><br/>';
                return s;
            }
        },
        series: [{
            data: data4
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【满意度调查：客户反馈分类统计】：柱状图 *****//
function column_userFeedback(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data5,colors);
    $('.column_userFeedback').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "客户反馈分类统计", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'</b><br/>';
                return s;
            }
        },
        series: [{
            data: data5
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}


//***** 【连接数】：曲线图 *****//
function spline_countContections(){
    $('.spline_countContections').highcharts({
        chart: {
            type: 'line'
        },
        title: {
            text: '连接数'
        },
        subtitle: {
            text: ''
        },
        xAxis: {
            categories: data.x,
            min: 0,
            max: data.x.length > 40 ? 40 : data.x.length - 1
        },
        yAxis: {
            title: {
                text: '数量'
            }
        },
        scrollbar: {
            enabled: true
        },
        plotOptions: {
            line: {
                dataLabels: {
                    // 开启数据标签
                    enabled: true
                },
                // 关闭鼠标跟踪，对应的提示框、点击事件会失效
                enableMouseTracking: false
            }
        },
        series: [{
            name: data.name,
            data: data.y
        }]
    //    chart: {
    //        type: 'spline',
    //        plotBackgroundImage: "images/column_bg.png"
    //    },
    //    title: {
    //        text: "连接数", //一级标题
    //        style:setCommonStyle().textStyle
    //    },
    //    subtitle: {
    //        text: ''
    //    },
    //    xAxis: {
    //        type: 'datetime'
    //        //categories: data.xDatetime
    //    },
    //    yAxis: {
    //        title: {
    //            text: ''
    //        },
    //        min: 0,
    //        minorGridLineWidth: 0,
    //        gridLineWidth: 0,
    //        alternateGridColor: null
    //    },
    //    tooltip: {
    //        valueSuffix: ' '
    //    },
    //    plotOptions: {
    //        spline: {
    //            lineWidth: 4,
    //            states: {
    //                hover: {
    //                    lineWidth: 5
    //                }
    //            },
    //            marker: {
    //                //enabled: false
    //            },
    //            pointInterval: data.pInterval*60000,// 10分钟一个数据点
    //            //pointInterval: 600000,// 10分钟一个数据点
    //            //pointStart: Date.UTC(2016, 3, 1, 0, 0, 0)//Date.UTC(year,month-1, day, hours, minutes, seconds, ms)
    //            pointStart: data.xDatetime//Date.UTC(year,month-1, day, hours, minutes, seconds, ms)
    //        }
    //    },
    //    series: [{
    //        name: data.name,
    //        data: data.y

    //    }]
    //    ,
    //    navigation: {
    //        menuItemStyle: {
    //            fontSize: '10px'
    //        }
    //    },
    //    credits: {
    //        enabled:false //去掉右下角highcharts.com的水印
    //    },
    //    exporting: {
    //        enabled: false
    //    }
    });

}


//***** 【营业对比：进厂台次】：柱状图 *****//
function column_bC_countCars(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data1,colors);
    $('.column_bC_countCars').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "营业对比-进厂台次", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'台</b><br/>';
                return s;
            }
        },
        series: [{
            data: data1
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【营业对比：产值】：柱状图 *****//
function column_bC_outputValue(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data2,colors);
    $('.column_bC_outputValue').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "营业对比-产值", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'元</b><br/>';
                return s;
            }
        },
        series: [{
            data: data2
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【营业对比：客单价】：柱状图 *****//
function column_bC_price(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data3,colors);
    $('.column_bC_price').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "营业对比-客单价", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'元</b><br/>';
                return s;
            }
        },
        series: [{
            data: data3
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【营业对比：毛利】：柱状图 *****//
function column_bC_grossProfit(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data4,colors);
    $('.column_bC_grossProfit').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "营业对比-毛利", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'元</b><br/>';
                return s;
            }
        },
        series: [{
            data: data4
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}


//***** 【客户对比：总客户】：柱状图 *****//
function column_allUsers(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data1,colors);
    $('.column_allUsers').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "客户对比-总客户", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'人</b><br/>';
                return s;
            }
        },
        series: [{
            data: data1
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【客户对比：活跃客户】：柱状图 *****//
function column_activeUsers(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data2,colors);
    $('.column_activeUsers').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "客户对比-活跃客户", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'人</b><br/>';
                return s;
            }
        },
        series: [{
            data: data2
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【客户对比：流失客户】：柱状图 *****//
function column_lossUsers(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data3,colors);
    $('.column_lossUsers').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "客户对比-流失客户", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +'人</b><br/>';
                return s;
            }
        },
        series: [{
            data: data3
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}
//***** 【客户对比：流失率】：柱状图 *****//
function column_lossRate(){
    var colors = setCommonStyle().colors;
    var  categories = getCategories(data4,colors);
    $('.column_lossRate').highcharts({
        chart: {
            type: 'column',
            plotBackgroundImage: "images/column_bg.png"
        },
        title: {
            text: "客户对比-流失率", //一级标题
            style:setCommonStyle().textStyle
        },
        xAxis: {
            labels: {
                enabled: true// x坐标不显示文字

            },
            categories: categories
        },
        yAxis: {
            title: {
                text: ''
            },
            labels: {
                format: '{value:.,0f}'//设置y轴数据格式,默认一千是1k，这样改了之后就是1000了
            },
            gridLineWidth:0 //图标区的横条
        },
        plotOptions: {
            column: {
                cursor: 'pointer',
                dataLabels: {
                    enabled: false, //是否显示柱状图上的数据标签
                    style: {
                        fontWeight: 'bold'
                    },
                    formatter: function() {
                        return this.y;
                    }
                }
            }
        },
        tooltip: {
            formatter: function() {
                var point = this.point,
                    s = this.x +':<b>'+ this.y +' %</b><br/>';
                return s;
            }
        },
        series: [{
            data: data4
        }],
        legend: {
            enabled:false
        },
        credits: {
            enabled:false //去掉右下角highcharts.com的水印
        },
        exporting: {
            enabled: false
        }
    })
        .highcharts(); // return chart
}

//***** 【用户和设备总数：用户总数】：饼图 *****//
function pie_countUsers(data) {
    $('.pie_countUsers').highcharts({
        colors:setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor:null
        },
        title: {
            text:  setTitleByOut(data) || '新增客户',
            useHTML:true,
            style:setCommonStyle().textStyle
        },
        tooltip: {
            //formatter:function(){
            //    return'<b>'+this.point.name+'</b>: '+ this.y+'人';
            //}
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size:200,
                //center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            name: '占比',
            type: 'pie',
            data: data

        }]
    });

}
//***** 【用户和设备总数：非车联网用户总数】：饼图 *****//
function pie_countnetworkingusers(data) {
    $('.pie_countnetworkingusers').highcharts({
        colors: setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor: null
        },
        title: {
            text: setTitleByOut(data) || '新增客户',
            useHTML: true,
            style: setCommonStyle().textStyle
        },
        tooltip: {
            //formatter: function () {
            //    return '<b>' + this.point.name + '</b>: ' + this.y + '人';
            //}
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size: 200,
                //center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            name:'占比',
            type: 'pie',
            data: data

        }]
    });

}
//***** 【用户和设备总数：主帐号用户总数】：饼图 *****//
function pie_countmasteraccountusers(data) {
    $('.pie_countmasteraccountusers').highcharts({
        colors: setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor: null
        },
        title: {
            text: setTitleByOut(data) || '新增客户',
            useHTML: true,
            style: setCommonStyle().textStyle
        },
        tooltip: {
            //formatter: function () {
            //    return '<b>' + this.point.name + '</b>: ' + this.y + '人';
            //}
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size: 200,
                //center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            name:'占比',
            type: 'pie',
            data: data

        }]
    });

}
//***** 【用户和设备总数：子帐号用户总数】：饼图 *****//
function pie_countsubaccountusers(data) {
    $('.pie_countsubaccountusers').highcharts({
        colors: setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor: null
        },
        title: {
            text: setTitleByOut(data) || '新增客户',
            useHTML: true,
            style: setCommonStyle().textStyle
        },
        tooltip: {
            //formatter: function () {
            //    return '<b>' + this.point.name + '</b>: ' + this.y + '人';
            //}
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size: 200,
                //center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            name:'占比',
            type: 'pie',
            data: data

        }]
    });

}
//***** 【用户和设备总数：实销设备总数】：饼图 *****//
function pie_countDevices(data) {
    $('.pie_countDevices').highcharts({
        colors:setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor:null
        },
        title: {
            text: setTitleByOut(data) || '流失客户',
            style:setCommonStyle().textStyle
        },
        tooltip: {
            //formatter:function(){
            //    return'<b>'+this.point.name+'</b>: '+ this.y+'人';
            //}
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size:200,
                //center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            name:'占比',
            type: 'pie',
            data: data
        }]
    });

}

//***** 【用户和设备总数：短信唤醒设备总数】：饼图 *****//
function pie_smscountDevices(data) {
    $('.pie_smscountDevices').highcharts({
        colors: setCommonStyle().colors,
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            backgroundColor: null
        },
        title: {
            text: setTitleByOut(data) || '流失客户',
            style: setCommonStyle().textStyle
        },
        tooltip: {
            //formatter: function () {
            //    return '<b>' + this.point.name + '</b>: ' + this.y + '人';
            //}
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                size: 200,
                //center:[80,80],
                showInLegend: true
            }
        },
        series: [{
            name:'占比',
            type: 'pie',
            data: data
        }]
    });

}
