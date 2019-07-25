var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        preDott: false,
        nextDott: false,
        searchMap: {
            'keywords': '',
            'category': '',
            'brand': '',
            spec: {},
            'price': '',
            'pageNo': 1,
            'pageSize': 40,
            'sortField': '',
            'sortType': ''
        },//用于绑定搜索的条件的参数的对象
        pageLabels: [],
        resultMap: {},//搜索的结果封装对象
        brandList: {},
        specMap: {},
        searchEntity: {}
    },
    methods: {
        isKeywordsIsBrand: function () {
            if (this.resultMap.brandList != null && this.resultMap.brandList.length > 0) {
                for (var i = 0; i < this.resultMap.brandList.length; i++) {
                    if (this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text) != -1) {
                        this.searchMap.brand = this.resultMap.brandList[i].text;
                        return true;
                    }
                }
            }
            return false;
        },
        doSort: function (sortField, sortType) {
            this.searchMap.sortField = sortField;
            this.searchMap.sortType = sortType;
            this.search();
        },
        clear: function () {
            this.searchMap = {
                'keywords': this.searchMap.keywords,
                'category': '',
                'brand': '',
                spec: {},
                'price': '',
                'pageNo': 1,
                'pageSize': 40
            };
        },
        queryByPage: function (pageNo) {
            var number = parseInt(pageNo);
            if (number > this.resultMap.totalPages) {
                number = this.resultMap.totalPages;
            }
            if (number < 1) {
                number = 1;
            }
            this.searchMap.pageNo = number;
            this.search();
        },
        buildPageLabel: function () {
            this.pageLabels = [];
            //显示以当前页为中心的5个页码
            let firstPage = 1;
            this.preDott = false;
            this.nextDott = false;
            let lastPage = this.resultMap.totalPages;//结束页码

            if (this.resultMap.totalPages > 5) {
                //判断 如果当前的页码 小于等于3  pageNo<=3      1 2 3 4 5  显示前5页
                if (this.searchMap.pageNo <= 3) {
                    firstPage = 1;
                    lastPage = 5;
                    this.preDott = false;
                    this.nextDott = true;
                } else if (this.searchMap.pageNo >= this.resultMap.totalPages - 2) {//如果当前的页码大于= 总页数-2    98 99 100
                    firstPage = this.resultMap.totalPages - 4;
                    lastPage = this.resultMap.totalPages;
                    this.preDott = true;
                    this.nextDott = false;
                } else {
                    firstPage = this.searchMap.pageNo - 2;
                    lastPage = this.searchMap.pageNo + 2;
                    this.preDott = true;
                    this.nextDott = true;

                }
            } else {
                //this.preDott=false;
                //this.nextDott=false;
            }
            for (let i = firstPage; i <= lastPage; i++) {
                this.pageLabels.push(i);
            }
        },
        removeSearchItem: function (key) {
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = '';
            } else {
                delete this.searchMap.spec[key];
            }
            this.search();
        },
        addSearchItem: function (key, value) {
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = value;
            } else {
                this.searchMap.spec[key] = value;
            }
            this.search();
        },
        search: function () {
            axios.post('/itemSearch/search.shtml', this.searchMap).then(
                function (response) {//Map
                    app.resultMap = response.data;
                    app.buildPageLabel();
                }
            )
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/item/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/item/findPage.shtml', {
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
            axios.post('/item/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.search(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/item/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.search(1);
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
            axios.get('/item/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/item/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.search(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        // findDetailsPage:function (id) {
        //     axios.get('/item/findDetailsPage/' + id + '.shtml').then(resp=>{
        //         window.location.href = "http://localhost:9107/cart.html";
        //     })
        // },
        // addFootMark: function (id) {
        //     alert("发送请求")
        //     axios.get('/itemSearch/addFootMark/' + id + '.shtml', { withCredentials: true }).then(resp => {
        //         if (resp.data.success) {
        //             console.log("添加足迹成功")
        //         }
        //     }).catch(error => {
        //         console.log("添加足迹失败")
        //     });
        //     app.tiaozhuan();
        // },
        // tiaozhuan: function (id) {
        //     // window.location.href = "http://localhost:9105/149187842867983.html";
        //     axios.get('/item/tiaoZhaun.shtml?id=' + id).then(resp => {
        //         var tbgoods = JSON.stringify(resp.data);
        //         console.log(tbgoods);
        //         window.location.href = "http://localhost:9105/" + tbgoods.id + ".html?id=" + tbgoods.id;
        //     }).catch(error => {
        //         console.log("跳转失败")
        //     })
        //
        // },
        tiaozhuan: function (id) {
            // window.location.href = "http://localhost:9105/149187842867983.html";
            window.location.href = "http://localhost:9105/" + id + ".html?id=" + id;

        }

    },
    //钩子函数 初始化了事件和
    //页面记载
    created: function () {
        //1.获取URL中的参数的值
        var jsonObj = this.getUrlParam();
        //2.赋值给变量searchMap.keywords
        // decodeURIComponent 解码
        // alert(jsonObj);
        this.searchMap.keywords = decodeURIComponent(jsonObj.keywords);
        //3.执行搜索
        this.search();
    }

})
