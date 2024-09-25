$(function(){
    drawCatalog(); //目录数据
});


function drawCatalog(){
    var catalog_data = [{
        "type":"一级菜单",
        "name":"前言",
        "pagenum":4,
        "target":"target_0",
        "sub":null

    },{
        "type":"一级菜单",
        "name":"1 下载与安装",
        "pagenum":5,
        "target":"target_1",
        "sub":null

    },{
        "type":"一级菜单",
        "name":"2 E-go手机平台的功能描述",
        "pagenum":6,
        "target":"target_2",
        "sub":[{
            "type":"二级菜单",
            "name":"2.1 欢迎界面",
            "pagenum":6,
            "target":"target_2_1",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"2.2 注册与登录",
            "pagenum":7,
            "target":"target_2_2",
            "sub":[{
                "type":"三级菜单",
                "name":"2.2.1 登录",
                "pagenum":7,
                "target":"target_2_2_1",
                "sub":null
            },{
                "type":"三级菜单",
                "name":"2.2.2 注册",
                "pagenum":8,
                "target":"target_2_2_2",
                "sub":null
            }]
        }]

    },{
        "type":"一级菜单",
        "name":"3 主页",
        "pagenum":9,
        "target":"target_3",
        "sub":[{
            "type":"二级菜单",
            "name":"3.1 绑定车辆",
            "pagenum":10,
            "target":"target_3_1",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"3.2 爱车轨迹",
            "pagenum":11,
            "target":"target_3_2",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"3.3 爱车位置",
            "pagenum":12,
            "target":"target_3_3",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"3.4 电子围栏",
            "pagenum":14,
            "target":"target_3_4",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"3.5 通知公告",
            "pagenum":15,
            "target":"target_3_5",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"3.6 爱车管理",
            "pagenum":16,
            "target":"target_3_6",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"3.7 爱车服务",
            "pagenum":17,
            "target":"target_3_7",
            "sub":null
        }]

    },{
        "type":"一级菜单",
        "name":"4 控制",
        "pagenum":19,
        "target":"target_4",
        "sub":[{
            "type":"二级菜单",
            "name":"4.1 车辆状态",
            "pagenum":20,
            "target":"target_4_1",
            "sub":[{
                "type":"三级菜单",
                "name":"4.1.1 未绑定状态",
                "pagenum":20,
                "target":"target_4_1_1",
                "sub":null
            },{
                "type":"三级菜单",
                "name":"4.1.2 离线状态",
                "pagenum":21,
                "target":"target_4_1_2",
                "sub":null
            },{
                "type":"三级菜单",
                "name":"4.1.3 休眠状态",
                "pagenum":21,
                "target":"target_4_1_3",
                "sub":null
            },{
                "type":"三级菜单",
                "name":"4.1.4 在线熄火状态",
                "pagenum":22,
                "target":"target_4_1_4",
                "sub":null
            },{
                "type":"三级菜单",
                "name":"4.1.5在线点火状态",
                "pagenum":23,
                "target":"target_4_1_5",
                "sub":null
            }]
        },{
            "type":"二级菜单",
            "name":"4.2 在线点火前除霜状态",
            "pagenum":23,
            "target":"target_4_2",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"4.3 在线点火后除霜状态",
            "pagenum":24,
            "target":"target_4_3",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"4.4在线点火前后除霜状态",
            "pagenum":24,
            "target":"target_4_4",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"4.5 在线熄火状态时开启寻车功能",
            "pagenum":25,
            "target":"target_4_5",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"4.6 在线状态开启座椅加热功能",
            "pagenum":26,
            "target":"target_4_6",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"4.7 在线状态开启冷气功能",
            "pagenum":26,
            "target":"target_4_7",
            "sub":null
        },{
            "type":"二级菜单",
            "name":"4.8 已登录在线状态开启暖气功能",
            "pagenum":27,
            "target":"target_4_8",
            "sub":null
        }]

    },{
        "type":"一级菜单",
        "name":"5 功能",
        "pagenum":28,
        "target":"target_5",
        "sub":[{
            "type": "二级菜单",
            "name": "5.1 用户反馈",
            "pagenum": 28,
            "target":"target_5_1",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "5.2用车指南",
            "pagenum": 29,
            "target":"target_5_2",
            "sub": null
        }]

    },{
        "type":"一级菜单",
        "name":"6 信息",
        "pagenum":30,
        "target":"target_6",
        "sub":null

    },{
        "type":"一级菜单",
        "name":"7 E-go手机平台帐号管理描述",
        "pagenum":30,
        "target":"target_7",
        "sub":[{
            "type": "二级菜单",
            "name": "7.1 E-go手机平台注销登录",
            "pagenum": 30,
            "target":"target_7_1",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "7.2 E-go手机平台重新登录",
            "pagenum": 31,
            "target":"target_7_2",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "7.3 E-go手机平台修改密码",
            "pagenum": 32,
            "target":"target_7_3",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "7.4 E-go手机平台找回密码",
            "pagenum": 32,
            "target":"target_7_4",
            "sub": null
        }]

    },{
        "type":"一级菜单",
        "name":"8 人车绑定",
        "pagenum":33,
        "target":"target_8",
        "sub":[{
            "type": "二级菜单",
            "name": "8.1 在爱车管理中切换",
            "pagenum": 33,
            "target":"target_8_1",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "8.2 在控制界面切换",
            "pagenum": 34,
            "target":"target_8_2",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "8.3 在信息界面切换",
            "pagenum": 35,
            "target":"target_8_3",
            "sub": null
        }]

    },{
        "type":"一级菜单",
        "name":"9 版本更新",
        "pagenum":35,
        "target":"target_9",
        "sub":[{
            "type": "二级菜单",
            "name": "9.1 更新提示",
            "pagenum": 35,
            "target":"target_9_1",
            "sub": null
        },{
            "type": "二级菜单",
            "name": "9.2 更新下载",
            "pagenum": 35,
            "target":"target_9_2",
            "sub":[{
                "type":"三级菜单",
                "name":"9.2.1 IOS版软件更新",
                "pagenum":35,
                "target":"target_9_2_1",
                "sub":null
            },{
                "type":"三级菜单",
                "name":"9.2.2 Android版软件更新",
                "pagenum":36,
                "target":"target_9_2_2",
                "sub":null
            }]
        }]

    },{
        "type":"一级菜单",
        "name":"10 常见故障排除指南",
        "pagenum":36,
        "target":"target_10",
        "sub":null

    },{
        "type":"一级菜单",
        "name":"11 注意事项",
        "pagenum":37,
        "target":"target_11",
        "sub":null
    }];


    var listI_arr = [];
    for(var i=0; i<catalog_data.length; i++){
        var dataI = catalog_data[i];
        listI_arr.push('<li><a href="#'+dataI.target+'"><em class="page-num">'+dataI.pagenum+'</em><span>'+dataI.name+'</span></a>');

        //有二级菜单
        if(dataI.sub){
            var listII_arr = ['<ul class="catalog-II">'];

            for(var j=0;j<dataI.sub.length; j++){
                var dataII = dataI.sub[j];
                listII_arr.push('<li><a href="#'+dataII.target+'"><em class="page-num">'+dataII.pagenum+'</em><span>'+dataII.name+'</span></a>');

                //有三级菜单
                if(dataII.sub) {
                    var listIII_arr = ['<ul class="catalog-III">'];
                    for(var k=0; k<dataII.sub.length; k++){
                        var dataIII = dataII.sub[k];
                        listIII_arr.push('<li><a href="#'+dataIII.target+'"><em class="page-num">'+dataIII.pagenum+'</em><span>'+dataIII.name+'</span></a></li>');
                    }
                    listIII_arr.push('</ul>');
                    listII_arr.push(listIII_arr.join("")+'</li>');
                }else{
                    listII_arr.push('</li>');
                }
            }
            listII_arr.push('</ul>');
            listI_arr.push(listII_arr.join("")+'</li>');

        }else{
            listI_arr.push('</li>');
        }
    }
    $(".catalog-I").html(listI_arr.join(""));
}