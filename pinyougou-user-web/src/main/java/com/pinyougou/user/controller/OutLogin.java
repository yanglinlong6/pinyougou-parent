package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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
