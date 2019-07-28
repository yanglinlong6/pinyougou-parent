var app = new Vue({
    el: "#app",

    methods :{
        unfreeze:function () {
            alert("触发")
            axios.post('/unfreeze/unfree.shtml').then(function (response) {
                if(response.data.success) {
                    //解冻成功
                    alert("解冻成功");
                    window.location.href = "/home-index.html"
                }else {
                    alert("服务器繁忙")
                }
            })
        }
    }
})