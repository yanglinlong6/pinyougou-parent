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
import entity.Sale;
import entity.SaleCount;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.NumberFormat;
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
     * 折线图数据-获取销售额
     * key为时间 value为金额
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
            Date dateStart = MyDateUtil.toDate(beginTime, "yyyy-MM-dd HH:mm");
            Date dateEnd = MyDateUtil.toDate(endTime, "yyyy-MM-dd HH:mm");
            //设置时间区间
            criteria.andBetween("paymentTime", dateStart, dateEnd);
            List<TbOrder> tbOrders = orderMapper.selectByExample(example);

            System.out.println("该时间段查询的订单长度为====="+tbOrders.size());

            //2获取到该时间段该商家的订单 进行遍历
            if (tbOrders.size() > 0 && tbOrders != null) {
                //如果不为null name根据订单id获取订单明细 获取总价格
                for (TbOrder tbOrder : tbOrders) {
                    Long orderId = tbOrder.getOrderId(); //获取到订单id 订单里面有好多订单明细
                    Date paymentTime = tbOrder.getPaymentTime(); //获取到该订单的支付时间
                    //Date换成String 并且使用24h小时制 所以是HH
                    String dateStr = MyDateUtil.toString(paymentTime, "yyyy-MM-dd HH:mm");
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
                    System.out.println("销售额为====="+totalMoney);
                    map.put(dateStr, totalMoney); //将累加的销售额存入map
                }
            } else {
                return new HashMap();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return new HashMap();
        }
        System.out.println("map封装的数据为     "+map);
        return map;
    }

    /**
     * 饼状图--商品销售量
     * 先从item分类表中获取全部一级分类信息
     * 从订单表中获取订单商品中商品分类 统计购买的总数量
     *
     * @return
     */
    @Override
    public List<SaleCount> findSalesReports01() {
        //1从订单表中获取商品goodsId 还有num 购买的数量--使用map进行存储
        List<TbOrderItem> orderItemList = tbOrderItemMapper.selectAll();
        //创建list存储订单的销售量
        List<Sale> saleList = new ArrayList<>();
        Integer totalNum = 0; //声明一个变量定义订单的购买的所有商品数量
        for (TbOrderItem tbOrderItem : orderItemList) {
            Sale sale = new Sale();
            sale.setGoodsId(tbOrderItem.getGoodsId()); //设置goodId
            sale.setNum(tbOrderItem.getNum());  //设置商品的购买数量
            saleList.add(sale);
            totalNum += tbOrderItem.getNum(); //获取到所有的商品数量
        }
        //2根据goodId和goods表的id进行匹配 获取到一级分类的id
        List<TbGoods> tbGoods = goodsMapper.selectAll();
        for (Sale sale : saleList) {
            for (TbGoods tbGood : tbGoods) {
                if (sale.getGoodsId().longValue() == tbGood.getId().longValue()) {
                    sale.setCategory1Id(tbGood.getCategory1Id()); //设置一级分类ID
                }
            }
        }
        //3将一级分类的id转换成中文名称
        List<TbItemCat> tbItemCats = itemCatMapper.selectAll();
        Set<String> set = new HashSet<>();  //定义一个集合存储一级分类名称
        for (TbItemCat tbItemCat : tbItemCats) {
            for (Sale sale : saleList) {
                if (tbItemCat.getId().longValue() == sale.getCategory1Id().longValue()) {
                    sale.setName(tbItemCat.getName());   //设置一级分类的名称
                    set.add(tbItemCat.getName());
                }
            }
        }
        //遍历set一级分类 遍历sale集合 判断是否是那个一级分类 然后进行相加
        Map<String, Integer> map = new HashMap<>();
        //声明一个计数器 用来记录每个分类下面有多少商品数量
        Integer count = 0;
        for (String s : set) {
            //set每次循环 创建一个Integer对象用来记录一个一级分类购买的数量
            for (Sale sale : saleList) {
                if (sale.getName().equals(s)) {
                    count += sale.getNum();
                } else {
                    map.put(s, sale.getNum());
                }
            }
            map.put(s, count);
            count = 0; //每个一级分类统计完数量之后都要进行情况 这样下一个分类就数量就重新开始
        }
        //创建存储饼状图数据的list  将数据使用pojo封装 这样前端js中可以更好获取数据
        List<SaleCount> saleCountList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            SaleCount saleCount = new SaleCount();
            saleCount.setCategoryName(entry.getKey()); //设置商品的一级分类
            saleCount.setCount(entry.getValue().toString()); //设置一级分类对应的商品总数量
            saleCountList.add(saleCount);
        }
        return saleCountList;
    }

    /**
     * 一种可以将两个数相除变成百分数的方法 -- 当然这里已经不需要了
     * 需要使用到NumberFormat格式化对象 最终结果转化为字符串来存储
     *
     * @param totalNum
     * @param map
     */
    private void changePercent(float totalNum, Map<String, Integer> map) {
        //创建存储饼状图数据的map 将数据分别封装在key 和 value 中
        Map<String, String> stringMap = new HashMap<>();
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String result = numberFormat.format((float) entry.getValue() / totalNum * 100 + "%");
            stringMap.put(entry.getKey(), result);
        }
    }
}