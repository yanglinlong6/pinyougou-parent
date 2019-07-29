var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        list0:[],
        itemCatList:[],//格式 是  [null,null,n......,"手机"]
        entity:{},
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/orderStatistic/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                alert(response.data.list)
                //获取数据
                app.list0=response.data.list;
                console.log(app.list0)
                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有商品
        findAll:function () {
            console.log(app);
            axios.get('/order/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
        findPage:function () {
            var that = this;
            axios.get('/order/findPage.shtml',{params:{
                    pageNo:this.pageNo
                }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },



    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

    }

})
