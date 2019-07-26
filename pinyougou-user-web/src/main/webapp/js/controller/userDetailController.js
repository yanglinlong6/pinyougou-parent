var app = new Vue({
    el:"#app",
    data:{
        userDetail:{headPic:""},
        year:"",
        month:"",
        day:""
    },
    methods:{
        updateInfo:function () {
            var date = this.year+"-"+this.month+"-"+this.day;
            alert(date)
            axios.post("/user/updateDetail.shtml?date="+date,this.userDetail).then(function (response) {
                alert(response.data.message)
            })
        },
        //文件上传
        upload:function () {
            var formData=new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('file', file.files[0]);
            axios({
                url:"http://localhost:9110/upload/uploadFile.shtml",
                data:formData,
                method:"POST",
                headers:{'Content-Type': 'multipart/form-data'},//上传文件需要将请求头改为 'multipart/form-data'
                //开启跨域请求携带相关认证信息
                withCredentials:true
            }).then(function (response) {
                if(response.data.success){
                    //上传成功
                    alert("上传成功")
                    app.userDetail.headPic=response.data.message;
                }else{
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        cleanInput:function () {
            var element = document.getElementById("file");
            element.outerHTML = element.outerHTML;
        },
        getOriginInfo:function () {
            axios.get("/user/findByUserId.shtml").then(function (response) {
                app.userDetail = response.data
                var timeStr = app.userDetail.birthday.toLocaleString();
                var splits = timeStr.split("-");
                splits[2] = splits[2].substring(0,2)
               /* var a = $("#select_year2").html()
                alert( $("#select_year2").html())*/
               /* $("#select_year2 option[value=1993]").attr("selected","selected")
                $("#select_month2 option[value=6]").attr("selected","selected")*/
                /*$("#select_month2").attr("value",11)*/
                app.year = splits[0]
                if (splits[1].startsWith(0)) {
                    splits[1] = splits[1].replace("0","")
                }
                app.month = splits[1]
                var month = splits[1]
                if (splits[2].startsWith(0)) {
                    splits[2] = splits[2].replace("0","")
                }
                app.day = splits[2]

                var dayCount = 0;
                switch (parseInt(app.month)) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        dayCount = 31;
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        dayCount = 30;
                        break;
                    case 2:
                        dayCount = 28;
                        if ((app.year % 4 == 0) && (app.year % 100 != 0) || (app.year % 400 == 0)) {
                            dayCount = 29;
                        }
                        break;
                    default:
                        break;
                }
                var dayStr=""
                for (var i = 1; i <= dayCount; i++) {
                    var sed = app.day==i?"selected":"";
                     dayStr =dayStr+ "<option value=\"" + i + "\" "+sed+">" + i + "</option>";


                }
                $("#select_day2").html(dayStr);
            })
        },
        /*buildDay:function () {
            if ( $("#select_year2").val() == 0 || $("#select_month2").val() == 0) {
                // 未选择年份或者月份
                $DaySelector.html(str);
            } else {
                $DaySelector.html(str);
                var year = parseInt($("#select_year2").val());
                var month = parseInt($MonthSelector.val());
                var dayCount = 0;
                switch (month) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        dayCount = 31;
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        dayCount = 30;
                        break;
                    case 2:
                        dayCount = 28;
                        if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                            dayCount = 29;
                        }
                        break;
                    default:
                        break;
                }

                var daySel = $DaySelector.attr("rel");
                for (var i = 1; i <= dayCount; i++) {
                    var sed = daySel==i?"selected":"";
                    var dayStr = "<option value=\"" + i + "\" "+sed+">" + i + "</option>";
                    $DaySelector.append(dayStr);
                }
            }
        }
*/
    },
    created:function () {
        this.getOriginInfo();
    }
});