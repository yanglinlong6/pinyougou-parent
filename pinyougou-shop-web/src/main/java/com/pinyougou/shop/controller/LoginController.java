package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.shop.controller
 * 作者: Yanglinlong
 * 日期: 2019/6/24 22:11
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/getName")
    public String getLoginName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
