package com.pinyougou.sellergoods.service;

import java.util.Map;

/**
 * @program: com.pinyougou.sellergoods.service
 * @author: Sun jinwei
 * @create: 2019-07-24 21:44
 * @description:
 **/
public interface SalesService {

    /**
     * 折线图数据
     * @param sellerId
     * @param beginTime
     * @param endTime
     * @return
     */
    Map<String, Double> getSalesReport(String sellerId, String beginTime, String endTime);

    /**
     * 饼状图数据
     * @return
     */
    Map<String, Double> findSalesReports();
}
