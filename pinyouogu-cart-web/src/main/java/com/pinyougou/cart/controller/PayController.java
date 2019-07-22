package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.sun.org.apache.regexp.internal.RE;
import entity.Result;
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
    private OrderService orderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
        if (payLog != null) {
            return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
        }
//        IdWorker idworker = new IdWorker(0, 1);
//        return weixinPayService.createNative(idworker.nextId() + "", "1");
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


