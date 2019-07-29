var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        totalMoney: 0,//总金额
        totalNum: 0,//总数量
        cartList: [],
        addressList: [],
        address: {},
        order: { paymentType: '1' },
        entity: {},
        ids: [],
        searchEntity: {},
        flagLogin:false
    },
    methods: {
        //查询所有的购物车的列表数据
        findCartList: function () {
            axios.get('/cart/findCartList.shtml').then(
                function (response) {
                    app.cartList = response.data;//List<Cart>   cart { List<ORDERiMTE> }
                    app.totalMoney = 0;
                    app.totalNum = 0;
                    for (var i = 0; i < response.data.length; i++) {
                        var obj = response.data[i];//Cart
                        for (var n = 0; n < obj.orderItemList.length; n++) {
                            var objx = obj.orderItemList[n];//ORDERiMTE
                            app.totalMoney += objx.totalFee;
                            app.totalNum += objx.num;
                        }
                    }

                }
            )
        },
        //向已有的购物车中添加商品
        addGoodsToCartList: function (itemId, num) {
            axios.get('/cart/addGoodsToCartList.shtml?itemId=' + itemId + '&num=' + num).then(
                function (response) {
                    if (response.data.success) {
                        //
                        app.findCartList();
                    }
                }
            )
        },
        findAddressList: function () {
            axios.get('/address/findAddressListByUserId.shtml').then(function (response) {
                app.addressList = response.data;
                for (var i = 0; i < app.addressList.length; i++) {
                    if (app.addressList[i].isDefault == '1') {
                        app.address = app.addressList[i];
                        break;
                    }
                }
            });
        },
        selectAddress: function (address) {
            this.address = address;
        },
        isSelectedAddress: function (address) {
            if (address == this.address) {
                return true;
            }
            return false;
        },
        selectType: function (type) {
            console.log(type);
            this.$set(this.order, 'paymentType', type);
            //this.order.paymentType=type;
        },
        submitOrder:function () {
            this.$set(this.order,'receiverAreaName',this.address.address);
            this.$set(this.order,'receiverMobile',this.address.mobile);
            this.$set(this.order,'receiver',this.address.contact);
            axios.post('/order/add.shtml',this.order).then(function (response) {
                if (response.data.success){
                    window.location.href = "pay.html";
                } else {
                    alert(response.data.message);
                }
            })
        },
        addGoodsToCollectionList:function (itemId){// 添加收藏
            axios.get('/cart/addGoodsToCollectionList.shtml?itemId=' + itemId).then(
                function (response) {
                    //未登录
                    if (response.data.message=='请登录') {
                        alert("要登录");
                        var url = window.location.href;//获取当前浏览器中的URL的地址
                        window.location.href = "http://localhost:9107/page/login.shtml?url=" + url;
                    }else {
                        if (response.data.success){
                            window.location.href='http://localhost:9106/home-person-collect.html?itemId='+itemId
                        }else {

                        }
                    }


                }
            )
        },
    },
    created: function () {
        this.findCartList();

        if (window.location.href.indexOf("getOrderInfo.html") != -1) {
            this.findAddressList();
        }
    }
});