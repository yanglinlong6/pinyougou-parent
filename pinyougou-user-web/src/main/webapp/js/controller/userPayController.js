var app = new Vue({
    el:"#app",
    data:{
        payOrderList:[],
        totalMoney: 0,
        totalPayMoney: 0,
        out_trade_no: ''
    },
    methods:{
        getPayOrder:function () {
            axios.get("/order/getOrderByStatus.shtml?status=1").then(function (resposnse) {
                //alert(JSON.stringify(resposnse.data))
                app.payOrderList = resposnse.data;
                var i =  resposnse.data
                for (var j = 0;j < i.length;j++){
                    console.log(i[j]);
                }
            })
        },
        payNow:function (orderId) {
            window.location.href = "paynow.html?orderId="+orderId
        },
        createNative:function () {
            let urlParamObject = this.getUrlParam();
            var orderId = urlParamObject.orderId;
            axios.get("/order/payNow.shtml?orderId="+orderId).then(function (response) {
                if (response.data) {
                    //有数据
                    console.log(response.data)
                    app.totalMoney = response.data.total_fee / 100;
                    app.out_trade_no = response.data.out_trade_no;

                    var qrious = new QRious({
                        element: document.getElementById("qrious"),
                        level: "H",
                        size: 250,
                        value: response.data.code_url
                    });

                    //发送请求
                    app.queryPayStatus(response.data.out_trade_no);
                } else {
                    //没有数据
                }
            })
        },
        //根据支付的订单号发送请求获取支付的状态 Result
        queryPayStatus: function (out_trade_no) {
            axios.get('/order/queryStatus.shtml?out_trade_no=' + out_trade_no).then(
                function (response) {//response.data =Result
                    if (response.data.success) {
                        //支付成功
                        window.location.href = "paysuccess.html?money=" + app.totalMoney;
                    } else {
                        //支付失败:
                        //1.支付失败
                        //2.支付超时
                        if (response.data.message == '支付超时') {
                            alert("123")
                            //重新生成二维码
                            window.location.href = "paysuccess.html?money=" + app.totalMoney;

                            // app.createNative();
                        } else {
                            window.location.href = "payfail.html";
                        }

                    }
                }
            )
        },
        getMoney:function () {
            let urlParamObject = this.getUrlParam();
            var money = urlParamObject.money;
            this.totalPayMoney = money;

        }
    },
    created:function () {
        if (window.location.href.indexOf("paynow.html") == -1) {
            this.getPayOrder();
        }
        if (window.location.href.indexOf("paynow.html") != -1) {
            this.createNative();
        }
        if (window.location.href.indexOf("paysuccess.html") != -1) {
            this.getMoney();
        }
    }
});