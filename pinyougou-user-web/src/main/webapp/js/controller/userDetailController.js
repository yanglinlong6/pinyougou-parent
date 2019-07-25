var app = new Vue({
    el:"#app",
    data:{
        address:{notes:""},
        userDetail:{headPic:""},
        year:"",
        month:"",
        day:""
    },
    methods:{
        updateInfo:function () {
            var date = new Date(this.year+"/"+this.month+"/"+this.day);
            this.userDetail.birthday = date;
            axios.post("/user/updateDetail.shtml",this.userDetail).then(function (response) {
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
            })
        }
    },
    created:function () {
        this.getOriginInfo();
    }
});