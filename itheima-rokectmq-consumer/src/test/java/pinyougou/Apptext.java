package pinyougou;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou
 * 作者: Yanglinlong
 * 日期: 2019/7/3 20:23
 */
@ContextConfiguration("classpath:spring-consumer.xml")
@RunWith(SpringRunner.class)
public class Apptext {
    @Test
    public void consumer() throws Exception{

        Thread.sleep(1000000);
    }

}
