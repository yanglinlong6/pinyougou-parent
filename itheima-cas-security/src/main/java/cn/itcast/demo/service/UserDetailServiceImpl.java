package cn.itcast.demo.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 项目名:pinyougou-parent
 * 包名: cn.itcast.demo.service
 * 作者: Yanglinlong
 * 日期: 2019/7/10 19:32
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("----------------------------username");
        //方法 之前我们是用来 从数据库查询用户的信息进行认证权限的控制（1.权限 2.认证）
        //现在和CAS集成，该方法就只做一件事：就是授权。认证交给CAS服务端
        return new User(username,"", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
