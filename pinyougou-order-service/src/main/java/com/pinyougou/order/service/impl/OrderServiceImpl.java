package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.*;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.pojo.TbOrder;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder> implements OrderService {


    private TbOrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    public OrderServiceImpl(TbOrderMapper orderMapper) {
        super(orderMapper, TbOrder.class);
        this.orderMapper = orderMapper;
    }


    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (order != null) {
            if (StringUtils.isNotBlank(order.getPaymentType())) {
                criteria.andLike("paymentType", "%" + order.getPaymentType() + "%");
                //criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
            }
            if (StringUtils.isNotBlank(order.getPostFee())) {
                criteria.andLike("postFee", "%" + order.getPostFee() + "%");
                //criteria.andPostFeeLike("%"+order.getPostFee()+"%");
            }
            if (StringUtils.isNotBlank(order.getStatus())) {
                criteria.andLike("status", "%" + order.getStatus() + "%");
                //criteria.andStatusLike("%"+order.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(order.getShippingName())) {
                criteria.andLike("shippingName", "%" + order.getShippingName() + "%");
                //criteria.andShippingNameLike("%"+order.getShippingName()+"%");
            }
            if (StringUtils.isNotBlank(order.getShippingCode())) {
                criteria.andLike("shippingCode", "%" + order.getShippingCode() + "%");
                //criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
            }
            if (StringUtils.isNotBlank(order.getUserId())) {
                criteria.andLike("userId", "%" + order.getUserId() + "%");
                //criteria.andUserIdLike("%"+order.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerMessage())) {
                criteria.andLike("buyerMessage", "%" + order.getBuyerMessage() + "%");
                //criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerNick())) {
                criteria.andLike("buyerNick", "%" + order.getBuyerNick() + "%");
                //criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerRate())) {
                criteria.andLike("buyerRate", "%" + order.getBuyerRate() + "%");
                //criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverAreaName())) {
                criteria.andLike("receiverAreaName", "%" + order.getReceiverAreaName() + "%");
                //criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + order.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverZipCode())) {
                criteria.andLike("receiverZipCode", "%" + order.getReceiverZipCode() + "%");
                //criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiver())) {
                criteria.andLike("receiver", "%" + order.getReceiver() + "%");
                //criteria.andReceiverLike("%"+order.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(order.getInvoiceType())) {
                criteria.andLike("invoiceType", "%" + order.getInvoiceType() + "%");
                //criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
            }
            if (StringUtils.isNotBlank(order.getSourceType())) {
                criteria.andLike("sourceType", "%" + order.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(order.getSellerId())) {
                criteria.andLike("sellerId", "%" + order.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+order.getSellerId()+"%");
            }

        }
        List<TbOrder> all = orderMapper.selectByExample(example);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void add(TbOrder order) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART_REDIS_KEY").get(order.getUserId());
        List<Long> orderList = new ArrayList<>();
        double total_money = 0;
        for (Cart cart : cartList) {
            long orderId = idWorker.nextId();
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(orderId);
            tbOrder.setUserId(order.getUserId());
            tbOrder.setPaymentType(order.getPaymentType());
            tbOrder.setStatus("1");
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            tbOrder.setReceiver(order.getReceiver());
            tbOrder.setSourceType(order.getSourceType());
            tbOrder.setSellerId(cart.getSellerId());
            double money = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);
                orderItem.setSellerId(cart.getSellerId());
                TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());
                orderItem.setGoodsId(item.getGoodsId());
                money += orderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(orderItem);
            }
            tbOrder.setPayment(new BigDecimal(money));
            total_money += money;
            orderMapper.insert(tbOrder);
        }
        TbPayLog payLog = new TbPayLog();
        String outTradeNo = idWorker.nextId() + "";
        payLog.setOutTradeNo(outTradeNo);
        payLog.setCreateTime(new Date());
        String ids = orderList.toString().replace("[", "").replace("]", "").replace("", "");
        payLog.setOrderList(ids);
        payLog.setPayType("1");
        payLog.setTotalFee((long) (total_money * 100));
        payLog.setTradeState("0");
        payLog.setUserId(order.getUserId());
        payLogMapper.insert(payLog);
        redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
        redisTemplate.boundHashOps("CART_REDIS_KEY").delete(order.getUserId());


    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);

        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLog.setTradeState("1");//
        payLogMapper.updateByPrimaryKey(payLog);

        //* 2.根据支付日志 获取到商品订单列表 更新商品订单状态
        String orderList = payLog.getOrderList();//   37,38
        String[] split = orderList.split(",");
        for (String orderidstring : split) {// 37 38
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderidstring));
            tbOrder.setStatus("2");//已经付完款
            tbOrder.setUpdateTime(new Date());
            tbOrder.setPaymentTime(tbOrder.getUpdateTime());
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }


    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Override
    /**
     *@Description //用户所有订单，及订单的详情
     *@param  [userId]
     *@return java.util.List<com.pinyougou.pojo.TbOrder>
     *@time 2019-7-24 22:06
     */
    public List<TbOrder> getOrderByStatus(String userId,String status) {
        TbOrder tbOrder = new TbOrder();
        tbOrder.setUserId(userId);
        tbOrder.setStatus(status);
        //找出所有订单
        List<TbOrder> tbOrderList = tbOrderMapper.select(tbOrder);
        //遍历订单，找出订单所有的商品信息
        for (TbOrder order : tbOrderList) {
            //设置店铺名称
            order.setSellerNickName(tbSellerMapper.selectByPrimaryKey(order.getSellerId()).getNickName());
            Long orderId = order.getOrderId();
            TbOrderItem tbOrderItem = new TbOrderItem();
            tbOrderItem.setOrderId(orderId);
            List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.select(tbOrderItem);
            for (TbOrderItem orderItem : tbOrderItemList) {
                TbItem tbItem = itemMapper.selectByPrimaryKey(orderItem.getItemId());
                orderItem.setSpec(tbItem.getSpec().replace("{","")
                        .replace("}","")
                        .replace("\"",""));//设置规格属性
            }
            order.setOrderItemList(tbOrderItemList);
        }

        return tbOrderList;
    }

    @Override
    public void updateOrderStatusAndCreateLog(String out_trade_no, String transaction_id, String userId) {
        TbOrder tbOrder = orderMapper.selectByPrimaryKey(out_trade_no);
        tbOrder.setStatus("2");
        TbPayLog tbPayLog = new TbPayLog();
        tbPayLog.setOutTradeNo(idWorker.nextId()+"");
        tbPayLog.setOutTradeNo(transaction_id);
        tbPayLog.setPayTime(new Date());
        tbPayLog.setUserId(userId);
        tbPayLog.setTotalFee(tbOrder.getPayment().longValue());
        tbPayLog.setCreateTime(tbOrder.getCreateTime());
        tbPayLog.setOrderList(out_trade_no);

        payLogMapper.insert(tbPayLog);
    }
}
