package com.pinyougou.sellergoods.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.sellergoods.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder> implements OrderService {


    private TbOrderMapper orderMapper;

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

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    /**
     * 获取销售额
     *
     * @param sellerId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Map<String, Double> getSalesReport(String sellerId, String beginTime, String endTime) {
        Map<String, Double> map = new HashMap();
        try {
            //1根据条件 当前商家 当前时间段的
            Example example = new Example(TbOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("sellerId", sellerId); //确定当前商家
            //将String 转换成Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date dateStart = dateFormat.parse(beginTime);
            Date dateEnd = dateFormat.parse(endTime);
            //设置时间区间
            criteria.andBetween("paymentTime", dateStart, dateEnd);
            List<TbOrder> tbOrders = orderMapper.selectByExample(example);

            //2获取到该时间段该商家的订单 进行遍历
            if (tbOrders.size() > 0 && tbOrders != null) {
                //如果不为null name根据订单id获取订单明细 获取总价格
                for (TbOrder tbOrder : tbOrders) {
                    Long orderId = tbOrder.getOrderId(); //获取到订单id 订单里面有好多订单明细
                    Date paymentTime = tbOrder.getPaymentTime(); //获取到该订单的支付时间
                    //Date换成String
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String dateStr = sdf.format(paymentTime);
                    Example example1 = new Example(TbOrderItem.class);
                    Example.Criteria criteria1 = example1.createCriteria();
                    criteria1.andEqualTo("orderId", orderId);
                    List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example1);
                    //获取到订单明细来进行加 价格
                    Double totalMoney=0.0;
                    for (TbOrderItem tbOrderItem : tbOrderItems) {
                        BigDecimal totalFee = tbOrderItem.getTotalFee(); //获取一个订单明细的总价
                        System.out.println("订单明细中总价为   "+totalFee);
                        //进行累加销售额
                        totalMoney+= totalFee.doubleValue();
                    }
                    map.put(dateStr,totalMoney); //将累加的销售额存入map
                }
            }else {
                return new HashMap();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return new HashMap();
        }
        return map;
    }

}
