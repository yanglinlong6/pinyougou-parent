package com.pinyougou;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.SQLOutput;
import java.util.*;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou
 * 作者: Yanglinlong
 * 日期: 2019/7/3 8:53
 */
public class App {

    public static void main(String[] args) throws Exception {
        //创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模板所在的目录
        configuration.setDirectoryForTemplateLoading(new File("C:\\EnCoding\\pinyougouMajor\\pinyougou-parent\\itheima-freemaker\\src\\main\\resources\\template"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("demo.ftl");
        //创建数据模型
        Map map = new HashMap();
        map.put("name", "张三 ");
        map.put("message", "欢迎来到神奇的品优购世界！");
        map.put("success", true);
        List goodsList =new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        map.put("goodsList", goodsList);
        map.put("today", new Date());
        //6.创建Writer对象
        Writer out = new FileWriter(new File("C:\\EnCoding\\pinyougouMajor\\pinyougou-parent\\itheima-freemaker\\src\\main\\resources\\html\\test.html"));
        //7.输出
        template.process(map, out);
        //8.关闭Writer对象
        out.close();
    }
}
