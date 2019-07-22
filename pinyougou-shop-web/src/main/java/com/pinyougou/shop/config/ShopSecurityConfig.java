package com.pinyougou.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.shop.config
 * 作者: Yanglinlong
 * 日期: 2019/6/24 20:38
 */
@EnableWebSecurity
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/*.html","/seller/add.shtml")
                .permitAll().anyRequest().authenticated();
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login?error");
        http.csrf().disable();
        //开启同源iframe 可以访问策略
        http.headers().frameOptions().sameOrigin();
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
//        auth.userDetailsService(userDetailsService);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
