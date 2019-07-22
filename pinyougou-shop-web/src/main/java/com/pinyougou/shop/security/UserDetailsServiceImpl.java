package com.pinyougou.shop.security;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.shop.security
 * 作者: Yanglinlong
 * 日期: 2019/6/24 20:44
 */
public class UserDetailsServiceImpl implements UserDetailsService {

//    @Reference

    @Autowired
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TbSeller tbSeller = sellerService.findOne(username);
        if (tbSeller == null) {
            return null;
        }
        String status = tbSeller.getStatus();
        if (!"1".equals(status)) {
            return null;
        }
//        return new User(username,"{noop}123456" ,
//                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
//        return new User(username,"{noop}"+tbSeller.getPassword() ,
//                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
        return new User(username, tbSeller.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER"));
    }
}
