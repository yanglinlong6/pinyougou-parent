﻿var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        loginName: '',
        ids: [],
        smsCode: '',
        repwd: '',
        searchEntity: {},
        userOrderList:[],
        statusList:["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"],
    },
    methods: {
        //获取登录名
        getName: function () {
            axios.get('/login/name.shtml').then(function (response) {
                app.loginName = response.data;
            }).catch(function (error) {
                console.log("该用户被冻结");
                if(error.toString().indexOf("900") != -1) {
                    alert("抱歉，你的账号冻结了")
                    window.location.href = "/unfreeze.html";
                }
            });
        },
        formSubmit: function () {
            var that = this;
            this.$validator.validate().then(function (result) {
                if (result) {
                    console.log(that);
                    axios.post('/user/add/' + that.smsCode + '.shtml', that.entity).then(function (response) {
                        if (response.data.success) {
                            window.location.href = "home-index.html";
                        } else {
                            that.$validator.errors.add(response.data.errorsList);
                        }

                    }).catch(function (error) {
                        console.log("123123321123")
                    })
                }
            })
        },
        searchList: function (curPage) {
            axios.post('/user/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/user/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/user/findPage.shtml', {
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
            axios.post('/user/add/' + this.smsCode + '.shtml', this.entity).then(function (response) {
                console.log(response);
                console.log(response.data.success);
                if (response.data.success) {
                    // app.searchList(1);
                    window.location.href = "home-index.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/user/update.shtml', this.entity).then(function (response) {
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
            axios.get('/user/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/user/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        createSmsCode: function () {
            axios.get('/user/sendCode.shtml?phone=' + this.entity.phone).then(function (response) {
                if (response.data.success) {
                    alert(response.data.message);//显示数据
                } else {
                    //发送失败
                    alert(response.data.message);//
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findAllOrder:function () {
            axios.get("/order/getOrderByStatus.shtml").then(function (response) {
                app.userOrderList = response.data;
            })
        },
        findFootMark: function () {
            axios.get('/user/findFootMark.shtml').then(resp => {
                app.footMarkList = app.data.footMarkList;
            })
        },
        payNow:function (orderId) {
            axios.get("/")
        },


    },
    //钩子函数 初始化了事件和
    created: function () {

        this.findAllOrder();
        // this.searchList(1);
        this.getName();
    }

})
