var pickdate = Vue.extend({
    template: "#pickdate",
    props: {
        pickStyle: {
            type: Object,
            default: function () {
                return {}
            }
        },
        pickDate: {
            type: String,
            default: ""
        },
        minDate: {
            type: String,
            default: "1991-12-26"
        },
        maxDate: {
            type: String,
            default: "2050-12-12"
        },
        isChangeYear: {
            type: Boolean,
            default: false
        },
        isEdit: {
            type: Boolean,
            default: false
        },
        isTime: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            monthString: 1,
            days: [],
            year: "",
            pickYear: "",
            month: "",
            pickMonth: "",
            day: "",
            sltDay: "",
            pickDay: "",
            today: "",
            hour: "",
            minute: "",
            second: "",
            months: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'],
            weeks: ['日', '一', '二', '三', '四', '五', '六']
        }
    },
    computed: {
        arrowLeft() {
            if (this.isChangeYear) {
                return {
                    left: "20px"
                }
            } else {
                return {
                    left: "10px"
                }
            }
        },
        arrowRight() {
            if (this.isChangeYear) {
                return {
                    right: "20px"
                }
            } else {
                return {
                    right: "10px"
                }
            }
        },
        minYear() {
            if (this.minDate.split('-') != -1) {
                return this.minDate.split('-')[0];
            } else {
                return this.minDate.split('/')[0];
            }
        },
        minMonth() {
            if (this.minDate.split('-') != -1) {
                return this.minDate.split('-')[1];
            } else {
                return this.minDate.split('/')[1];
            }
        },
        minDay() {
            if (this.minDate.split('-') != -1) {
                return this.minDate.split('-')[2] == undefined ? "" : this.minDate.split('-')[2];
            } else {
                return this.minDate.split('/')[2] == undefined ? "" : this.minDate.split('/')[2];
            }
        },
        disPrev() {
            return this.minYear >= this.year && parseInt(this.minMonth) - 1 >= this.month
        },
        maxYear() {
            if (this.maxDate.split('-') != -1) {
                return this.maxDate.split('-')[0];
            } else {
                return this.maxDate.split('/')[0];
            }
        },
        maxMonth() {
            if (this.maxDate.split('-') != -1) {
                return this.maxDate.split('-')[1];
            } else {
                return this.maxDate.split('/')[1];
            }
        },
        maxDay() {
            if (this.maxDate.split('-') != -1) {
                return this.maxDate.split('-')[2] == undefined ? "" : this.maxDate.split('-')[2];
            } else {
                return this.maxDate.split('/')[2] == undefined ? "" : this.maxDate.split('/')[2];
            }
        },
        disNext() {
            return this.maxYear <= this.year && parseInt(this.maxMonth) - 1 <= this.month
        }
    },
    methods: {
        cancelFn() {
            this.$emit('cancelPick');
        },
        paddingZero(val) {
            return parseInt(val) < 10 ? "0" + parseInt(val) : parseInt(val)
        },
        clearDate() {
            this.$emit('clearDate');
        },
        sltToday() {
            var now = new Date();
            this.year = now.getFullYear();
            this.month = now.getMonth();
            this.sltDay = this.paddingZero(now.getDate());
            this.monthString = this.months[this.month];
            this.render(this.year, this.month);
        },
        changeYear() {
            this.render(this.year, this.month);
        },
        init() {

            var now = new Date();
            var splitDate = [];
            if (this.pickDate.split(" ")[0] != undefined) {
                if (this.pickDate.split(" ")[0].indexOf('-') != -1) {
                    splitDate = this.pickDate.split(" ")[0].split("-");
                } else if (this.pickDate.split(" ")[0].indexOf('/') != -1) {
                    splitDate = this.pickDate.split(" ")[0].split("/");
                }
            }
            if (splitDate.length >= 2) {
                this.year = parseInt(splitDate[0]);
                this.month = parseInt(splitDate[1]) - 1;
                this.pickYear = parseInt(splitDate[0]);
                this.pickMonth = parseInt(splitDate[1]) - 1;
                this.pickDay = parseInt(splitDate[2]);
            } else {
                this.year = now.getFullYear();
                this.month = now.getMonth();
                this.day = now.getDate();
                this.sltDay = this.paddingZero(now.getDate());
            }
            if (this.pickDate.split(" ")[1] != undefined) {
                this.hour = this.pickDate.split(" ")[1].split(":")[0];
                this.minute = this.pickDate.split(" ")[1].split(":")[1];
                this.second = this.pickDate.split(" ")[1].split(":")[2];
            } else {
                this.hour = this.paddingZero(now.getHours());
                this.minute = this.paddingZero(now.getMinutes());
                this.second = this.paddingZero(now.getSeconds());
            }
            this.monthString = this.months[this.month];
            this.render(this.year, this.month);
        },
        render(y, m) {
            var firstDayOfMonth = new Date(y, m, 1).getDay()         //当月第一天
            var lastDateOfMonth = new Date(y, m + 1, 0).getDate()    //当月最后一天
            var lastDayOfLastMonth = new Date(y, m, 0).getDate()     //上个月的最后一天
            this.year = y
            var i, line = 0, temp = []
            for (i = 1; i <= lastDateOfMonth; i++) {
                var dow = new Date(y, m, i).getDay()
                // 第一行
                if (dow == 0) {
                    temp[line] = []
                } else if (i == 1) {
                    temp[line] = []
                    var k = lastDayOfLastMonth - firstDayOfMonth + 1
                    for (var j = 0; j < firstDayOfMonth; j++) {
                        temp[line].push({
                            day: k,
                            prevMonth: true,
                            disabled: true,
                            selected: false
                        })
                        k++;
                    }
                }
                if (i <= this.minDay && parseInt(this.minMonth) - 1 == this.month && this.minYear == this.year || i >= this.maxDay && parseInt(this.maxMonth) - 1 == this.month && this.maxYear == this.year) {
                    temp[line].push({
                        day: i,
                        disabled: true,
                        selected: false
                    })
                } else {
                    temp[line].push({
                        day: i,
                        disabled: false,
                        selected: false
                    })
                }

                var chk = new Date()
                var chkY = chk.getFullYear()
                var chkM = chk.getMonth()

                if (chkY == this.year && chkM == this.month && i == this.day) {
                    temp[line].pop();
                    temp[line].push({
                        day: i,
                        today: true,
                        selected: false
                    })
                    this.today = [line, temp[line].length - 1]
                }
                if (i == this.pickDay && this.pickMonth == this.month && this.pickYear == this.year) {
                    temp[line].pop();
                    temp[line].push({
                        day: i,
                        selected: true
                    })
                }
                // 最后一行
                if (dow == 6) {
                    line++
                } else if (i == lastDateOfMonth) {
                    var k = 1
                    for (dow; dow < 6; dow++) {
                        temp[line].push({
                            day: k,
                            nextMonth: true,
                            disabled: true,
                            selected: false
                        })
                        k++
                    }
                }
            }
            //end for
            this.days = temp
        },
        prev() {
            if (!this.disPrev) {
                if (--this.month <= -1) {
                    this.year--;
                    this.month = 11;
                }
                this.render(this.year, this.month);
                this.monthString = this.months[this.month];
            }
        },
        prevYear() {
            this.render(--this.year, this.month);
        },
        nextYear() {
            this.render(++this.year, this.month);
        },
        next() {
            if (!this.disNext) {
                if (++this.month >= 12) {
                    this.month = 0;
                    this.year++;
                }
                this.render(this.year, this.month);
                this.monthString = this.months[this.month];
            }
        },
        select(k1, k2, next, prev, dis) {
            if (dis) {
                //              return;
            }
            for (var i = 0; i < this.days.length; i++) {
                for (var j = 0; j < this.days[i].length; j++) {
                    this.days[i][j].selected = false;
                }
            }
            this.$set(this.days[k1][k2], 'selected', true);
            var formatDate = "";
            this.sltDay = this.paddingZero(this.days[k1][k2].day);
            if (!next && !prev) {
                if (this.pickDate.indexOf('/') != -1) {
                    formatDate = this.year + "/" + this.paddingZero(this.month + 1) + "/" + this.sltDay;
                } else {
                    formatDate = this.year + "-" + this.paddingZero(this.month + 1) + "-" + this.sltDay;
                }
            } else if (next) {
                if (this.pickDate.indexOf('/') != -1) {
                    formatDate = this.year + "/" + this.paddingZero(this.month + 2) + "/" + this.sltDay;
                } else {
                    formatDate = this.year + "-" + this.paddingZero(this.month + 2) + "-" + this.sltDay;
                }
            } else if (prev) {
                if (this.pickDate.indexOf('/') != -1) {
                    formatDate = this.year + "/" + this.paddingZero(this.month) + "/" + this.sltDay;
                } else {
                    formatDate = this.year + "-" + this.paddingZero(this.month) + "-" + this.sltDay;
                }
            }
            if (!this.isTime) {
                this.$emit('output', formatDate);
            }
        },
        sureFn() {
            var formatDate = "";
            if (this.pickDate.indexOf('/') != -1) {
                formatDate = this.year + "/" + this.paddingZero(this.month + 1) + "/" + this.sltDay + " " + this.paddingZero(this.hour) + ":" + this.paddingZero(this.minute) + ":" + this.paddingZero(this.second);
            } else {
                formatDate = this.year + "-" + this.paddingZero(this.month + 1) + "-" + this.sltDay + " " + this.paddingZero(this.hour) + ":" + this.paddingZero(this.minute) + ":" + this.paddingZero(this.second);
            }
            this.$emit('output', formatDate);
        }
    },
    watch: {
        pickDate: function (value) {
            this.init()
        },
        hour(newVal, oldVal) {
            if (isNaN(newVal) && newVal != "" || newVal < 0) {
                this.hour = 0;
            }
            if (newVal >= 23) {
                this.hour = 23;
            }
        },
        minute(newVal, oldVal) {
            if (isNaN(newVal) && newVal != "" || newVal < 0) {
                this.minute = 0;
            }
            if (newVal >= 59) {
                this.minute = 59;
            }
        },
        second(newVal, oldVal) {
            if (isNaN(newVal) && newVal != "" || newVal < 0) {
                this.second = 0;
            }
            if (newVal >= 59) {
                this.second = 59;
            }
        }
    },
    mounted() {
        // alert()
        this.init()
    }
});




