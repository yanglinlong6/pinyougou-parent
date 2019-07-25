package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.user.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @RequestMapping(path = "/getAllOrder")
    public List<TbOrder> getAllOrder(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.getAllOrder(userId);
    }
}
