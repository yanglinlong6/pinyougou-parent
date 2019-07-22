var app = new Vue({
    el: "#app",
    data: {
        totalMoney: 0,
        totalPayMoney: 0,
        out_trade_no: ''
    },
    methods: {
        //页面加载的时候发送请求 获取code_url 生成二维码 展示  展示金额 和 支付订单号
        createNative: function () {
            axios.get('/pay/createNative.shtml').then(
                function (response) {//response.data=map

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

                }
            )
        },
        //根据支付的订单号发送请求获取支付的状态 Result
        queryPayStatus: function (out_trade_no) {
            axios.get('/pay/queryStatus.shtml?out_trade_no=' + out_trade_no).then(
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
        }
    },
    created: function () {
        if (window.location.href.indexOf("pay.html") != -1) {
            this.createNative();
        } else {
            let urlParamObject = this.getUrlParam();
            if (urlParamObject.money)
                this.totalPayMoney = urlParamObject.money;
        }
    }
});