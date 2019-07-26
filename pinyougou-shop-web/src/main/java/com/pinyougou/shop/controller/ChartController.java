package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.SalesService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @program: com.pinyougou.shop.controller
 * @author: Sun jinwei
 * @create: 2019-07-22 21:16
 * @description:
 **/
@RestController
@RequestMapping("/sales")
public class ChartController {
    @Reference
    private SalesService salesService;

    /**
     *
     * 横坐标--时间集合
     * 纵坐标--销售额集合--这里要求和对应的时间保持一致 所以需要进行排序
     * 后端给前台返回Map<String, List>数据结构
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping("/getSalesReport")
    public Map<String, List> getSalesReport(String beginTime, String endTime) {
        //1获取当前商家
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //2获取service层传递过来的map 但是此时key为没有顺序的
        Map<String, Double> salesReport = salesService.getSalesReport(sellerId, beginTime, endTime);
        //3所以需要将key进行排序--定义方法
        salesReport=sortMapByKey(salesReport);
        List<String> keyList=new ArrayList<>(); //定义存储的时间集合-横坐标
        List<Double> moneyList=new ArrayList<>(); //定义纵坐标集合 --销售额
        Map<String,List> map=new HashMap<>();
        for (Map.Entry<String, Double> stringDoubleEntry : salesReport.entrySet()) {
            keyList.add(stringDoubleEntry.getKey());
            moneyList.add(stringDoubleEntry.getValue());
        }
        map.put("xAxisList",keyList);
        map.put("seriesSaleList",moneyList);
        return map;
    }

    /**
     * 使用TreeMap进行排序
     * @param salesReport
     * @return
     */
    private Map<String, Double> sortMapByKey(Map<String, Double> salesReport) {

        if (salesReport==null||salesReport.isEmpty()){
            return null;
        }
        /**
         * 使用TreeMap 因为TreeMap实现sortMap接口，比较器进行排序 升序排列o1.compareTo(o2)
         * 降序排列 o2.compareTo(o1)
         */
        Map<String, Double> map=new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2); //升序排列就是o1-o2 降序排列就是o2-o1
            }
        });
        map.putAll(salesReport);
        return map;
    }

}