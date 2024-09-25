(function(){
    $.fn.extend({
        lpStar:function(options){
            //==== set default ====
            var myDefault = {
                "commentval":[1,2,3,4,5,6,7,8,9,10]
            };
            options = $.extend({}, myDefault, options);

            //==== create star ====
            return this.each(function(){
                var $ts = $(this);
                drawStar(options.commentval,$ts);
                var $tsp = $ts.parents(".starlist");
                $tsp.on("click","li",{obj:$ts},setStar);
            });

            //==== operation function ====
            function drawStar(arr,$ts){
                $ts.wrap('<div class="starlist"></div>');
                var deftval = $ts.val();
                if(typeof arr == "object" && arr.length>0){
                    var htmlarr = [];
                    var html = "";
                    for(var i=0; i<arr.length; i++){
                        if(deftval == arr[i]){
                           html =  '<li class="active">'+ arr[i] +'</li>';
                        }else{
                            html =  '<li>'+ arr[i] +'</li>';

                        }
                        htmlarr.push(html);
                    }
                    var $ul = $('<ul></ul>');
                    $ul.html(htmlarr.join(""));
                    $ts.hide().parents(".starlist").append($ul);
                    $ts.parents(".starlist").find("li.active").prevAll("li").addClass("active");
                }
            }

            function setStar(event){
                var tsval = $(this).text();//当前点击对象的值
                var $ts = event.data.obj;//调用该插件的input对象
                $(this).siblings("li").removeClass("active");
                $(this).prevAll("li").addClass("active");
                $(this).addClass("active");
                $ts.val(tsval);
                console.log($ts.val());
            }
        }
    })
})(jQuery);

$(function(){
    ($(".star-comment").length>0 && $(".star-comment").lpStar());

})