var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        payType: ['', '在线支付', '货到付款'],
        status: ['', '未付款', '已付款', '未发货', '已发货', '交易成功', '交易关闭', '待评价'],
        searchEntity: {}
    },
    methods: {
        deliverGoods: function () {
            axios.post('/order/deliverGoods.shtml', this.ids).then(resp => {
                if (resp.data.success) {
                    app.searchList(1);
                    // alert("已经发货成功")
                }
            }).catch(error => {
                console.log("1231312131321");
            })
        },
        searchList: function (curPage) {
            axios.post('/order/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;
                for (var i = 0; i < app.list.length; i++) {
                    if (app.list[i].consignTime !== undefined) {
                        app.list[i].consignTime = app.getTime(app.list[i].consignTime)
                    }
                    // app.list[i].consignTime = app.getTime(app.list[i].consignTime)
                }
                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/order/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/order/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add: function () {
            axios.post('/order/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/order/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/order/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/order/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        getTime: function (t) {
            var _time = new Date(t);
            var year = _time.getFullYear();
            var month = _time.getMonth() + 1;
            var date = _time.getDate();
            var hour = _time.getHours();
            var minute = _time.getMinutes();
            var second = _time.getSeconds();
            return year + "-" + month + "-" + date + "    " + hour + ":" + minute + ":" + second;
        }

    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

    }

})
