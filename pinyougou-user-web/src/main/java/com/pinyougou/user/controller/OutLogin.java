package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * 退出登录的controller
 */
@Controller
public class OutLogin {
    @Reference
    private UserService userService;

    @RequestMapping("/outLogin")
    public String outLogin() {
        System.out.println(userService);
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //记录最后登录时间
        System.out.println(name);
            TbUser tbUser = new TbUser();
            tbUser.setUsername(name);
            tbUser.setLastLoginTime(new Date());
            userService.update(tbUser);
       return "redirect:/logout/cas";
    }
}
