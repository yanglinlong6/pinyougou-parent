package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.SalesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: com.pinyougou.manager.controller
 * @author: Sun jinwei
 * @create: 2019-07-24 21:41
 * @description:
 **/
@RestController
@RequestMapping("/sales")
public class SalesController {

    @Reference
    private SalesService salesService;


    /**
     * 销售饼状图的数据  显示一级分类的销售情况
     * 横坐标为一级分类名称
     * 纵坐标对应一级分类名称对应的金额
     * @return
     */
    @RequestMapping("/findSalesReports")
    public Map<String, List<Double>> findSalesReports(){
        Map<String,Double> map=salesService.findSalesReports();
        List<String> keyList=new ArrayList<>();



        return null;

    }

}