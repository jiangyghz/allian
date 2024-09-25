/*
* resizable
* version: 1.0.0 (07/31/2011)
* @ jQuery v1.4.*
* @ json2 *
* @ ztree v2.6.
*
* By Spark
* example $("#txtname").ztreedropdown("txtid",{ajax:true,url:"path/path"});
*/
(function ($) {
    var selectdom=new Object();
    var ztreedownObj=undefined;
    $.fn.ztreedropdown = function (datastr,options, callback) {
        var op = $.extend({ text: "",value: "",checkable:false,checkType:{"Y":"", "N":""},autoexpand:true}, options);
        return this.each(function(){
            var _this=$(this);
            var data=datastr;
            if(data.length!=0){
                $(this).data("selectdata",data);
                $(this).data("selectop",op);
            }
            var $div = $("body").find("div#dropdowndiv");
            if ($div.length == 0) {
                $("body").append("<div id=dropdowndiv />");
                $div = $("body").find("div#dropdowndiv");
                $div.append("<div id=\"ztreedropdowndiv\" class='ztreediv'><ul id=\"ztreedropdownul\" class=\"ztree\"></ul></div>");
                $div.append("<div id=\"ztreedropdownbtn\" class=\"btn\"><input type='button' id='ztreebtnsubmit' value='确定' /></div>");
                $div.css({ "display": "none", "position": "absolute","max-height":"250px", "background-color": "white", "border": "#7f9db9 1px solid", "overflow-y": "auto", "overflow-x": "hidden", "z-index": "2000" });
                $("body").bind("mousedown", function (event) {
                    if (!(event.target.id == $div[0].id || $(event.target).parents("#" + $div[0].id).length > 0)) {
                        hideMenu(ztreedownObj);
                    }
                });
            }
            $ul = $("#ztreedropdownul");
            function showMenu(){
                $div.show();
                var tops=0;
                var wh=$(window).height();
                var tt=$(_this).offset().top;
                var st=$(window).scrollTop();
                var h=$(_this).outerHeight();
                setTimeout(function(){
                    var dh=$div.height();
                    if((tt-st+dh+h)>wh){
                        tops=tt-dh;
                    }else{
                        tops=tt+h;
                    }
                    $div.css({ "left": $(_this).offset().left + "px", "top": tops + "px" });
                    $(window).unbind("keyup").bind("keyup",function(e) {
                        if($div.filter(":visible").length>0){
                            if($ul.find(".select").length>0){
                                switch(e.keyCode){
                                case 38://up
                                    var node=$ul.find("li.on");
                                    if(node.length==0){
                                        $ul.find("li.select").eq(0).addClass("on");
                                    }else{
                                        var prev=node.prev();
                                        if(prev.length!=0){
                                            node.removeClass("on");
                                            prev.eq(0).addClass("on");
                                        }
                                    }
                                break;
                                case 40://down
                                    var node=$ul.find("li.on");
                                    if(node.length==0){
                                        $ul.find("li.select").eq(0).addClass("on");
                                    }else{
                                        var next=node.next();
                                        if(next.length!=0){
                                        node.removeClass("on");
                                            next.eq(0).addClass("on");
                                        }
                                    }
                                break;
                                case 13://回车
                                    var node=$ul.find("li.on");
                                    if(node.length>0){
                                        node.triggerHandler('click');
                                    }
                                break;
                            }
                            }else{
                            switch(e.keyCode){
                                case 38://up
                                    var node=ztreedownObj.getSelectedNode();
                                    if(node==null){
                                        var nodes = ztreedownObj.getNodes();
                                        ztreedownObj.selectNode(nodes[0]);
                                    }else{
                                        var next=getPreTreeNode(ztreedownObj,node);
                                        if(next!=null){
                                            ztreedownObj.selectNode(next);
                                        }
                                    }
                                break;
                                case 40://down
                                    var node=ztreedownObj.getSelectedNode();
                                    if(node==null){
                                        var nodes = ztreedownObj.getNodes();
                                        ztreedownObj.selectNode(nodes[0]);
                                    }else{
                                        var next=getNextTreeNode(ztreedownObj,node);
                                        if(next!=null){
                                            ztreedownObj.selectNode(next);
                                        }
                                    }
                                break;
                                case 13://回车
                                    var node=ztreedownObj.getSelectedNode();
                                    if(node!=null){
                                        zTreeOnClick(e,ztreedownObj,node);
                                    }
                                break;
                            }
                            }
                        }
                    });
                },0);
            }
            function removeNode(e,pid){
                var nodes=e.getNodesByParam("pid",pid,null);
                if(nodes.length>0){
                    $.each(nodes,function(i,n){
                        removeNode(e,n.id);
                        e.removeNode(n);
                    });
                }
            }
            var thistId="";
            var findprenode=function(node){//递归找上节点
                var finlastnoe=function(n){
                    var r=n;
                    if(n.isParent){
                        return finlastnoe(n.nodes[n.nodes.length-1]);
                    }
                    return r;
                }
                return finlastnoe(node);
            };
            function getPreTreeNode(e,treeNode) {
	            if(treeNode.isParent){
                    var nodes;
                    if(treeNode.parentNode){
                        nodes=treeNode.parentNode.nodes;
                    }else{
                        nodes=ztreedownObj.getNodesByParam("level",0);
                    }
                    thistId=treeNode.tId;
                    if(treeNode.isFirstNode){
                        if(treeNode.parentNode!=null){
                            return treeNode.parentNode;
                        }else{
                            return null;//第一个
                        }
                    }else{
                        var preNode;
                        for (var i=0; i<nodes.length; i++) {
		                    if (nodes[i].tId == thistId) {
			                    break;
		                    }
		                    preNode = nodes[i];
	                    }
                        return findprenode(preNode);
                    }

                }else{
                    var nodes=treeNode.parentNode.nodes;
                    thistId=treeNode.tId;
                    if(treeNode.isFirstNode){
                        return treeNode.parentNode;
                    }
                    var preNode;
                    for (var i=0; i<nodes.length; i++) {
		                if (nodes[i].tId == thistId) {
			                return preNode;
		                }
		                preNode = nodes[i];
	                }
                }
            };
            var findnextnode=function(node){ //递归找下节点
                if(node.isLastNode){
                    return findnextnode(node.parentNode);
                }else{
                    if(node.parentNode){
                        thistId=node.parentNode.tId;
                        return node.parentNode.nodes;
                    }else{
                        thistId=node.tId;
                        return ztreedownObj.getNodesByParam("level",0);
                    }
                }
            };
            function getNextTreeNode(e,treeNode) {
                if(treeNode.isParent){
                    thistId=treeNode.nodes[0].tId;
                    return treeNode.nodes[0];
                }else{
                    var nodes=treeNode.parentNode.nodes;
                    thistId=treeNode.tId;
                    console.info(nodes);
                    if (treeNode.isLastNode) {
                        nodes=findnextnode(treeNode);
                    }
                    for (var i=0; i<nodes.length; i++) {
		                if (nodes[i].tId == thistId) {
			                return nodes[i+1];
		                }
	                }
                }
            };
            function hideMenu(e){
                $div.hide();//fadeOut("fast");
                if(e!=undefined){
                    try{
                        var nodes=e.getNodesByParam("level",0,null);
                        $.each(nodes,function(i,n){
                            removeNode(e,n.id);
                        });
                    }
                    catch(e){};
                }
            };
            function setFont(treeId, treeNode) {
                if (treeNode && treeNode.isParent) {
                    return { color: "blue" };
                } else {
                    return null;
                }
            };
            var zTreeOnClick=function(event, treeId, treeNode) {
                if (treeNode) {
                    if (treeNode.disabled == "1") {
                        alert("此项不可选");
                    } else {
                        changeval(selectdom,treeNode);
                        hideMenu(ztreedownObj);
                        if (typeof (callback) == "function") {
                            callback(treeNode);
                        }
                    }
                }
            };
            if($(_this).eq(0)[0].tagName.toLowerCase()=="label"){
                return ;
            }
            if(data[0].pid==undefined){
                $(_this).unbind("click focus").bind("click focus",function() { //点击事件
                    selectdom=this;
                    $ul.empty();
                    $div.css({"height":"auto","min-width":$(selectdom).width()});
                    var flag=false;
                    if(op.value!=""){
                        flag=true;
                    }
                    $.each($(selectdom).data("selectdata"),function(i,n){
                        if(flag){
                            $ul.append("<li class='select' k='"+n[op.value]+"'>"+n[op.text]+"</li>");
                        }else{
                            try{
                            if(n.split(":").length==2){
                                $ul.append("<li class='select' k='"+n.split(":")[1]+"'>"+n.split(":")[0]+"</li>");
                            }else{
                                $ul.append("<li class='select' k='"+n+"'>"+n+"</li>");
                            }
                            }
                            catch(ex){}
                        }
                    });
                    $ul.find("li").each(function(i){
                        $(this).click(function(){
                            $(_this).val($(this).html());
                            $(_this).attr("data-t-key",$(this).attr("k"));
                            hideMenu();
                            if (typeof (callback) == "function") {
                                callback();
                            }
                        });
                    });
                    $("#dropdowndiv .btn").hide();
                    $("#dropdowndiv ztreedropdowndiv").addClass("on");
                    showMenu();
                    $(this).data("selectdata","");
                });
            }else{
                var changeval=function(e,node){
                    $(e).val(node.name);
                    if($(e).data("bv")!=undefined&&$(e).data("bv")!=""){
                        $(e).attr("data-t-key",node[$(e).data("bv")]);
                    }else{
                        $(e).attr("data-t-key",node.id);
                    }
                }
                $(_this).unbind("click ").bind("click ",function () { //点击事件
                    selectdom=this;
                    var setting = {
                        isSimpleData: true,
                        treeNodeKey: "id",
                        treeNodeParentKey: "pid",
                        fontCss: setFont,
                        callback: {
                            beforeExpand: function () { return true; },
                            beforeCollapse: function () { return true; },
                            click: zTreeOnClick
                            //click
                        }
                    }
                    if(op.checkable){
                        setting["checkable"]=true;
                        setting["checkType"]=op.checkType;
                        $("#dropdowndiv .btn").show();
                        $("#dropdowndiv ztreedropdowndiv").removeClass("on");
                    }else{
                        setting["checkable"]=false;
                        $("#dropdowndiv .btn").hide();
                        $("#dropdowndiv ztreedropdowndiv").addClass("on");
                    }
                    $ul.empty();
                    $div.css({"height":"auto","min-width":$(selectdom).width()});
                    ztreedownObj=undefined;
                    if(ztreedownObj==undefined){
                        var d=$(selectdom).data("selectdata");
                        if(op.checkable){
                            var dft=$(_this).data("dropdownval");
                            if(dft!=undefined&&dft.length>0){
                                //console.info(d);
                                $.each(d,function(i,n){
                                    //console.info(n.id);
                                    if($.contain(dft,n.id)){
                                        //console.info(n);
                                        n.checked=true;
                                    }
                                });
                            }
                        }
                        ztreedownObj=$ul.zTree(setting, d);
                    }else{
                        //ztreeObj.updateSetting(setting);
                        ztreedownObj.refresh();
                    }
                    $("#ztreebtnsubmit").unbind("click").bind("click",function(){
                        var tmp=ztreedownObj.getCheckedNodes();
                        var ids=[];
                        var names=[];
                        var val=[];
                        for (var i=0; i<tmp.length; i++) {
				            ids.push(tmp[i].id);
				            names.push(tmp[i].name);
				            val.push(tmp[i].val);
			            }
                        //$(_this).data("dropdownval",ids);
                        hideMenu(ztreedownObj);
                        var obj=new Object();
                        obj.id=ids.join(",");
                        obj.name=names.join(",");
                        obj.val=val.join(",");
                        obj.data=tmp;
                        if (typeof (callback) == "function") {
                            callback(obj);
                        }
                    });
                    if(op.autoexpand){
                        ztreedownObj.expandAll(true);
                    }
                    showMenu();
                });
            }

        });
        };
})(jQuery); 