var app = new Vue({
    el: "#app",
    data: {
        beginTime: '2019-07-23 09:00',
        endTime: '2019-07-23 23:00',

    },
    methods: {
        //点击查询执行的方法
        getData: function () {
            axios.get("/sales/getSalesReport.shtml?beginTime="+this.beginTime+"&endTime="+this.endTime).then(function (response) {
                //调用生成图标的方法
                app.generateChart(response.data);

            })
        },
        //生成图标的方法
        generateChart: function (data) {
            //基于准备好的DOM，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));
            myChart.clear();
            var option = {
                title: {
                    text: '销售额曲线图'
                },
                //提示框组件
                tooltip: {
                    //坐标轴触发，主要用于柱状图，折线图等
                    trigger: 'axis'
                },
                //数据全部显示
                axisLabel: {
                    interval: 0
                },
                //图例
                legend: {
                    data: ['销售额']
                },
                //横轴
                xAxis: {
                    data: data.xAxisList
                },
                //纵轴
                yAxis: {},
                //系列列表。每个系列通过type决定自己的图表类型
                series: [
                    {
                        name: '销售额',
                        //折线图
                        type: 'line',
                        data: app.dataToFixed(data)//处理小数点数据
                    }
                ]
            };
            //使用刚指定的配置项和数据显示图表
            myChart.setOption(option);
        },
        //将集合中的数据保留两位小数
        dataToFixed: function (data) {
            var seriesData = [];
            for (var i = 0; i < data.seriesSaleList.length; i++) {
                //将销量保留两位小数
                var temp = data.seriesSaleList[i].toFixed(2);
                seriesData.push(temp);
            }
            return seriesData;
        }

    },
    //钩子函数 初始化了事件和
    created: function () {

        this.getData();

    }

})
