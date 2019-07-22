var app = new Vue({
    el: "#app",
    data: {
        totalMoney: 0,
        totalPayMoney: 0,
        out_trade_no: ''//封装支付的金额 二维码连接 交易订单号

    },
    methods: {
        createNative: function () {
            axios.get('/pay/createNative.shtml').then(
                function (response) {//response.data=map

                    if (response.data) {
                        //有数据
                        app.totalMoney = response.data.total_fee / 100;
                        app.out_trade_no = response.data.out_trade_no;

                        var qrious = new QRious({
                            element: document.getElementById("qrious"),
                            level: "H",
                            size: 250,
                            value: response.data.code_url
                        });

                        //发送请求
                        if (qrious)
                            app.queryPayStatus(response.data.out_trade_no);
                    } else {
                        //没有数据
                    }

                }
            )
        },
        //根据支付的订单号发送请求获取支付的状态 Result
        queryPayStatus: function (out_trade_no) {
            axios.get('/pay/queryStatus.shtml', {
                params: {
                    out_trade_no: out_trade_no
                }
            }).then(
                function (response) {//response.data =Result
                    if (response.data.success) {
                        //支付成功
                        window.location.href = "paysuccess.html?money=" + app.totalMoney;
                    } else {
                        //支付失败:
                        //1.支付失败
                        //2.支付超时
                        if (response.data.message == "支付超时") {

                            //发送请求 执行取消订单的业务  axios.get('/')
                            // window.location.href = "paysuccess.html?money=" + app.totalMoney;
                            window.location.href = "payfail.html";
                        } else {
                            window.location.href = "payfail.html";
                        }

                    }
                }
            )
        }
    },
    //钩子函数
    created: function () {
        //页面一加载就应当调用
        if (window.location.href.indexOf("pay.html") != -1) {
            this.createNative();
        } else {
            let urlParamObject = this.getUrlParam();
            if (urlParamObject.money)
                this.totalPayMoney = urlParamObject.money;
        }
    }

})