var vm_shopadmin = new Vue({
    el: "#app_shopadmin ",
    data: function () {
        return {
            changeDate: "",
            startDate: "",
            content: "",
            startContent: "",
            title: "",
            tip: "",
            isPlan: "",
            isRecord: "",
            isChangeYear: false,
            pickDate: "",
            pickShow: false,
            isEdit: false,
            isTime: false,
            pickStyle: {},
            date1: "2017-05-01",
            date2: "2017-05-02",
            date3: "2017-05-03",
            cid: "",
            isedit: 0,           //当前是否为可编辑状态
            picBackup: "",
            delpicurl: "",        //保存时需要删除的图片url
            tipsMsg: "",           //tipsMsg
            provincelist: "",     //省 
            citylist: "",         //市 
            arealist: "",         //区 
            pcode: "33",
            ccode: "3301",
            picFileName: [],
            userinfo: "",//门店基本信息
            srjg: "",//收入结构
            storecontact: [],//门店联系方式
            zone: "",//省公司列表
            user: "",//用户列表
            CustomerLevel: "",//客户类型
            JoiningType: "",//加盟方式
            jyzd: "",//经营诊断
            jyxq: "",//经营详情
            xdjl: ""//巡店记录
        }
    },
    components: {
        "v-pickdate": pickdate
    },
    beforeMount: function () {
        var ts = this;
        if(getUrl().cid){
            ts.cid = getUrl().cid; //解析url,截取cid,渲染到data中
        }else{
            alert("抱歉，请先登录！")
        }
        getShopInfo(ts); //请求门店信息（分多次请求，部分计算的数据会影响渲染时间，故分批请求）
    },
    methods: {
        clearDate() {
            this.date = "";
            this.pickShow = false;
        },
        writePut(date,datetype) {
            this.changeDate = date;
            this[datetype] = date;
            this.pickShow = false;
        },
        hidePick() {
            this.pickShow = false;
        },
        selectPick(e,datatype) {
            this.pickShow = !this.pickShow;
            this.pickStyle = {
                left: e.target.offsetLeft + "px",
                top: e.target.offsetHeight + e.target.offsetTop + 2 + "px"
            };
            this.pickDate = e.target.value;
            console.log(e.target.value);
        },
        funSetTxtByVal(opts, optval, opttxt, dataval, datatxt) {
            //opts(string):需要匹配的json数组名称
            //optval(string)：opts里value对应的key名
            //opttxt(string)：opts里txt对应的key名
            //dataval(string)：data里value对应的key名
            //datatxt(string)：data里txt对应的key名
            var vm = vm_shopadmin;
            //【根据value赋值对应的txt】
            for (var x in this[opts]) {
                console.log("当前循环体中的value:" + vm[opts][x][optval]);
                console.log("当前选中的value:" + vm.userinfo[dataval]);
                if (vm[opts][x][optval] == vm.userinfo[dataval]) {
                    vm.userinfo[datatxt] = vm[opts][x][opttxt];
                    break;
                }
            }
        },
        funSetEdit: function () {
            var ts = this;
            //【修改编辑状态】
            this.isedit = !this.isedit;
            //若放弃了修改，则重新获取数据
            if (!this.isedit) {
                var ts = this;
                getShopInfo(ts); //请求门店信息（分两次请求，一次是基本数据，一次是需要计算的数据会比较慢的）
            } else {
                this.funGetProinceList(callOkGetProvince, 0);//自动获取省市区数据
                //获取省后回调，若有匹配的省，则获取pcode,用于后续的自动获取市
                function callOkGetProvince() {
                    if (ts.funSetCode('province', 'pcode')) {
                        ts.funGetCityList(callOkGetCity, 0);
                    } else {
                        ts.citylist = "";
                    }
                }
                //获取市后回调，若有匹配的市，则获取ccode,用于后续的自动获取区
                function callOkGetCity() {
                    if (ts.funSetCode('city', 'ccode')) {
                        ts.funGetAreaList(null, 0);
                    } else {
                        ts.arealist = "";
                    }
                }
            }
        },
        funGetProinceList: function (callOk, triggermode) {
            var ts = this;
            //【获取省数据】
            var params = { "methodname": "GetProvince", "args": [{ "arg": "{\"code\":\"\"}" }] };
            ajaxGetArea(params, callOkGetP);
            function callOkGetP(data) {
                //【获取省后回调】
                ts.provincelist = data;
                (callOk && callOk());
            }
        },
        funGetCityList: function (callOk, triggermode) {
            //callOk:成功后回调
            //triggermode:该方法被触发的方式 手动(参数1) 或自动(参数0)
            var ts = this;
            //【根据省获取市数据】
            ts.funSetCode('province', 'pcode');
            var params = { "methodname": "GetCity", "args": [{ "arg": "{\"code\":" + this.pcode + "}" }] };
            ajaxGetArea(params, callOkGetC);
            function callOkGetC(data) {
                //【获取市后回调】
                ts.citylist = data;
                ts.arealist = "";
                //手动触发时
                if (triggermode) {
                    ts.userinfo.city = "";
                    ts.userinfo.area = "";
                }
                (callOk && callOk());
            }
        },
        funGetAreaList: function (callOk, triggermode) {
            //callOk:成功后回调
            //triggermode:该方法被触发的方式 手动(参数1) 或自动(参数0)
            var ts = this;
            //【根据市获取区数据】
            ts.funSetCode('city', 'ccode');
            var params = { "methodname": "GetArea", "args": [{ "arg": "{\"code\":" + this.ccode + "}" }] };
            ajaxGetArea(params, callOkGetA);
            function callOkGetA(data) {
                //【获取区后回调】
                ts.arealist = data;
                //手动触发时
                if (triggermode) {
                    ts.userinfo.area = "";
                }
                (callOk && callOk());
            }
        },
        funSetCode: function (areatype, codetype) {
            //areatype：区域类型，当前是选择省还是选择市，可选值为province和city
            //codetype: 区域code类型，可选值为 pcode 和 ccode
            var ts = this;
            //【设置省市code】
            var selected = "";
            var list = [];
            if (areatype == "province") {
                selected = ts.userinfo.province;
                list = ts.provincelist;
            } else {
                selected = ts.userinfo.city;
                list = ts.citylist;
            }
            for (var i in list) {
                if (list[i][areatype] == selected) {
                    ts[codetype] = list[i].code;
                    break;
                }
            };
            return codetype;
        },
        funDelImg: function (index) {
             var ts = this;
            //【需要删除的图片先记录，删除只做假的删除，在保存的时候一起将delpicurl提交】
            var fname = getFname(index);
            var delpicurl = [];
            delpicurl.push(fname); //记录需要删除的图片
            ts.delpicurl = delpicurl.join("");
            ts.userinfo.StorePicURL.splice(index, 1);
        },
        // funDelImg: function (index) {
        //     var ts = this;
        //     //【删除照片】
        //     //提示删除中，请等待
        //     // loading("图片删除中，请等待...");
        //     var fname = getFname(index);
        //     ts.userinfo.StorePicURL.splice(index, 1);
        //     // ajaxUploadImg('delete', fname, callOkDeletePic);//获取当前文件名
        //     // function callOkDeletePic() {
        //     //     //【删除图片后回调】
        //     //     ts.userinfo.StorePicURL.splice(index, 1);
        //     //     vm_shopadmin.tipsMsg = "";
        //     // }
        // },
        funTriggerInputFile: function (e) {
            //【模拟点击上传照片按钮】
            var $currentimg = $(e.srcElement);
            $currentimg.siblings(".btn-uploadfile").click();
        },
        funSaveEdit: function () {
            var ts = this;
            //【保存编辑结果】
            ts.userinfo.cid = ts.cid;
            for (var x in ts.picFileName) {
                if (ts.picFileName[x]) {
                    ts.userinfo.StorePicURL[x] = ts.picFileName[x];//把新上传的文件名更新到门店图片列表（这就统一保存到后台的文件格式都未文件名或者带路径的文件名。而非之前的base64）；  
                }
            }
            var jsonData = {
                userinfo: ts.userinfo,
                storecontact: ts.storecontact,
                srjg: ts.srjg,
                delpicurl: ts.delpicurl
            }
            var params = { functype: "savestore", json: jsonData };
            ajaxStoreInfo(params, callOkSaveShopInfo);
            function callOkSaveShopInfo() {
                //【成功保存门店信息后回调】
                ts.isedit = 0;
            }
        }
    },
    computed: {
        setTotalGW: function () {
            // 合计：工位数
            var userinfo = this.userinfo;
            var totalgw = Number(userinfo.JSGWS) + Number(userinfo.BJGWS) + Number(userinfo.YQGWS) + Number(userinfo.MRGWS);
            return totalgw;
        },
        setTotalRy: function () {
            //合计：人员
            var srjg = this.srjg;
            var totalcount = 0;
            for (var x in srjg) {
                totalcount += Number(srjg[x].rs);
            }
            return totalcount;
        },
        setTotalCzTc: function () {
            //合计：产值台次
            var cztclist = this.jyxq.cztc;
            var totalcztc = [];
            var jd = 0, bj = 0, yq = 0, mr = 0;
            for (var x = 1; x < cztclist.length; x++) {
                var r = Number(cztclist[x].jd) + Number(cztclist[x].bj) + Number(cztclist[x].yq) + Number(cztclist[x].mr);
                totalcztc.push(r)
            }
            return totalcztc;
        }
    }
});

