package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder> implements OrderService {

    private TbOrderMapper tbOrderMapper;

    @Autowired
    public OrderServiceImpl(TbOrderMapper tbOrderMapper) {
        super(tbOrderMapper, TbOrder.class);
        this.tbOrderMapper = tbOrderMapper;
    }

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Override
    /**
    *@Description //用户所有订单，及订单的详情
    *@param  [userId]
    *@return java.util.List<com.pinyougou.pojo.TbOrder>
    *@time 2019-7-24 22:06
    */
    public List<TbOrder> getAllOrder(String userId) {
        TbOrder tbOrder = new TbOrder();
        tbOrder.setUserId(userId);
        //找出所有订单
        List<TbOrder> tbOrderList = tbOrderMapper.select(tbOrder);
        //遍历订单，找出订单所有的商品信息
        for (TbOrder order : tbOrderList) {
            Long orderId = order.getOrderId();
            TbOrderItem tbOrderItem = new TbOrderItem();
            tbOrderItem.setOrderId(orderId);
            List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.select(tbOrderItem);

            order.setOrderItemList(tbOrderItemList);
        }


        return tbOrderList;
    }
}
