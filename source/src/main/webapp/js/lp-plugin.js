//评分插件
;(function($){
    //把插件私有方法fn开头的放obj这个{}中，便于管理
    var obj = {
        //外部可传入的参数放这里
        "myDefault":{
            "gradeval":[1,2,3,4,5],                                 //默认拼分等级
            "gradecls":{
                "ulcls":"ulcls",                                    //ul样式
                "normalcls":"normal",                               //评分普通样式
                "activecls":"active",                               //评分高亮样式
                "showvalcls":null                                   //评分值样式
            },
            "onchange":null                                         //监听评分改变事件，参数是当前点击对象，我这里是ul li布局，当前点击的对象是li
        },

        //初始化评分插件
        "fnDrawGrade":function(arr,$tsinput,options){
            var ulcls = $tsinput.data("ulcls") || options.gradecls.ulcls;
            var normalcls = $tsinput.data("normalcls") || options.gradecls.normalcls;
            var activecls = $tsinput.data("activecls") || options.gradecls.activecls;
            var showvalcls = $tsinput.data("showvalcls") || options.gradecls.showvalcls;
            var deftval = $tsinput.val();

            if(typeof arr == "object" && arr.length>0){
                var htmlarr = [];
                var html = "";
                for(var i=0; i<arr.length; i++){
                    if(deftval == arr[i]){
                        html =  '<li class="'+ normalcls +' '+ activecls +'">'+ arr[i] +'</li>';
                    }else{
                        html =  '<li class="'+ normalcls +'">'+ arr[i] +'</li>';
                    }
                    htmlarr.push(html);
                }
                var $ul = $('<ul class="'+ ulcls +'"></ul>');
                $ul.html(htmlarr.join("")).after();
                $tsinput.hide().after($ul);
                //是否显示评分值
                if(showvalcls){
                    $ul.after('<span class="'+ showvalcls +'">'+ deftval +'</span>');
                }
                $tsinput.next($ul).find("."+activecls).prevAll("li").addClass(activecls);
            }
        },

        //修改评分值
        "fnUpadateGrade":function($tsinput,$ts,options){
            var tsval = $ts.text();//当前点击对象的值
            var activecls = $tsinput.data("activecls") || options.gradecls.activecls;
            $ts.siblings("li").removeClass(activecls);
            $ts.prevAll("li").addClass(activecls);
            $ts.addClass(activecls);
            $tsinput.val(tsval);
            //修改存在的评分值
            if($tsinput.next().next("span").length > 0){
                $tsinput.next().next("span").text(tsval);
            }
        }
    };

    // lpGrade()、 lpGradeDestroy() 这两个是开放给使用者的共有方法，可在外部调用的 绑定/解绑评分插件 方法
    $.fn.extend({
        //绑定评分插件
        lpGrade: function(options) {
            options = $.extend({}, obj.myDefault, options);

            //==== create grade ====
            return this.each(function () {
                var $tsinput = $(this);//调用该插件的input对象
                var value_arr = $tsinput.data("value") || options.gradeval;//评分等级数组
                if(typeof value_arr == "string"){value_arr = value_arr.split(",")};
                //初始化评分插件
                obj.fnDrawGrade(value_arr, $tsinput,options);

                //点击评分选项触发
                $tsinput.next("ul").on("click","li",function(){
                    obj.fnUpadateGrade($tsinput,$(this),options);//点击更新评分数
                    //开放给外部的返回当前点击对象的方法
                    if(options.onchange){
                        options.onchange($(this));
                    }
                });



            });
        },

        //销毁评分插件
        lpGradeDestroy: function(){
            return this.each(function(){
                var $tsinput = $(this);
                $tsinput.show().next("ul").remove().unbind("lpGrade");
            });
        }
    });
})(jQuery);

//输入框内容自动匹配
;(function($){
    $.fn.extend({
        lpAutoComplete:function(options){
            var obj = {
                "myDefault":null,


            }
            var myDefault = {
//                ,
            };

            options = $.extend({}, myDefault, options);

            return this.each(function(){

            });
        }
    });
})(jQuery);

//