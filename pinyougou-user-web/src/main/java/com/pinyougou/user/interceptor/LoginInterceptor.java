package com.pinyougou.user.interceptor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser user = new TbUser();
        user.setUsername(name);
        System.out.println(userService);
        TbUser tbUser = userService.selectOne(user);
        String status = tbUser.getStatus();
        System.out.println(status);
            if("2".equals(status)) {
                //该用户已冻结
                redirect(request,response);
            }else {
                flag = true;
            }

        return flag;
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String accept = request.getHeader("accept");
        if(accept.contains("application/json")) {
            System.out.println("这是一个ajax请求");
            response.setStatus(900);
        }else {
            System.out.println("这不是一个ajax请求");
            //进行重定向
            response.sendRedirect("/outLogin.shtml");
        }
    }
}

