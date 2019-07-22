package com.pinyougou;

import com.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou
 * 作者: Yanglinlong
 * 日期: 2019/6/29 21:58
 */
public class App {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        ItemService itemService = context.getBean(ItemService.class);
        itemService.ImportDataToEs();
    }
}
