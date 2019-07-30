package com.pinyougou.seckill.task;


import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import com.pinyougou.mapper.TbSeckillGoodsMapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.seckill.task
 * 作者: Yanglinlong
 * 日期: 2019/7/16 21:57
 */
@Component
public class GoodsTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 一个被反复执行的方法,
     * 通过注解 来指定 cron表达式:指定何时执行的
     * 任务类的作用就是每隔30秒 重新从数据库中查询所有的秒杀商品 将其存入到redis中，请注意条件。
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoods() {

        //1.注入dao

        //2.执行查询语句  符合条件的查询语句
        //select * from tb_seckill_good where status=1 and stock_count>0 and   开始<当前时间<结束时间 and id not in (12,3,3,4)
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "1");//审核过的
        criteria.andGreaterThan("stockCount", 0);//剩余库存>0
        Date date = new Date();
        criteria.andLessThan("startTime", date);
        criteria.andGreaterThan("endTime", date);


        //排除redis中已有的商品的列表
        Set<Long> seckillGoods = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();
        if (seckillGoods != null && seckillGoods.size() > 0) {
            criteria.andNotIn("id", seckillGoods);
        }

        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        //3.注入redisTemplate

        //4.存储到redis中  key  value   hash   bigkey  field  value
        for (TbSeckillGoods good : seckillGoodsList) {

            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(good.getId(), good);

            pushGoodsList(good);
        }

//        for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
//            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(tbSeckillGoods.getId(), tbSeckillGoods);
//        }
        redisTemplate.boundValueOps("n+ihao").set("nishi yife ");
        //System.out.println("chenggong");
    }

    public void pushGoodsList(TbSeckillGoods goods) {
        //向同一个队列中压入商品数据
        for (Integer i = 0; i < goods.getStockCount(); i++) {
            //库存为多少就是多少个SIZE 值就是id即可
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + goods.getId()).leftPush(goods.getId());
        }
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void popGoodsList(){
        System.out.println("=========================================");
        Date date = new Date();
        //从缓存中拿取所有已经上架的秒杀商品（应该是从缓存拿，而不是数据库拿）
        List<TbSeckillGoods> goods = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).values();

        for (TbSeckillGoods good : goods) {
            System.out.println(good.getEndTime().getTime()+"----------"+date.getTime());
            if (good.getEndTime().getTime()<date.getTime()){//如果结束日期小于当前日期，则表示过期
                System.out.println("此秒杀商品没有卖完："+good);//打印要删除的商品
                if (good.getStockCount()>0){//判断商品剩余库存数是否大于0
                    //库存都没有卖完
                    //将缓存中的信息存到数据库(包括剩余的库存量)
                    seckillGoodsMapper.updateByPrimaryKeySelective(good);
                    //若队列中存在未卖完的商品，也要清除队列
                    redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+good.getId()).remove(0,-1);
                    System.out.println("删除成功");
                }
                //删除秒杀商品
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(good.getId());
            }
        }
        date=null;//对象被赋值为null将被视为垃圾
    }

}
