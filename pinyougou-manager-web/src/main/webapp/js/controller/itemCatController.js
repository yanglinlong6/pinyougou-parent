﻿var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: { parentId: 0 },
        ids: [],
        entity_1: {},//变量1
        entity_2: {},//变量2
        searchEntity: {},
        grade: 1,//面包屑级别的设置
        status: ['未审核', '已审核', '审核未通过', '已关闭'], //定义商品的状态数组
    },
    methods: {
        //新增规格审核的需求：
        updateStatus: function (status) {
            //注意 没有使用restful风格
            axios.post('/itemCat/updateStatus.shtml?status=' + status, this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    // app.searchList(1);
                    app.selectList({ id: 0 });
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
                    // app.searchList(1);
                    app.selectList({ id: 0 });
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/itemCat/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    // app.searchList(1);
                    app.selectList({ id: 0 });
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
            axios.get('/itemCat/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findByParentId: function (parentId) {
            axios.get('/itemCat/findByParentId/' + parentId + '.shtml').then(function (response) {
                app.list = response.data;
                //记录下来
                app.entity.parentId = parentId;
            }).catch(function (error) {
                console.log("1231312131321");
            })
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
        upload: function () {
            var formData = new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('file', file.files[0]);
            axios({
                url: '/itemCat/importData.shtml',
                data: formData,
                method: 'post',
                headers: {
                    'Content-Type': 'multipart/form-data'
                }

            }).then(function (response) {
                if (response.data.success) {
                    alert("上传成功");
                    window.location.reload();
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        }


    },
    //钩子函数 初始化了事件和
    created: function () {

        // this.searchList(1);
        // this.findByParentId(0);
        this.selectList({ id: 0 });
    }

})
