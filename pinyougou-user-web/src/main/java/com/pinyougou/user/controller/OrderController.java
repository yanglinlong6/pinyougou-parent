package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbOrder;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping(path = "/getOrderByStatus")
    public List<TbOrder> getAllOrder(@RequestParam(required = false)String status){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TbOrder> tbOrderList = orderService.getOrderByStatus(userId,status);
        return tbOrderList;
    }

    @RequestMapping(path = "/payNow")
    /**
    *@Description //未支付订单  支付二维码生成
    *@param  [orderId]
    *@return java.util.Map
    *@time 2019-7-26 18:14
    */
    public Map payNow(@RequestParam Long orderId){
        TbOrder tbOrder = orderService.selectByPrimaryKey(orderId);
        if (tbOrder != null) {
           return weixinPayService.createNative(tbOrder.getOrderId()+"",(long)(tbOrder.getPayment().doubleValue()*100)+"");
        }
        return null;
    }

    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no) {
        //直接轮询调用 pay-service的接口方法 查询该out_trade_no对应的支付状态 返回数据
        Result result = new Result(false, "支付失败");

        //有一个超时时间 如果进过了5分钟还没支付就是表示超时
        int count = 0;
        while (true) {
            Map<String, String> resultMap = weixinPayService.queryStatus(out_trade_no);

            count++;

            if (count >= 4) {
                result = new Result(false, "支付超时");
                break;
            }

            if (resultMap == null) {
                result = new Result(false, "支付失败");
                break;
            }
            if ("SUCCESS".equals(resultMap.get("trade_state"))) {//支付成功
                result = new Result(true, "支付成功");

                       /* + 修改商品的订单的状态
                        + 支付日志的订单的状态
                        + 删除掉redis中的支付日志*/
                orderService.updateOrderStatus(out_trade_no, resultMap.get("transaction_id"));

                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
