package com.pinyougou.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.user.controller
 * 作者: Yanglinlong
 * 日期: 2019/7/10 20:00
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Reference
    private UserService userService;

    @RequestMapping("/name")
    public String getName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("sadas"+name);
        userService.addCount(name);
        return name;
    }
}
