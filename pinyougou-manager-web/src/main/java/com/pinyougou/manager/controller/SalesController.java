package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.SalesService;
import entity.SaleCount;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
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
     * 1累计销售数量图
     * key：一级分类商品的名称
     * value：一级分类对应下面所有商品卖出的总个数
     *
     * @return
     */
    @RequestMapping("/findSalesReports")
    public List<SaleCount> findSalesReports01() {
        //调用业务层将分类名称和对应的销售量百分比字符串
        List<SaleCount> saleCountList = salesService.findSalesReports01();
        //创建两个集合 用来存储分类名称 和 销售量百分比字符串
        return saleCountList;
    }
}