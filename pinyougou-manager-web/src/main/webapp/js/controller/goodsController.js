var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        image_entity: {url: '', color: ''},
        entity: {
            goods: {},
            goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
            itemList: []
        },
        ids: [],
        itemCat1List: [],
        itemCat2List: [],
        itemCat3List: [],
        specList: [],
        brandTextList: [],
        searchEntity: {}
    },
    methods: {
        //点击复选框的时候 调用生成 sku列表的的变量
        createList: function () {
            //1.定义初始化的值
            this.entity.itemList = [{'spec': {}, 'price': 0, 'num': 0, 'status': '0', 'isDefault': '0'}];

            //2.循环遍历 specificationItems

            var specificationItems = this.entity.goodsDesc.specificationItems;
            for (var i = 0; i < specificationItems.length; i++) {
                //3.获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表
                var obj = specificationItems[i];
                this.entity.itemList = this.addColumn(
                    this.entity.itemList,
                    obj.attributeName,
                    obj.attributeValue);
            }
        },

        /**
         *获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表 方法
         * @param list
         * @param columnName  网络
         * @param columnValue  [移动3G,移动4G]
         */
        addColumn: function (list, columnName, columnValue) {
            var newList = [];

            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];//
                for (var j = 0; j < columnValue.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var value = columnValue[j];//移动3G
                    newRow.spec[columnName] = value;
                    newList.push(newRow);
                }
            }

            return newList;
        },
        //当点击复选框的时候调用 并影响变量：entity.goodsDesc.specficationItems的值
        updateChecked: function ($event, specName, specValue) {
            let searchObject = this.searchObjectByKey(this.entity.goodsDesc.specificationItems, specName, 'attributeName');
            if (searchObject != null) {
                //searchObject====={"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if ($event.target.checked) {
                    searchObject.attributeValue.push(specValue);
                } else {
                    searchObject.attributeValue.splice(searchObject.attributeValue.indexOf(specValue), 1);
                    if (searchObject.attributeValue.length == 0) {
                        this.entity.goodsDesc.specificationItems.splice(this.entity.goodsDesc.specificationItems.indexOf(searchObject), 1)
                    }
                }
            } else {
                //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
                this.entity.goodsDesc.specificationItems.push({
                    "attributeName": specName,
                    "attributeValue": [specValue]
                });
            }
        },
        /**
         *
         * @param list 从该数组中查询[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
         * @param specName  指定查询的属性的具体值 比如 网络
         * @param key  指定从哪一个属性名查找  比如：attributeName
         * @returns {*}
         */
        searchObjectByKey: function (list, specName, key) {
            for (var i = 0; i < list.length; i++) {
                let specificationItem = list[i];//{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if (specificationItem[key] == specName) {
                    return specificationItem;
                }
            }
            return null;
        },
        /**
         *
         * @param list 从该数组中查询[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
         * @param specName  指定查询的属性的具体值 比如 网络
         * @param key  指定从哪一个属性名查找  比如：attributeName
         * @returns {*}
         */
        searchObjectByKey: function (list, specName, key) {
            for (var i = 0; i < list.length; i++) {
                let specificationItem = list[i];//{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
                if (specificationItem[key] == specName) {
                    return specificationItem;
                }
            }
            return null;
        },
        findItemCat1List: function () {
            axios.get('/itemCat/findByParentId/0.shtml').then(function (response) {
                app.itemCat1List = response.data;

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        removeImage: function (index) {
            //移除图片
            this.entity.goodsDesc.itemImages.splice(index, 1);
        },
        addImageEntity: function () {
            //添加图片
            this.entity.goodsDesc.itemImages.push(this.image_entity);
        },
        upload: function () {
            var formData = new FormData();
            //参数formData.append('file' 中的file 为表单的参数名  必须和 后台的file一致
            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            formData.append('file', file.files[0]);
            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                data: formData,
                method: 'post',
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials: true
            }).then(function (response) {
                if (response.data.success) {
                    //上传成功
                    console.log(this);
                    app.image_entity.url = response.data.message;
                    console.log(JSON.stringify(app.image_entity));
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
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
            //获取富文本编辑器中的内容传递给对象
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    // app.searchist(1);
                    app.entity = {goods: {}, goodsDesc: {}, itemList: []}
                    editor.html("");//清空
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.datra.success) {
               window.location.href="goods.html";
                    // app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.goods.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
                //赋值到富文本编辑器
                editor.html(app.entity.goodsDesc.introduction);
                app.entity.goodsDesc.itemImages = JSON.parse(app.entity.goodsDesc.itemImages);
                app.entity.goodsDesc.customAttributeItems = JSON.parse(app.entity.goodsDesc.customAttributeItems);
                app.entity.goodsDesc.specificationItems = JSON.parse(app.entity.goodsDesc.specificationItems);
                for (var i = 0; i < app.entity.itemList.length; i++) {
                    let item = app.entity.itemList[i];
                    item.spec = JSON.parse(item.spec);
                }

            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        isChecked: function (specName, specValue) {
            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems, 'attributeName', specName);
            console.log(obj);
            if (obj != null) {
                if (obj.attributeValue.indexOf(specValue) != -1) {
                    return true;
                }
            }
            return false;
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }

    },
    watch: {
        'entity.goods.category1Id': function (newval, oldvalue) {
            //赋值为空
            this.itemCat3List = [];
            //删除属性回到原始状态
            if (this.entity.goods.id == null) {
                delete this.entity.goods.category2Id;

                delete this.entity.goods.category3Id;

                delete this.entity.goods.typeTemplateId;

            }
            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(function (response) {
                    app.itemCat2List = response.data;

                }).catch(function (error) {
                    console.log("1231312131321");
                });


            }

        },
        'entity.goods.category2Id': function (newval, oldvalue) {
            //删除
            if (this.entity.goods.id == null) {
                delete this.entity.goods.category3Id;
                delete this.entity.goods.typeTemplateId;
            }

            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(function (response) {
                    app.itemCat3List = response.data;

                }).catch(function (error) {
                    console.log("1231312131321");
                });
            }
        },
        'entity.goods.category3Id': function (newval, oldval) {
            if (newval != undefined) {
                axios.get('/itemCat/findOne/' + newval + '.shtml').then(
                    function (response) {
                        //获取列表数据 三级分类的列表
                        // app.entity.goods.typeTemplateId = response.data.typeId;
                        //第一个参数：需要改变的值的对象变量
                        //第二个参数：需要赋值的属性名
                        //第三个参数：要赋予的值
                        app.$set(app.entity.goods, 'typeTemplateId', response.data.typeId);
                        console.log(response.data.typeId);
                        console.log(app.entity.goods.typeTemplateId);

                    }
                )
            }
        },
        'entity.goods.typeTemplateId': function (newval, oldval) {
            if (newval != undefined) {

                axios.get('/typeTemplate/findOne/' + newval + '.shtml').then(
                    function (response) {

                        //获取到的是模板的对象
                        var typeTemplate = response.data;
                        //品牌的列表
                        app.brandTextList = JSON.parse(typeTemplate.brandIds);//[{"id":1,"text":"联想"}]

                        //获取模板的扩展属性的值赋值显示
                        app.entity.goodsDesc.customAttributeItems =
                            JSON.parse(typeTemplate.customAttributeItems);

                    }
                )


                //监听模板的变化 根据模板的ID 获取模板的规格的数据拼接成要的格式
                axios.get('/typeTemplate/findSpecList/' + newval + '.shtml').then(
                    function (response) {
                        app.specList = response.data;
                    }
                )
            }
        },
        //监控变量的变化，如果是已经
        'entity.goods.isEnableSpec': function (newVal, oldVal) {
            //如果是隐藏规格列表 则清除所有数据，展开是再进行选择。
            if (newVal == 0) {
                this.entity.goodsDesc.specificationItems = [];
                this.entity.itemList = [];
            }
        },
        //监控数据变化 ，如果最后还剩下一个就直接删除
        'entity.itemList': function (newval, oldval) {
            //如果是相同的数据那么直接赋值为空即可
            console.log(JSON.stringify([{
                spec: {},
                price: 0,
                num: 0,
                status: '0',
                isDefault: '0'
            }]) == JSON.stringify(newval));
            if (JSON.stringify([{spec: {}, price: 0, num: 0, status: '0', isDefault: '0'}]) == JSON.stringify(newval)) {
                this.entity.itemList = [];
            }
        }
    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);
        this.findItemCat1List();
        // 使用插件中的方法getUrlParam（） 返回是一个JSON对象，例如：{id:149187842867989}
        var request = this.getUrlParam();
        //获取参数的值
        console.log(request);
        //根据ID 获取 商品的信息
        this.findOne(request.id);
    }

})
