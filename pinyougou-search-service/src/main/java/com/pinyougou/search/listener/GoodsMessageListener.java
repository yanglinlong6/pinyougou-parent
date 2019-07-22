package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.impl.ItemSearchServiceImpl;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.search.listener
 * 作者: Yanglinlong
 * 日期: 2019/7/3 21:00
 */
public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchServiceImpl itemSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println(">>>>>>>>>>>>>>>>>>接收数据");
        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s,MessageInfo.class);
                    switch (messageInfo.getMethod()) {
                        case 1: {
                            String contenxt1 = messageInfo.getContext().toString();
                            List<TbItem> tbItems = JSON.parseArray(contenxt1, TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                        }
                        case 2:
                        {
                            String context1 = messageInfo.getContext().toString();
                            List<TbItem> tbItems = JSON.parseArray(context1, TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                        }
                        case 3:
                        {
                            String s1 = messageInfo.getContext().toString();
                            Long[] longs = JSON.parseObject(s1, Long[].class);
                            itemSearchService.deleteByIds(longs);
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
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
