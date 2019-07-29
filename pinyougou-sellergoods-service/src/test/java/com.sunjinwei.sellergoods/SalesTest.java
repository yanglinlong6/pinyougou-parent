package com.sunjinwei.sellergoods;


import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Sale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.NumberFormat;
import java.util.*;

/**
 * @program: com.sunjinwei.sellergoods
 * @author: Sun jinwei
 * @create: 2019-06-18 12:55
 * @description:
 **/
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class SalesTest {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbGoodsMapper goodsMapper;

    @Test
    public void findOne() {
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
        System.out.println("购买 的总数量为==="+totalNum);
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
        //4sale类存储的一级分类名称肯定有重复的 这时候需要进行统计
        //遍历set一级分类 遍历sale集合 判断是否是那个一级分类 然后进行相加
        Map<String, Integer> map = new HashMap<>();
        System.out.println("商品 分类名称：   "+set);
        for (Sale sale : saleList) {
            System.out.println(sale.getName()+"  "+sale.getNum());
        }

        Integer count = 0;

        for (String s : set) {
            //set每次循环 创建一个Integer对象用来记录一个一级分类购买的数量
            for (Sale sale : saleList) {
                if (sale.getName().equals(s)) {
                    count += sale.getNum();
                } else {
                    map.put(s,sale.getNum());
                }
            }
            map.put(s,count);
            count=0;

        }

        System.out.println("返回的数据为===="+map);

    }

    /**
     * 转化为百分号
     */
    @Test
    public void test02(){
        int a=3;
        int b=9;
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float)a/(float)b*100)+"%";
        System.out.println("百分比为:" + result );


    }

}