package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.thread.CreateOrderThread;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.IfProfileValue;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder> implements SeckillOrderService {


    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, TbSeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (StringUtils.isNotBlank(seckillOrder.getUserId())) {
                criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getSellerId())) {
                criteria.andLike("sellerId", "%" + seckillOrder.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getStatus())) {
                criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverAddress())) {
                criteria.andLike("receiverAddress", "%" + seckillOrder.getReceiverAddress() + "%");
                //criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + seckillOrder.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getTransactionId())) {
                criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
                //criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
            }

        }
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CreateOrderThread createOrderThread;

    @Override
    public void submitOrder(Long seckillId, String userId) {

        //判断是否已经在排队中了
        //如果有
        if (redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId) != null) {
            throw new RuntimeException("已在排队中");
        }
        //如果有支付订单
        if (redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId) != null) {
            throw new RuntimeException("有未支付的订单");
        }

        Long goodsId = (Long) redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).rightPop();
        if (goodsId == null) {
            //说明商品已经没有库存了
            throw new RuntimeException("商品已被抢光");

        }

        //用户进入排队中
        redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(new SeckillStatus(userId, seckillId, SeckillStatus.SECKILL_queuing));

        //设置排队标识
        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId, seckillId);

        createOrderThread.handleOrder();
//        TbSeckillGoods killgoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);

//        if (killgoods == null || killgoods.getStockCount() <= 0) {
//            throw new RuntimeException("商品已被抢光");
//        }
//        killgoods.setStockCount(killgoods.getStockCount() - 1);
//        redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId, killgoods);
//
//        if (killgoods.getStockCount() <= 0) {
//            seckillGoodsMapper.updateByPrimaryKey(killgoods);
//            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillId);
//        }
//        long orderId = idWorker.nextId();
//        TbSeckillOrder seckillOrder = new TbSeckillOrder();
//        seckillOrder.setId(orderId);
//        seckillOrder.setSeckillId(seckillId);
//        seckillOrder.setUserId(userId);
//        seckillOrder.setMoney(killgoods.getCostPrice());
//        seckillOrder.setSellerId(killgoods.getSellerId());
//        seckillOrder.setCreateTime(new Date());
//        seckillOrder.setStatus("0");
//        redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(userId, seckillId);

    }

    @Override
    public TbSeckillOrder getUserOrderStatus(String userId) {
        //返回订单对象
        return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
    }

    @Override
    public void updateOrderStatus(String transaction_id, String userId) {

        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if (seckillOrder != null) {
            seckillOrder.setPayTime(new Date());
            seckillOrder.setStatus("1");
            seckillOrder.setTransactionId(transaction_id);
            //存储到数据库中
            seckillOrderMapper.insert(seckillOrder);

            //删除预订单即可
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
        }
    }

    @Override
    public void deleteOrder(String userId) {

        //获取秒杀订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if (seckillOrder == null) {
            System.out.println("没有该订单");
            return;
        }
        Long seckillId = seckillOrder.getSeckillId();

        //获取商品对象
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);

        //说明 redis中已经没有了
        if (seckillGoods == null) {
            //从数据库查询
            seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillId);
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            //重新存储到REDIS中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId, seckillGoods);
        } else {
            //1.恢复库存
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId, seckillGoods);
        }
        //商品队列中恢复元素
        redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).leftPush(seckillId);

        System.out.println("删除该预订单");
        //删除该预订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
    }
}
