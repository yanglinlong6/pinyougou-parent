package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.MyDateUtil;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.sellergoods.service.SalesService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: com.pinyougou.sellergoods.service.impl
 * @author: Sun jinwei
 * @create: 2019-07-24 21:44
 * @description:
 **/
@Service
public class SalesServiceImpl implements SalesService {
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 获取销售额---折线图数据 key为时间 value为金额
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
            //将String 转换成Date 24h小时制 所以是HH
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
                    //Date换成String 并且使用24h小时制 所以是HH
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateStr = sdf.format(paymentTime);
                    Example example1 = new Example(TbOrderItem.class);
                    Example.Criteria criteria1 = example1.createCriteria();
                    criteria1.andEqualTo("orderId", orderId);
                    List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example1);
                    //获取到订单明细来进行加 价格
                    Double totalMoney = 0.0;
                    for (TbOrderItem tbOrderItem : tbOrderItems) {
                        BigDecimal totalFee = tbOrderItem.getTotalFee(); //获取一个订单明细的总价
                        //进行累加销售额
                        totalMoney += totalFee.doubleValue();
                    }
                    map.put(dateStr, totalMoney); //将累加的销售额存入map
                }
            } else {
                return new HashMap();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return new HashMap();
        }
        return map;
    }

    /**
     * 饼状图1--商品销售量
     * 先从item分类表中获取全部一级分类信息
     * 从订单表中获取订单商品中商品分类 统计购买的总数量
     *
     * @return
     */
    @Override
    public Map<String, Float> findSalesReports01() {
        //1从item分类表中获取一级分类信息 作为map中的key
        TbItemCat tbItemCat = new TbItemCat();
        tbItemCat.setParentId((long) 0);
        List<TbItemCat> itemCatList = itemCatMapper.select(tbItemCat);
        Set<String> keyList = new HashSet<>();
        for (TbItemCat itemCat : itemCatList) {
            keyList.add(itemCat.getName());
        }
        //2从订单表中获取购买的商品属于哪些分类 将goodId和num使用map来存储
        Map<Long, Integer> map = new HashMap<>();
        List<TbOrderItem> orderItemList = tbOrderItemMapper.selectAll();
        for (TbOrderItem tbOrderItem : orderItemList) {
            map.put(tbOrderItem.getGoodsId(), tbOrderItem.getNum());
        }
        //3遍历订单map 找出key对应一级分类名称 并将map的value存储到新的map中
        //新的map中key为一级分类,相当于itemCat表中的parentId;value为卖出的数量
        Map<Long, Integer> map02 = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : map.entrySet()) {
            //根据goodId找出对应的一级分类
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(entry.getKey());
            map02.put(tbGoods.getCategory1Id(), entry.getValue());
        }
        //4需要将分类id替换为string字符串中文名称
        for (Map.Entry<Long, Integer> entry : map02.entrySet()) {
            TbItemCat itemCat = new TbItemCat();
            itemCat.setParentId(entry.getKey());
            List<TbItemCat> tbItemCats = itemCatMapper.select(itemCat);

        }


        return null;
    }


}