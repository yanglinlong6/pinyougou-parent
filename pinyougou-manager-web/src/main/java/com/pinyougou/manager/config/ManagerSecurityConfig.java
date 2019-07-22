package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.manager.config
 * 作者: Yanglinlong
 * 日期: 2019/6/24 17:32
 */
@EnableWebSecurity
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html","/login/getName.shtml")
                .permitAll().anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login.html")
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
        //会自动添加ROLE_
        auth.inMemoryAuthentication().withUser("admin")
                .password("{noop}admin")
                .roles("ADMIN");
    }
}
