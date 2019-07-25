package com.pinyougou.seckill.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.seckill.listener
 * 作者: Yanglinlong
 * 日期: 2019/7/18 22:55
 */
public class DelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeixinPayService weixinPayService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    System.out.println("延时消息接收");
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    System.out.println(messageInfo);
                    //订单更新 标识即可
                    if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE) {


                        //获取延迟消息中Redis中的1分钟前未支付订单的信息
                        TbSeckillOrder tbSeckillOrder = JSON.parseObject(messageInfo.getContext().toString(), TbSeckillOrder.class);

                        //获取数据库中的订单的信息
                        TbSeckillOrder seckillOrder = seckillOrderMapper.selectByPrimaryKey(tbSeckillOrder.getId());
                        //数据库找不到，说明有可能没支付
                        if (seckillOrder == null) {//数据库中找不到
                            //关闭微信订单
                            Map map = weixinPayService.closePay(tbSeckillOrder.getId()+"");

                            if ("SUCCESS".equals(map.get("result_code"))|| "ORDERCLOSED".equals(map.get("err_code"))){
                                //删除预订单
                                seckillOrderService.deleteOrder(tbSeckillOrder.getUserId());
                            }

                            if ("ORDERPAID".equals(map.get("err_code"))){//说明最后一秒已付款,但是数据库没有
                                //去微信端查找流水号
                                Map<String,String>  resultMap = weixinPayService.queryStatus(tbSeckillOrder.getId()+"");
                                //已经支付则更新入库
                                seckillOrderService.updateOrderStatus(resultMap.get("transaction_id"),tbSeckillOrder.getUserId());
                            }else{
                                System.out.println("由于微信端错误");
                            }
                            break;

                        }
                        //有订单 无需关心
                    }

                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
