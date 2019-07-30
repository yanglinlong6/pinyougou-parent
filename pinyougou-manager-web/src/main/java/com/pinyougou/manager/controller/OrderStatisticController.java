package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.order.service.OrderStatisticService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.OrderStatistic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.xml.transform.Source;
import java.util.List;

@RestController
@RequestMapping("/orderStatistic")
public class OrderStatisticController {

    @Reference
    private OrderStatisticService orderStatisticService;

    @RequestMapping("/search")
    public PageInfo<OrderStatistic> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                             @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                             @RequestBody TbOrder order) {
        System.out.println(order.getCreateTime()+"创建时间");
        System.out.println(order.getUpdateTime()+"更新时间");
        PageInfo<OrderStatistic> page = orderStatisticService.findPage(pageNo, pageSize, order);
        System.out.println(page);
        return page;
    }
}
