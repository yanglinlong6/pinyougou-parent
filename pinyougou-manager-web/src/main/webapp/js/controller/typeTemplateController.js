﻿var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: { customAttributeItems: [] },
        brandOptions: [],//显示品牌的列表
        specOptions: [],//显示规格的列表
        ids: [],
        searchEntity: {},
        status: ['未审核', '已审核', '审核未通过', '已关闭'], //定义商品的状态数组
    },
    methods: {
        //新增规格审核的需求：
        updateStatus: function (status) {
            //注意 没有使用restful风格
            axios.post('/typeTemplate/updateStatus.shtml?status=' + status, this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        jsonToString: function (list, key) {
            //用于循环遍历  获取对象中的属性的值 拼接字符串,返回
            var listJson = JSON.parse(list);//[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]

            var str = "";
            for (var i = 0; i < listJson.length; i++) {
                var obj = listJson[i];//{"id":27,"text":"网络"}
                str += obj[key] + ",";
            }
            if (str.length > 0) {
                str = str.substring(0, str.length - 1);
            }
            // var ojb = {id:1}
            //ojb.id   =1  ojb['id']=1

            return str;
        },
        removeTableRow: function (index) {
            this.entity.customAttributeItems.splice(index, 1);
        },
        addTableRow: function () {
            this.entity.customAttributeItems.push({});
        },
        findSpecList: function () {
            axios.get('/specification/findAll.shtml').then(function (response) {
                let specList = response.data;//[{id,name}]
                for (var i = 0; i < specList.length; i++) {
                    app.specOptions.push({ id: specList[i].id, text: specList[i].specName });
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
            //specOptions
        },
        findBrandIds: function () {
            axios.get('/brand/findAll.shtml').then(function (response) {
                let brandList = response.data;//[{id,name}]
                for (var i = 0; i < brandList.length; i++) {
                    app.brandOptions.push({ id: brandList[i].id, text: brandList[i].name });
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        searchList: function (curPage) {
            axios.post('/typeTemplate/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;
                console.log(app.list)
                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/typeTemplate/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml', {
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
            axios.post('/typeTemplate/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/typeTemplate/update.shtml', this.entity).then(function (response) {
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
            axios.get('/typeTemplate/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
                app.entity.brandIds = JSON.parse(app.entity.brandIds);
                app.entity.customAttributeItems = JSON.parse(app.entity.customAttributeItems);
                app.entity.specIds = JSON.parse(app.entity.specIds);
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/typeTemplate/delete.shtml', this.ids).then(function (response) {
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
                url: '/typeTemplate/importData.shtml',
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

        this.searchList(1);
        this.findBrandIds();
        this.findSpecList();
    }

})
