package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.*;
import com.pinyougou.order.service.OrderService;
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
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder TbOrder) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (TbOrder != null) {
            if (StringUtils.isNotBlank(TbOrder.getPaymentType())) {
                criteria.andLike("paymentType", "%" + TbOrder.getPaymentType() + "%");
                //criteria.andPaymentTypeLike("%"+TbOrder.getPaymentType()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getPostFee())) {
                criteria.andLike("postFee", "%" + TbOrder.getPostFee() + "%");
                //criteria.andPostFeeLike("%"+TbOrder.getPostFee()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getStatus())) {
                criteria.andLike("status", "%" + TbOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+TbOrder.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getShippingName())) {
                criteria.andLike("shippingName", "%" + TbOrder.getShippingName() + "%");
                //criteria.andShippingNameLike("%"+TbOrder.getShippingName()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getShippingCode())) {
                criteria.andLike("shippingCode", "%" + TbOrder.getShippingCode() + "%");
                //criteria.andShippingCodeLike("%"+TbOrder.getShippingCode()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getUserId())) {
                criteria.andLike("userId", "%" + TbOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+TbOrder.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getBuyerMessage())) {
                criteria.andLike("buyerMessage", "%" + TbOrder.getBuyerMessage() + "%");
                //criteria.andBuyerMessageLike("%"+TbOrder.getBuyerMessage()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getBuyerNick())) {
                criteria.andLike("buyerNick", "%" + TbOrder.getBuyerNick() + "%");
                //criteria.andBuyerNickLike("%"+TbOrder.getBuyerNick()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getBuyerRate())) {
                criteria.andLike("buyerRate", "%" + TbOrder.getBuyerRate() + "%");
                //criteria.andBuyerRateLike("%"+TbOrder.getBuyerRate()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getReceiverAreaName())) {
                criteria.andLike("receiverAreaName", "%" + TbOrder.getReceiverAreaName() + "%");
                //criteria.andReceiverAreaNameLike("%"+TbOrder.getReceiverAreaName()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + TbOrder.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+TbOrder.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getReceiverZipCode())) {
                criteria.andLike("receiverZipCode", "%" + TbOrder.getReceiverZipCode() + "%");
                //criteria.andReceiverZipCodeLike("%"+TbOrder.getReceiverZipCode()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + TbOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+TbOrder.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getInvoiceType())) {
                criteria.andLike("invoiceType", "%" + TbOrder.getInvoiceType() + "%");
                //criteria.andInvoiceTypeLike("%"+TbOrder.getInvoiceType()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getSourceType())) {
                criteria.andLike("sourceType", "%" + TbOrder.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+TbOrder.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(TbOrder.getSellerId())) {
                criteria.andLike("sellerId", "%" + TbOrder.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+TbOrder.getSellerId()+"%");
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
    public void add(TbOrder TbOrder) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART_REDIS_KEY").get(TbOrder.getUserId());
        List<Long> orderList = new ArrayList<>();
        double total_money = 0;
        for (Cart cart : cartList) {
            long orderId = idWorker.nextId();
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(orderId);
            tbOrder.setUserId(TbOrder.getUserId());
            tbOrder.setPaymentType(TbOrder.getPaymentType());
            tbOrder.setStatus("1");
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setReceiverAreaName(TbOrder.getReceiverAreaName());
            tbOrder.setReceiverMobile(TbOrder.getReceiverMobile());
            tbOrder.setReceiver(TbOrder.getReceiver());
            tbOrder.setSourceType(TbOrder.getSourceType());
            tbOrder.setSellerId(cart.getSellerId());
            double money = 0;
            for (TbOrderItem TbOrderItem : cart.getOrderItemList()) {
                TbOrderItem.setId(idWorker.nextId());
                TbOrderItem.setOrderId(orderId);
                TbOrderItem.setSellerId(cart.getSellerId());
                TbItem item = itemMapper.selectByPrimaryKey(TbOrderItem.getItemId());
                TbOrderItem.setGoodsId(item.getGoodsId());
                money += TbOrderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(TbOrderItem);
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
        payLog.setUserId(TbOrder.getUserId());
        payLogMapper.insert(payLog);
        redisTemplate.boundHashOps("payLog").put(TbOrder.getUserId(), payLog);
        redisTemplate.boundHashOps("CART_REDIS_KEY").delete(TbOrder.getUserId());


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
        if (tbOrderList != null) {
            for (TbOrder TbOrder : tbOrderList) {
                //设置店铺名称
                TbOrder.setSellerNickName(tbSellerMapper.selectByPrimaryKey(TbOrder.getSellerId()).getNickName());
                Long orderId = TbOrder.getOrderId();
                TbOrderItem tbOrderItem = new TbOrderItem();
                tbOrderItem.setOrderId(orderId);
                List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.select(tbOrderItem);
                for (TbOrderItem TbOrderItem : tbOrderItemList) {
                    TbItem tbItem = itemMapper.selectByPrimaryKey(TbOrderItem.getItemId());
                    TbOrderItem.setSpec(tbItem.getSpec().replace("{","")
                            .replace("}","")
                            .replace("\"",""));//设置规格属性
                }
                TbOrder.setOrderItemList(tbOrderItemList);
            }
        }

        return tbOrderList;
    }

    @Override
    public void updateOrderStatusAndCreateLog(String out_trade_no, String transaction_id, String userId) {
        TbOrder tbOrder = orderMapper.selectByPrimaryKey(out_trade_no);
        tbOrder.setStatus("2");
        orderMapper.updateByPrimaryKey(tbOrder);

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
