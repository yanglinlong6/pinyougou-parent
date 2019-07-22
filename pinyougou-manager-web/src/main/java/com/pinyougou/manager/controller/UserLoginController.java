package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.manager.controller
 * 作者: Yanglinlong
 * 日期: 2019/7/22 11:25
 */
@RestController
@RequestMapping("/login")
public class UserLoginController {

    @RequestMapping("/getName")
    public String getUserInfo() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
