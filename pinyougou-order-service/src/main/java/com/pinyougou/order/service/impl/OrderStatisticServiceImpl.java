package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderStatisticService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import entity.OrderStatistic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class OrderStatisticServiceImpl extends CoreServiceImpl<TbGoods> implements OrderStatisticService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;


    private TbGoodsMapper goodsMapper;

    @Autowired
    public OrderStatisticServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper,TbGoods.class);
        this.goodsMapper=goodsMapper;
    }

    /**
     * 条件分页查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<OrderStatistic>  findPage(Integer pageNo, Integer pageSize, TbOrder order){
        System.out.println("进来了，进来了");

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(order!=null){//如果条件不为空
            /*===================================判断订单 创建时间是 */
            if (StringUtils.isNotBlank(order.getCreateTime()+"")) {
                criteria.andGreaterThan("createTime",order.getCreateTime());
                //criteria.andLike("sellerId", "%" + order.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+order.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(order.getUpdateTime()+"")) {
                criteria.andLessThan("updateTime",order.getUpdateTime());
                //criteria.andLike("sellerId", "%" + order.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+order.getSellerId()+"%");
            }
        }
        List<TbOrder> orderList = orderMapper.selectByExample(example);


        if(orderList!=null && orderList.size()>0 ){
            HashMap<Long, OrderStatistic> orderStatisticHashMap = new HashMap<>();

            //一个order对应了多个orderItem
            for (TbOrder order1 : orderList) {
                System.out.println(order1+"-----------订单");

                Example example2 = new Example(TbOrderItem.class);
                Example.Criteria criteria2 = example2.createCriteria();

                if (StringUtils.isNotBlank(order1.getOrderId()+"")) {
                    criteria2.andEqualTo("orderId",order1.getOrderId());
                }

                //通过order找到多个orderItem
                List<TbOrderItem> orderItems = orderItemMapper.selectByExample(example2);

                //设置分页条件
                PageHelper.startPage(pageNo,pageSize);

                //遍历所有的订单项
                for (TbOrderItem orderItem : orderItems) {

                    //再由orderItem获取goodID
                    System.out.println(orderItem+"-------订单项");
                    Long goodsId = orderItem.getGoodsId();

                    OrderStatistic orderStatisticByHashMap = orderStatisticHashMap.get(goodsId);
                    //说明hashmap里面有
                    if (orderStatisticByHashMap!=null){

                        orderStatisticByHashMap.setNum(orderStatisticByHashMap.getNum()+orderItem.getNum());

                        orderStatisticByHashMap.setTotalFee(orderStatisticByHashMap.getTotalFee().add(orderItem.getTotalFee()));
                    }else {
                        TbGoods TbGoods = goodsMapper.selectByPrimaryKey(goodsId);

                        OrderStatistic orderStatistic = new OrderStatistic();
                        orderStatistic.setGoods(TbGoods);
                        orderStatistic.setNum(orderItem.getNum());

                        orderStatistic.setPrice(TbGoods.getPrice());
                        orderStatistic.setTotalFee(orderItem.getTotalFee());
                        orderStatistic.setGoodsId(goodsId);

                        orderStatisticHashMap.put(goodsId,orderStatistic);
                    }
                }
            }
            ArrayList<OrderStatistic> orderStatistics = new ArrayList<>(orderStatisticHashMap.values());
            PageInfo<OrderStatistic> info = new PageInfo<OrderStatistic>(orderStatistics);
            info.setList(orderStatistics);
            System.out.println(info);
            //序列化再反序列化
            String s = JSON.toJSONString(info);
            PageInfo<OrderStatistic> pageInfo = JSON.parseObject(s, PageInfo.class);
            return pageInfo;//等下修改

        }else {
            PageInfo<OrderStatistic> info = new PageInfo<OrderStatistic>();
            return info;
        }


    }
}
