package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.cart.controller
 * 作者: Yanglinlong
 * 日期: 2019/7/14 22:26
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService orderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder order = (TbSeckillOrder) orderService.getUserOrderStatus(userId);
        if (order != null) {
            double v = order.getMoney().doubleValue() * 100;
            long x = (long) v;
            return weixinPayService.createNative(order.getId() + "", x + "");
        }
//        IdWorker idworker = new IdWorker(0, 1);
//        return weixinPayService.createNative(idworker.nextId() + "", "1");
        return null;
    }

    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Result result = new Result(false, "支付失败");
        try {
            int count = 0;
            //1.调用支付的服务 不停的查询 状态
            while (true) {
                Map<String, String> resultMap = weixinPayService.queryStatus(out_trade_no);
                count++;

                if (count >= 16) {
                    result=new Result(false,"支付超时");
                    //关闭微信订单
                    Map map = weixinPayService.closePay(out_trade_no);

                    if ("SUCCESS".equals(map.get("result_code"))|| "ORDERCLOSED".equals(map.get("err_code"))){
                        //删除预订单
                        orderService.deleteOrder(userId);
                    }

                    if ("ORDERPAID".equals(map.get("err_code"))){
                        //已经支付则更新入库
                        orderService.updateOrderStatus(resultMap.get("transaction_id"),userId);
                        return new Result(true,"订单已支付");
                    }else{
                        System.out.println("由于微信端错误 ---- ");
                    }
                    break;
                }

                Thread.sleep(3000);

                //如果超时5分钟就直接退出。

                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result = new Result(true, "支付成功");
                    orderService.updateOrderStatus(resultMap.get("transaction_id"), userId);
                    break;
                }
            }
            //2.返回结果
            return result;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result(false, "支付失败");
        }
    }
}


