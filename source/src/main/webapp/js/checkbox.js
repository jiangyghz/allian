$(function(){
    checkControl();
})

function checkControl(){
    var idList = filterObj(); // return #*TreeView*，#*TreeView*,#*TreeView*......
    $(idList).on("click","table input",checkAll);//全选控制列表项全选
    $(idList).on("click","div input",checkList);//列表项全选控制全选
    //$("#TreeView1,#TreeView2,#ctl00_ContentPlaceHolder1_TreeView1,#ctl00_ContentPlaceHolder1_TreeView2").on("click","table input",checkAll);//全选控制列表项全选
    //$("#TreeView1,#TreeView2,#ctl00_ContentPlaceHolder1_TreeView1,#ctl00_ContentPlaceHolder1_TreeView2").on("click","div input",checkList);//列表项全选控制全选
}

function checkAll(){
    var $this = $(this);
    var $p = $this.parent().parent().parent().parent();//获取全选的父级元素table用来通过上下文关系找next
    var $inputList = $p.next("div").find("input");
    if($(this).prop("checked")){
        $inputList.prop("checked",true);
    }else{
        $inputList.prop("checked",false);
    }
}

function checkList(){
    var $this = $(this);
    var $ckbListP = $this.parent().parent().parent().parent().parent();//获取全选的父级元素table用来通过上下文关系找prev及child
    var len = $ckbListP.find("input[type=checkbox]").length;
    var checkedLen = $ckbListP.find("input[type=checkbox]:checked").length;
    //单个联动全选
    if(len == checkedLen){
        $ckbListP.prev("table").find("input").prop("checked",true);
    }else{
        $ckbListP.prev("table").find("input").prop("checked",false);
    }
}

function filterObj(){
    //删选出div的对象，匹配id带TreeVieww关键字的推入到数组
    var objList  = document.getElementsByTagName("div");
    var divId,
        divIdStr = "",
        divIdArray = [];
    for(var i=0;i<objList.length;i++){
        divId = objList[i].id;

        //若id存在且能匹配到TreeView，则将该id推入数组中
        if(divId && divId.indexOf("TreeView") != -1){
            divIdArray.push("#"+divId);
        }
    }
    //把数组格式的id集合传化成string
    divIdStr = divIdArray.join(",");
    return divIdStr;
}


