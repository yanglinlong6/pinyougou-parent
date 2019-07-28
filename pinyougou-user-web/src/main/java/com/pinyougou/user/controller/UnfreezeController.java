package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 专门解冻的controller
 */

@RestController
@RequestMapping("/unfreeze")
public class UnfreezeController {

    @Reference
    private UserService userService;

    /**
     * 对账号进行解冻
     * @return
     */
    @RequestMapping("/unfree")
    public Result unfreeze() {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(name);
            TbUser tbUser = new TbUser();
            tbUser.setUsername(name);
            TbUser tbUser1 = userService.selectOne(tbUser);
            tbUser1.setStatus("1");
            tbUser1.setLastLoginTime(new Date());
            userService.update(tbUser1);
            return new Result(true,"解冻成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"服务器繁忙，请稍后再试");
        }
    }

}
