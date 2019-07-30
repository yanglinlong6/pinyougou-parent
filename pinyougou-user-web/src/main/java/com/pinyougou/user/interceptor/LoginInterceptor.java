package com.pinyougou.user.interceptor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.MyDateUtil;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 自定义拦截器，在访问控制器之前进行拦截
 */

public class LoginInterceptor implements HandlerInterceptor {

    @Reference
    private UserService userService;

    //该方法是在controller方法之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean flag = false;
        System.out.println("进入拦截器了");
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //匿名用户需要放行
        if ("anonymousUser".equals(name)) {
            return true;
        } else {
            System.out.println(name);
            TbUser user = new TbUser();
            user.setUsername(name);
            System.out.println(userService);
            //在这里先进行判断该用户有没有超过三个月没登录
            TbUser tbUser = userService.selectOne(user);
            Date lastLoginTime = tbUser.getLastLoginTime();
            //判断是否为空
            if ( lastLoginTime != null ) {
                String last = MyDateUtil.toString(lastLoginTime, "yyyy-MM-dd");
                String now = MyDateUtil.toString(new Date(), "yyyy-MM-dd");
                boolean isYes = MyDateUtil.isGreaterThanThreeMonths(last, now, "yyyy-MM-dd");
                if ( isYes ) {
                    //超过三个月了,修改字段
                    tbUser.setStatus("2");
                } else {
                    tbUser.setLastLoginTime(new Date());
                }
            } else {
                tbUser.setLastLoginTime(new Date());
            }

            //更新数据库
            userService.updateByPrimaryKey(tbUser);

            String status = tbUser.getStatus();
            System.out.println(status);
            if ( "2".equals(status) ) {
                //该用户已冻结
                redirect(request, response);
            } else {
                flag = true;
            }

            return flag;
        }
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String accept = request.getHeader("accept");
        if(accept.contains("application/json")) {
            System.out.println("这是一个ajax请求");
            response.setStatus(900);

        }else {
            System.out.println("这不是一个ajax请求");
            //进行重定向
            response.sendRedirect("/unfreeze.html");
        }
    }
}

