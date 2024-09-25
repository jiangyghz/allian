//密码策略
(function($){
    var acceptpwdlv=3;
    //启用密码验证
    $.fn.pwdstrategy=function(){
        var aLvTxt = ['','低','中','高'];
        var init=function(val){
            var lv = 0;
            if(val.match(/[a-z]/g)){lv++;}
            if(val.match(/[0-9]/g)){lv++;}
            if(val.match(/(.[^a-z0-9])/g)){lv++;}
            if(val.length < 6){lv=1;}
            if(lv > 3){lv=3;}
            return lv;
        };
        this.each(function(i,n){
            var span=$(this).next(".pwdstr");
            if(span.length==0){
                $(this).after("<span class='pwdstr' style='margin:0 0 0 5px;'>密码强度:<span class='pwdlength' style='height:8px;width:120px;border:1px solid #ccc;padding:0px;line-height:0px;display:inline-block;'><strong style='height:6px; display:inline-block;line-height:0px;'></strong></span></span>");
                span=$(this).next(".pwdstr");
            }
            $(this).bind("keyup",function(){
                var lv=init($(this).val());
                $(this).data("pwdstrat",lv);
                if(lv>=3&&lv.length<8){//必须>=8位
                    lv=2;
                }
                var color="red";
                switch(lv){
                    case 2:
                        color="orange";
                    break;
                    case 3:
                        color="green";
                    break;
                }
                span.find("strong").css({"background":color});
                var len=lv==0?0:(120/3*lv);
                span.find("strong").width(len).attr("title",aLvTxt[lv]);
            });
        });
    };
    //是否通过验证
    $.fn.pwdpass=function(){
        return $(this).data("pwdstrat")>=acceptpwdlv&&$(this).val().length>=8;
    };
})(jQuery);