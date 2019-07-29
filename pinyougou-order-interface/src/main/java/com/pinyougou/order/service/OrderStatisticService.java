package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import entity.OrderStatistic;


public interface OrderStatisticService extends CoreService<TbGoods> {

    PageInfo<OrderStatistic> findPage(Integer pageNo, Integer pageSize, TbOrder order);
}
