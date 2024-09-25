$(document).ready(function () {


    //=======全选


    function initTableCheckbox() {
        var $thr = $('.o-checkbox table thead tr');
        var $checkAllTh = $('<th><input type="checkbox" id="checkAll" name="checkAll" /></th>');
        /*将全选/反选复选框添加到表头最前，即增加一列*/
        $thr.prepend($checkAllTh);
        /*“全选/反选”复选框*/
        var $checkAll = $thr.find('input');
        $checkAll.click(function (event) {
            /*将所有行的选中状态设成全选框的选中状态*/
            $tbr.find('input').prop('checked', $(this).prop('checked'));
            /*并调整所有选中行的CSS样式*/
            if ($(this).prop('checked')) {
                $tbr.find('input').parent().parent().addClass('warning');
            } else {
                $tbr.find('input').parent().parent().removeClass('warning');
            }
            /*阻止向上冒泡，以防再次触发点击操作*/
            event.stopPropagation();
        });
        /*点击全选框所在单元格时也触发全选框的点击操作*/
        $checkAllTh.click(function () {
            $(this).find('input').click();
        });
        var $tbr = $('.o-checkbox table tbody tr');
        var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
        /*每一行都在最前面插入一个选中复选框的单元格*/
        $tbr.prepend($checkItemTd);
        /*点击每一行的选中复选框时*/
        $tbr.find('input').click(function (event) {
            /*调整选中行的CSS样式*/
            $(this).parent().parent().toggleClass('warning');
            /*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/
            $checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
            /*阻止向上冒泡，以防再次触发点击操作*/
            event.stopPropagation();
        });
        /*点击每一行时也触发该行的选中操作*/
        $tbr.click(function () {
            $(this).find('input').click();
        });
    }
    initTableCheckbox();


    //=======menu
    $(function () {
        var Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;

            // Variables privadas
            var links = this.el.find('.link');
            // Evento
            links.on('click', {
                el: this.el,
                multiple: this.multiple
            }, this.dropdown)
        }

        Accordion.prototype.dropdown = function (e) {
            var $el = e.data.el;
            $this = $(this),
                $next = $this.next();

            $next.slideToggle();
            $this.parent().toggleClass('open');

            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp().parent().removeClass('open');
            };
        }

        var accordion = new Accordion($('#accordion'), false);
    });

    //=======main-chart
})

