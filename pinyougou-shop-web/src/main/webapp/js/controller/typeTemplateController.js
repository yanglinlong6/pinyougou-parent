var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        status:['未申请','已审核','申请中','已驳回'],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/typeTemplate/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/typeTemplate/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml',{params:{
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
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/typeTemplate/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/typeTemplate/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.entity.status=0;
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/typeTemplate/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/typeTemplate/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        updateStatus:function () {
            axios.post('/typeTemplate/updateStatus.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        jsonToString:function (list,key) {
            //1.将字符串转成JSON对象
            var jsonObj = JSON.parse(list); //[{}]
            //2.循环遍历[{}]
            var str="";
            for(var i=0;i<jsonObj.length;i++){
                str+=jsonObj[i][key]+",";//{id,text}
            }
            if(str.length>0){
                str=str.substring(0,str.length-1)
            }
            //3.获取里面的text文本的值 通过逗号分隔 返回
            return str;
        }


    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
