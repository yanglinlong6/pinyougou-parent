var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list0: [],
        list1: [],
        list2: [],
        entity: {},
        contentList: [],
        grade: 1,// 分类级别
        ids: [],
        keywords: '',
        searchEntity: {},

        //list2:[ { "id": 1, "name": "图书、音像、电子书刊", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 74, "name": "家用电器", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 161, "name": "电脑、办公", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 249, "name": "个护化妆", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 290, "name": "钟表", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 296, "name": "母婴", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 378, "name": "食品饮料、保健食品", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 438, "name": "汽车用品", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 495, "name": "玩具乐器", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 558, "name": "手机", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 580, "name": "数码", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 633, "name": "家居家装", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 699, "name": "厨具", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 749, "name": "服饰内衣", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 865, "name": "鞋靴", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 903, "name": "礼品箱包", "parentId": 0, "status": "0", "typeId": 35 }, { "id": 963, "name": "珠宝", "parentId": 0, "status": "1", "typeId": 35 }, { "id": 1031, "name": "运动健康", "parentId": 0, "status": null, "typeId": 35 }, { "id": 1147, "name": "彩票、旅行、充值、票务", "parentId": 0, "status": null, "typeId": 35 }, { "id": 1186, "name": "小白大数据", "parentId": 0, "status": null, "typeId": 35 }],
    },
    methods: {
        doSearch: function () {
            window.location.href = "http://localhost:9104/search.html?keywords=" + encodeURIComponent(this.keywords);
        },
        findByCategoryId: function (categoryId) {
            axios.get('/content/findByCategoryId/' + categoryId + '.shtml').then(function (response) {
                //alert(response.data);
                app.contentList = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        searchList: function (curPage) {
            axios.post('/content/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/content/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/content/findPage.shtml', {
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
            axios.post('/content/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/content/update.shtml', this.entity).then(function (response) {
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
            axios.get('/content/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/content/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        selectList: function (p_entity) {
            //如果当前的等级是1
            if (this.grade == 1) {
                this.entity_1 = {};
                this.entity_2 = {};
            }
            if (this.grade == 2) {
                this.entity_1 = p_entity;
                this.entity_2 = {};
            }

            if (this.grade == 3) {
                this.entity_2 = p_entity;
            }
            this.findByParentId(p_entity.id);
            this.findByParentId(p_entity.id + 1);
        },
        findByParentId: function (parentId) {
            axios.get('/itemCat/findByParentId/' + parentId + '.shtml').then(function (response) {

                if (parentId == 0) {
                    app.list0 = response.data;
                } else if (parentId == 1) {
                    app.list1 = response.data;
                } else {
                    app.list2 = response.data;
                }

                // app.list[parentId]=JSON.stringify(response.data);

                //alert(app.list[0])
                //记录下来
                app.entity.parentId = parentId;

            }).catch(function (error) {
                console.log("1231312131321");
            })
        },


    },
    //钩子函数 初始化了事件和
    created: function () {

        // this.searchList(1);
        this.findByCategoryId(1);
        this.findByParentId(0);
        //this.selectList({ id: 0 });
    }

})
