package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: com.pinyougou.manager.controller
 * @author: Sun jinwei
 * @create: 2019-07-22 12:31
 * @description:
 **/
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/getName")
    public String getName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return name;
    }

}