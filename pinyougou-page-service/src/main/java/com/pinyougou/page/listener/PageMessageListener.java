package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目名:pinyougou-parent 包名: com.pinyougou.page.listener 作者: Yanglinlong 日期: 2019/7/3 21:21
 */
public class PageMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        System.out.println(">>>>当前的线程>>>>" + Thread.currentThread().getName());
        System.out.println(itemPageService);
        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    //2.获取消息体 bytes
                    byte[] body = msg.getBody();
                    //3.转成字符串  Messageinfo
                    String info1 = new String(body);
                    //4.转成对象
                    MessageInfo info = JSON.parseObject(info1, MessageInfo.class);
                    //5.判断 类型 //add  update  delete  分别进行生成静态页 和删除静态页
                    System.out.println(info);
                    switch (info.getMethod()) {
                        case MessageInfo.METHOD_ADD: {
                            Object context1 = info.getContext();
                            String s = context1.toString();
                            List<TbItem> itemList = JSON.parseArray(s, TbItem.class);
                            Set<Long> set = new HashSet<>();
                            for (TbItem tbItem : itemList) {
                                Long goodsId = tbItem.getGoodsId();
                                set.add(goodsId);
                            }
                            for (Long aLong : set) {
                                itemPageService.genItemHtml(aLong);
                            }
                            System.out.println(info + "新增");
                            break;
                        }
                        case MessageInfo.METHOD_UPDATE: {
                            Object context1 = info.getContext();
                            String s = context1.toString();
                            List<TbItem> itemList = JSON.parseArray(s, TbItem.class);
                            Set<Long> set = new HashSet<>();
                            for (TbItem tbItem : itemList) {
                                Long goodsId = tbItem.getGoodsId();
                                set.add(goodsId);
                            }
                            for (Long aLong : set) {
                                itemPageService.genItemHtml(aLong);
                            }
                            System.out.println(info + "更新");
                            break;
                        }
                        case MessageInfo.METHOD_DELETE: {
                            String s = info.getContext().toString();
                            Long[] longs = JSON.parseObject(s, Long[].class);
                            System.out.println(longs);
                            itemPageService.deleteById(longs);
                            break;
                        }
                        default:
                            break;
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