function getShopInfo(ts) {
    //【获取店铺基本信息】
    var params = { functype: "getstoreinfo", cid: ts.cid };
    ajaxStoreInfo(params, callOkGetShopInfo);
    function callOkGetShopInfo(r) {
        //【成功获取门店基本信息后回调】
        ts.userinfo = r.userinfo[0];//门店基本信息
        // if (typeof ts.userinfo.StorePicURL == "string") {
        //     ts.userinfo.StorePicURL = JSON.parse(ts.userinfo.StorePicURL);
        // }
        ts.picBackup = ts.userinfo.storeDelPicUrl;//先备份初始值
        ts.srjg = r.srjg; //收入结构
        ts.storecontact = r.storecontact;//门店联系方式
        ts.zone = r.zone;//所属省公司列表
        ts.user = r.user;//用户名：选择运营经理和总监
        ts.CustomerLevel = r.CustomerLevel;//客户类型
        ts.JoiningType = r.JoiningType;//加盟方式
    }

    //【获取经营诊断数据】
    var params2 = { functype: "jyzd", cid: ts.cid };
    ajaxStoreInfo(params2, callOkGetJyzd);
    function callOkGetJyzd(r) {
        //【获取经营诊断回调】
        ts.jyzd = r; //经营诊断（诊断标准+诊断结果）
    }

    //【获取经营详情数据】
    var params3 = { functype: "jyxq", cid: ts.cid };
    ajaxStoreInfo(params3, callOkGetJyxq);
    function callOkGetJyxq(r) {
        //【获取经营详情回调】
        ts.jyxq = r; //经营详情
    }

    //【获取巡店记录数据】
    var params4 = { functype: "xdjl", cid: ts.cid };
    ajaxStoreInfo(params4, callOkGetXdjl);
    function callOkGetXdjl(r) {
        //【获取巡店记录回调】
        ts.xdjl = r; //巡店记录
    }
}
function ajaxStoreInfo(params, callOk) {
    //【ajax:提交到http://mp.hks360.com:83/data/StoreInfo.aspx接口的post请求】
    axios({
        method: "POST",
        url: '/data/StoreInfo.aspx',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data: params
        // params: params
    })
        .then((response) => {
            (callOk && callOk(response.data));
        })
        .catch(function (err) {
            console.log(err);
        })
}
function setImagePreview(e, type) {
    //【上传图片】
    var index = $(e).attr("index");

    var file = e.files[0];
    //判断是否是图片类型
    if (!/image\/\w+/.test(file.type)) {
        alert("只能选择图片");
        return false;
    }
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        var base64url = this.result;
        loading("图片上传中,请等待...");
        if (type == 'upload') {
            //新增  
            ajaxUploadImg('upload', base64url, callOkUploadPic);

        } else {
            //修改(替换路径（等删除完回调里调上传，上传完后再替换路径）)          
            var fname = getFname(index);
            ajaxUploadImg('delete', fname, callOKDeletePic);//获取当前文件名
            function callOKDeletePic() {
                ajaxUploadImg('upload', base64url, callOkUploadPic);
            };
        };

        function callOkUploadPic(fname) {
            //【上传图片回调】
            if (type == "upload") {
                vm_shopadmin.userinfo.StorePicURL.push(fname.filename);
            } else {
                vm_shopadmin.userinfo.StorePicURL[index] = fname.filename;
                var imgObjPreview = $(e).prev().get(0); //获取img对象
                imgObjPreview.src = fname.filename; //给对应img赋base64的结果
            };
            vm_shopadmin.tipsMsg = "";

            $(e).val("");//上传成功后清空input file
        }
    };

}
function ajaxUploadImg(action, picurl, callOk) {
    //【上传图片：增删改功能】
    var params_delete = { action: "delete", Data: { filename: picurl } };//picurl参数此时传递的是fname
    var params_upload = { action: "upload", Data: { Json: { "cid": vm_shopadmin.cid, "imagedata": picurl } } };//picurl此时传递的是base64串

    //删除
    if (action == "delete") {
        upPicFile(params_delete, callOk);

        //上传（新增）    
    } else if (action == "upload") {
        upPicFile(params_upload, callOk);

        //修改（先删后增）  
    } else {
        upPicFile(params_delete, callOk);//先删除
        upPicFile(params_upload, callOk);//后上传
    }

    function upPicFile(params, callOk) {
        axios({
            method: "POST",
            url: 'StorePicture.aspx',
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            data: params
        })
            .then((response) => {
                console.log(response.data);
                (callOk && callOk(response.data));
            })
            .catch(function (err) {
                console.log(err);
                vm_shopadmin.tipsMsg = "";
            })
    }
}
function getFname(index) {
    //【根据索引index获取当前需要删除的图片文件（带路径）】
    var fname = vm_shopadmin.userinfo.StorePicURL[index];
    return fname;
}
function ajaxGetArea(params, callOk) {
    //【获取省市区】
    axios.get('WebFunctionEpcService.aspx?data=' + JSON.stringify(params))
        .then((response) => {
            (callOk && callOk(response.data));
        })
}
function getUrl() {
    //【解析url,返回json格式的url参数】
    // var url = "www.baidu.com?cid=6001";
    var url = location.href;
    var param = {};
    url.replace(/([^?&]+)=([^?&]+)/g, function (s, v, k) {
        param[v] = decodeURIComponent(k);
        return k + '=' + v;
    });
    console.log(JSON.stringify(param));
    return param;
}
function delBase64Prev(string) {
    var sidx = string.indexOf("base64,");
    string = string.substring(sidx + 5);
    console.log(string)
    return string;
}
function loading(msg) {
    //提示
    vm_shopadmin.tipsMsg = msg
}









