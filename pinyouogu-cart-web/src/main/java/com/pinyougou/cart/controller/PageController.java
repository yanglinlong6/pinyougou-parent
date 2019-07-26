package com.pinyougou.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.seckill.controller
 * 作者: Yanglinlong
 * 日期: 2019/7/18 9:57
 *
 * @author 59276
 */
@Controller
public class PageController {
    @RequestMapping("/page/login")
    public String showPage(String url) {
        return "redirect:" + url;
    }
}
