var app = new Vue({
    el: "#app",
    data: {

        list:[],
        forDate: '',
        toDate: ''

    },
    methods: {
        selectShopCount:function () {
            axios.get('/seller/findDateMoney.shtml?forDate='+this.forDate+'&toDate='+this.toDate).then(function (response) {
                app.list=response.data;
                if (app.list.length === 0){
                    alert("没有查询结果")
                }
            }).catch(function (error) {
                alert("服务器异常")
            })
        }
    },
    //钩子函数 初始化了事件和
    created: function () {
        this.selectShopCount();
    }

})
