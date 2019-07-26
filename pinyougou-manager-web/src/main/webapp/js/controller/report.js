var app = new Vue({
    el: "#app",
    data: {
        sales: [], //用于显示饼状图中的数据
        names: [],  //用于将分类名称放置于图的左上角
    },
    methods: {
        //点击查询执行的方法
        getData: function () {
            axios.get("/sales/findSalesReports.shtml").then(function (response) {
                //调用生成图标的方法
                app.generateChart(response.data);
            })
        },
        //生成图标的方法
        generateChart: function (data) {
            for (var i = 0; i < data.length; i++) {
                app.sales.push({value: data[i].count, name: data[i].categoryName}),
                    app.names.push(data[i].categoryName)
            }
            //基于准备好的DOM，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));
            myChart.clear();
            var option = {
                title: {
                    text: '商品销售量',
                    x: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: app.names,
                },

                //color: ['#CD5C5C', '#00CED1', '#9ACD32', '#FFC0CB'],
                stillShowZeroSum: false,
                toolbox:{
                    show:true,
                    feature:{
                        mark:{show:true},
                        dataView:{show:true,readOnly:false},
                        magicType:{
                            show:true,
                            type:['pie','funnel'],
                            option:{
                                funnel:{
                                    x:'25%',
                                    width:'50%',
                                    funnelAlign:'left',
                                    max:1548,
                                },
                            },
                        },
                    },
                },
                calculable:true,
                series: [
                    {
                        name: '商品销售量',
                        type: 'pie',
                        radius: '80%',
                        center: ['60%', '60%'],
                        data: app.sales,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(128, 128, 128, 0.5)'
                            }
                        }
                    }
                ]
            };
            //使用刚指定的配置项和数据显示图表
            myChart.setOption(option);
        },
    },
    //钩子函数 初始化了事件
    created: function () {
        this.getData();
    }

})
