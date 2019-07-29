package com.pinyougou.user.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.user.service.UserService;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/pagemark")
@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
public class PageController {
    // private static List<Long> markListNew = new ArrayList<>();
    
    @Reference
    private UserService userService;
    
    @Reference
    private CartService cartService;

    @RequestMapping("/addFootMark")
    public Result addFootMark(@RequestParam(value = "id") Long id, HttpServletRequest request,
        HttpServletResponse response) {
        System.out.println(id);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(username);
            if ("anonymousUser".equals(username)) {
                String markListstring = CookieUtil.getCookieValue(request, "markList", true);
                if (StringUtils.isEmpty(markListstring)) {
                    markListstring = "[]";
                }
                List<Long> markList = JSON.parseArray(markListstring, Long.class);
                markList.add(id);
                String jsonString = JSON.toJSONString(markList);
                System.out.println("没有登陆:" + markList);
                CookieUtil.setCookie(request, response, "markList", jsonString, 7 * 24 * 3600, true);
            }
            else {
                System.out.println("已经登陆");
                String markListstring = CookieUtil.getCookieValue(request, "markList", true);
                if (StringUtils.isEmpty(markListstring)) {
                    markListstring = "[]";
                }
                List<Long> markList = JSON.parseArray(markListstring, Long.class);
                List<Long> markListNew = userService.getMarkListFromRedis(username);
                if (markList != null) {
                    markListNew.addAll(markList);
                }
                CookieUtil.deleteCookie(request, response, "markList");
                markListNew.add(id);
                System.out.println("登陆以后:" + markListNew);
                userService.addFootMark(username, markListNew);
            }
            return new Result(true, "添加收藏成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加收藏失败");
        }
    }
    
}