//======信息汇总
$(function () {
    if ($('#new_container').length != 0) {


        //=======新增人数
        $('#new_container').highcharts({
            chart: {
                type: 'line'
            },
            title: {
                text: '最新一月粉丝增加情况'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: list1
            },
            yAxis: {
                title: {
                    text: '粉丝数量'
                }
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: true
                    },
                    enableMouseTracking: false
                }
            },
            series: [{
                    name: '新增数量',
                    data: data3
                },
                // {
                //     name: 'London',
                //     data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
                // }
            ]
        });
    }
    //=======取消关注数
    if ($('#cancel_container').length != 0) {
        $('#cancel_container').highcharts({
            chart: {
                type: 'line'
            },
            title: {
                text: '最新一月粉丝取消关注情况'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: list1
            },
            yAxis: {
                title: {
                    text: '粉丝数量'
                }
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: true
                    },
                    enableMouseTracking: false
                }
            },
            series: [{
                    name: '取消关注数量',
                    data: data2
                },
                // {
                //     name: 'London',
                //     data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
                // }
            ]
        });
    }
    //=======净增人数
    if ($('#net_container').length != 0) {
        $('#net_container').highcharts({
            chart: {
                type: 'line'
            },
            title: {
                text: '最新一月粉丝净增情况'
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: list1
            },
            yAxis: {
                title: {
                    text: '粉丝数量'
                }
            },
            plotOptions: {
                line: {
                    dataLabels: {
                        enabled: true
                    },
                    enableMouseTracking: false
                }
            },
            series: [{
                    name: '新增数量',
                    data: data4
                },
                // {
                //     name: 'London',
                //     data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
                // }
            ]
        });
    }
    //=======累计人数
    if ($('#cum_container').length != 0) {
        $('#cum_container').highcharts({
            chart: {
                type: 'line'
            },
            title: {
                text: '最新一月粉丝累计人数'
            },
            xAxis: {
                categories: list1,
                crosshair: true
            },
            yAxis: {
                min: 0,
                title: {
                    text: '数量 (人)'
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y:.1f} 人</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            series: [{
                name: '数量',
                data: data1

            }]
        });
    }
    //======柱状图    
    if ($('#messageamount_bar').length != 0) {
        $('#messageamount_bar').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '信息汇总'
            },
            xAxis: {
                categories: [
                    '二手车咨询',
                    '事故估价',
                    '维修预约',
                    '文字信息',
                    '语音信息',
                    '预约试驾',
                    '合计',
                ],
                crosshair: true
            },
            yAxis: {
                min: 0,
                title: {
                    text: '数量 (人)'
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y:.1f} 人</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            series: [{
                name: '接受数量',
                data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, ]

            }, {
                name: '回复数量',
                data: [83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, ]

            }, {
                name: '接收客户数',
                data: [48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, ]

            }, {
                name: '回复客户数',
                data: [42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, ]

            }]
        });
    }
    //=====饼图
    if ($('#mainactionReport_bar').length != 0) {
        $('#mainactionReport_bar').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                type: 'pie'
            },
            title: {
                text: 'Browser market shares January, 2015 to May, 2015'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    }
                }
            },
            series: [{
                name: 'Brands',
                colorByPoint: true,
                data: [{
                    name: '发送数',
                    y: 56.33
                }, {
                    name: '完成数',
                    y: 24.03,
                    sliced: true,
                    selected: true
                }]
            }]
        });
    }
    if ($('#sumanswer_bar').length != 0) {
        $('#sumanswer_bar').highcharts({
            chart: {
                type: 'bar'
            },
            title: {
                text: '满意度汇总'
            },
            xAxis: {
                categories: ['1、销售人员接待和服务质量', '2、销售人员接待的及时性', '3、车辆的介绍', '4、销售人员对您疑问的响应速度', '5、销售人员是否邀请您进行试乘试驾', '6、是否在承诺时间内交车', '7、经销商交车服务', '8、完成所有书面文件流程的及时性', '9、销售顾问对购车过程中所有费用的解释', '10、销售顾问进行新车配置的解释', '11、交车后是否回访客户满意度情况', '12、总体满意度', '13、服务人员积极倾听需求或期望并响应', '14、客户休息区舒适（包括座椅、娱乐设施、饮料点心', '15、交车过程的等待时间', '16、周到的协助提车（如协助找到车辆、付款等）', '17、服务站正确地完成维修保养', '18、车辆在服务站停留的时间', '19、车辆在服务站停留的时间', '20、车辆交付时的整洁程度', '21、维修保养工作的性价比', '22、是否因为上次维修没有完成导致这次到店?'],
                title: {
                    text: null
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: '满意度',
                    align: 'high'
                },
                labels: {
                    overflow: 'justify'
                }
            },
            tooltip: {
                valueSuffix: ''
            },
            plotOptions: {
                bar: {
                    dataLabels: {
                        enabled: true
                    }
                },
                series: {
                    pointPadding: 0, //数据点之间的距离值
                    groupPadding: 0, //分组之间的距离值
                    borderWidth: 0,
                    shadow: false,
                    pointWidth: 5 //柱子之间的距离值
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -40,
                y: 80,
                floating: true,
                borderWidth: 1,
                backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
                shadow: true
            },
            credits: {
                enabled: false
            },
            series: [{
                name: '非常满意 10分',
                data: [5, 3, 2, 2, 2, 5, 3, 2, 2, 2, 5, 3, 2, 2, 2, 5, 3, 2, 2, 2, 5, 3, ]
            }, {
                name: '满意 9分',
                data: [2, 3, 3, 3, 2, 2, 3, 3, 3, 2, 2, 3, 3, 3, 2, 2, 3, 3, 3, 2, 2, 3, ]
            }, {
                name: '一般 8~6分',
                data: [0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, ]
            }, {
                name: '不满意 5~0分',
                data: [0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, ]
            }]
        });
    }
    if ($('#sumanswer').length != 0) {
        $('#sumanswer').highcharts({
            chart: {
                type: 'bar'
            },
            title: {
                text: '满意度'
            },
            xAxis: {
                labels: {
                    enable: true,
                    rotation: -15, //旋转,效果就是影响标签的显示方向
                    step: 1,
                },
                categories: ['1、销售人员接待和服务质量', '2、销售人员接待的及时性', '3、车辆的介绍', '4、销售人员对您疑问的响应速度', '5、销售人员是否邀请您进行试乘试驾', '6、是否在承诺时间内交车', '7、经销商交车服务', '8、完成所有书面文件流程的及时性', '9、销售顾问对购车过程中所有费用的解释', '10、销售顾问进行新车配置的解释', '11、交车后是否回访客户满意度情况', '12、总体满意度', '13、服务人员积极倾听需求或期望并响应', '14、客户休息区舒适（包括座椅、娱乐设施、饮料点心', '15、交车过程的等待时间', '16、周到的协助提车（如协助找到车辆、付款等）', '17、服务站正确地完成维修保养', '18、车辆在服务站停留的时间', '19、车辆在服务站停留的时间', '20、车辆交付时的整洁程度', '21、维修保养工作的性价比', '22、是否因为上次维修没有完成导致这次到店?', ]

            },
            yAxis: {
                min: 0,
                allowDecimals: false,
                tickPositions: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                title: {
                    text: ''
                }
            },
            legend: {
                reversed: true
            },
            plotOptions: {
                series: {
                    stacking: 'normal'
                },
                column: {
                    pointPadding: 10,
                    pointWidth: 5, //柱子的宽度30px
                }

            },
            series: [{
                name: '非常满意 10分',
                data: [5, 3, 2, 2, 2, 5, 3, 2, 2, 2, 5, 3, 2, 2, 2, 5, 3, 2, 2, 2, 5, 3, ]
            }, {
                name: '满意 9分',
                data: [2, 3, 3, 3, 2, 2, 3, 3, 3, 2, 2, 3, 3, 3, 2, 2, 3, 3, 3, 2, 2, 3, ]
            }, {
                name: '一般 8~6分',
                data: [0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, ]
            }, {
                name: '不满意 5~0分',
                data: [0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, ]
            }]
        });
    }
    //=========主题活动统计
    if ($('#mainactionReport_bar').length != 0) {
        $('#mainactionReport_bar').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '洗车券（服务部）'
            },
            xAxis: {
                categories: [
                    '发送数', '完成数',
                ],
                crosshair: true
            },
            yAxis: {
                min: 0,
                allowDecimals: false,
                title: {
                    text: '数量 (个)'
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y:.1f} 个</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            series: [{
                name: '发送数量',
                data: [5, 1, ]

            }]
        });
    }
    if ($('#mainactionReport_bar2').length != 0) {
        $('#mainactionReport_bar2').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: '洗车券（销售部）'
            },
            xAxis: {
                categories: [
                    '发送数', '完成数',
                ],
                crosshair: true
            },
            yAxis: {
                min: 0,
                allowDecimals: false,
                title: {
                    text: '数量 (个)'
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y:.1f} 个</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            series: [{
                name: '发送数量',
                data: [5, 1, ]

            }]
        });
    }
});