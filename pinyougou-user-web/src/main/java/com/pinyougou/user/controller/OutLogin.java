package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * 退出登录的controller
 */
@Controller
@RequestMapping("/logout")
public class OutLogin {
    @Reference
    private UserService userService;

    @RequestMapping("/outLogin")
    public String outLogin() {
       return "redirect:/logout/cas";
    }
}
