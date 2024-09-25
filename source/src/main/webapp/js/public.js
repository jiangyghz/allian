        var baseUrl = "/manage/";
        // var baseUrl="http://changan.91jch.com/manage/"
        if(location.pathname.indexOf("store")>-1){
            baseUrl=location.pathname; //  /store/manage/userlist
            baseUrl=baseUrl.substring(0,baseUrl.lastIndexOf('/')+1); // /store/manage/
        }
        //设置分页按钮
        function setPaginnaiton(pageno) {
            htmlList = [];
            var baseNum = Math.floor(pageno / 10);
            if (maxPage == 0) {
                $('#AspNetPager2').html('');
                return false;
            }
            if (pageno == 1) {
                htmlList.push('<a disabled="disabled" style="margin-right:5px;">首页</a>')
            } else {
                htmlList.push('<a href="javascript:getData(1)" style="margin-right:5px;">首页</a>')
            }
            if (baseNum == 0) {
                htmlList.push('<a disabled="disabled" style="margin-right:5px;">前页</a>')
            } else {
                htmlList.push('<a href="javascript:getData(' + Math.floor(pageno / 10) * 10 +
                    ')" style="margin-right:5px;">前页</a>')
            }
            for (var i = 1; i <= 10; i++) {
                if (baseNum * 10 + i > maxPage) {
                    break;
                }
                if (baseNum * 10 + i == pageno) {
                    htmlList.push('<span disabled="disabled" style="margin-right:5px;">' + (baseNum * 10 + i) +
                        '</span>')
                    continue;
                }
                htmlList.push('<a href="javascript:getData(' + (baseNum * 10 + i) + ')" style="margin-right:5px;">' + (
                    baseNum * 10 + i) + '</a>')
            }
            if (baseNum == Math.floor(maxPage / 10)) {
                htmlList.push('<a disabled="disabled" style="margin-right:5px;">后页</a>')
            } else {
                htmlList.push('<a href="javascript:getData(' + (Math.floor(pageno / 10) * 10 + 11) +
                    ')" style="margin-right:5px;">后页</a>')
            }
            htmlList.push('<a href="javascript:getData(' + maxPage + ')" style="margin-right:5px;">尾页</a>')
            $('#AspNetPager2').html(htmlList);
        }

        Date.prototype.Format = function (formatStr) {
            var str = formatStr
            var Week = ['日', '一', '二', '三', '四', '五', '六']
          
            str = str.replace(/yyyy|YYYY/, this.getFullYear())
            str = str.replace(/yy|YY/, (this.getYear() % 100) > 9 ? (this.getYear() % 100).toString() : '0' + (this.getYear() % 100))
          
            str = str.replace(/MM/, (this.getMonth() + 1) > 9 ? (this.getMonth() + 1).toString() : '0' + (this.getMonth() + 1))
            str = str.replace(/M/g, (this.getMonth() + 1))
          
            str = str.replace(/w|W/g, Week[this.getDay()])
          
            str = str.replace(/dd|DD/, this.getDate() > 9 ? this.getDate().toString() : '0' + this.getDate())
            str = str.replace(/d|D/g, this.getDate())
          
            str = str.replace(/hh|HH/, this.getHours() > 9 ? this.getHours().toString() : '0' + this.getHours())
            str = str.replace(/h|H/g, this.getHours())
            str = str.replace(/mm/, this.getMinutes() > 9 ? this.getMinutes().toString() : '0' + this.getMinutes())
            str = str.replace(/m/g, this.getMinutes())
          
            str = str.replace(/ss|SS/, this.getSeconds() > 9 ? this.getSeconds().toString() : '0' + this.getSeconds())
            str = str.replace(/s|S/g, this.getSeconds())
          
            return str
          }

        //加载中弹窗
        function loading(txt) {
            $('body').loading({
                loadingWidth: 120,
                title: '',
                name: 'test',
                discription: txt ? txt : '',
                direction: 'column',
                type: 'origin',
                // originBg:'#71EA71',
                originDivWidth: 40,
                originDivHeight: 40,
                originWidth: 6,
                originHeight: 6,
                smallLoading: false,
                loadingMaskBg: 'rgba(0,0,0,0.2)'
            });
        }
        //
        function getLoginuse(success){
            loading();
            $.ajax({
                type: 'GET',
                url: baseUrl + 'GetLoginUse',
                data: "",
                dataType: 'JSON',
                success: function (res) {
                    // console.log(res);
                    if(res.code!="0"){
                        alert(res.msg)
                    }else{
                        if(res.toid=='zb'){
                            res.toid="";
                        }
                        success(res);      
                    }

                },
                error: function (error) {
                    // console.log(error)
                    alert("发生错误,请稍后重试")
                }
            })
        }
        
        function getUrlkey(name){
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null
        }