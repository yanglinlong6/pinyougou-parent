var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        status: ['未申请', '已审核', '申请中', '已驳回'],
        searchEntity: {},
        category1: {},
        category2: [],
        typeList: []
        //category3:[]


        /*addSubItemCats:function () {
            this.entity.subItemCats.push({tbItemCat:{},subItemCats:[]})
        },*/
    },
    methods: {
        addTwoItemCats: function () {
            this.category2.push({name: '', category3: []})
        },
        removeTableRow: function (index) {
            this.category2.splice(index, 1)
        },
        addThreeItemCats: function (index) {
            this.category2[index].category3.push({})
        },
        removeThreeRow: function (index, index3) {
            this.category2[index].category3.splice(index3, 1)
        },


        searchList: function (curPage) {
            axios.post('/itemCat/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml', {
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
            axios.post('/itemCat/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/itemCat/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            this.entity = {category1: this.category1, category2: this.category2};
            axios.post('/itemCat/save.shtml', this.entity).then(function (response) {
                if (response.data.success) {
                    alert("添加成功");
                    app.searchList(1);
                }
            }).catch(function (error) {
                alert("添加失败")
            });


        },
        findOne: function (id) {
            axios.get('/itemCat/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/itemCat/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        updateStatus: function () {
            axios.post('/itemCat/updateStatus.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                    window.location.reload();
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        getTypeList:function () {
            axios.get('/itemCat/getTypeList.shtml',).then(function (response) {
                app.typeList = response.data
            }).catch(function (error) {

            });
        }


    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

    }

})
