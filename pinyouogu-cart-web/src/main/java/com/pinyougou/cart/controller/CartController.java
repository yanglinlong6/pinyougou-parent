package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
public class CartController {

    @Reference
    private CartService cartService;

    /**
     * @param itemId 要添加的商品SKU的ID
     * @param num    购买的数量
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {

        try {
            //1.获取用户名 anonymousUser springsecurity 内置的角色的用户名 表示匿名用户
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
//            System.out.println(name);
            if ("anonymousUser".equals(name)) {
                //2.判断用户是否登录 如果没登录 操作cookie
                //2.1 从cookie中获取已有的购物车列表数据 List<Cart>
                String cartListstring = CookieUtil.getCookieValue(request, "cartList", true);
                if (StringUtils.isEmpty(cartListstring)) {
                    cartListstring = "[]";
                }
                List<Cart> cartList = JSON.parseArray(cartListstring, Cart.class);

                //2.2 向已有的购物车列表中 添加 商品   返回一个最新的购物车列表                 写一个方法 (向已有的购物车中添加商品:)
                List<Cart> newestList = cartService.addGoodsToCartList(cartList, itemId, num);

                String jsonString = JSON.toJSONString(newestList);
                //2.3 将最新的购物车数据设置回cookie中.
                CookieUtil.setCookie(request, response, "cartList", jsonString, 7 * 24 * 3600, true);

            } else {
                //3.如果登录 操作的redis
                //3.1 从redis中获取已有的购物车列表数据
                List<Cart> redisList = cartService.getCartListFromRedis(name);

                //3.2 向已有购物车列表中添加 商品 返回一个最新的购物车的列表
                List<Cart> newestList = cartService.addGoodsToCartList(redisList, itemId, num);
                //3.3 将最新的购物车数据 存储回redis中
                cartService.saveToRedis(name, newestList);

            }
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }

    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        //1.获取用户名 anonymousUser springsecurity 内置的角色的用户名 表示匿名用户
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //2 判断
        if ("anonymousUser".equals(name)) {
            //展示cookie中的购物车的数据
            String cartListstring = CookieUtil.getCookieValue(request, "cartList", true);
            if (StringUtils.isEmpty(cartListstring)) {
                cartListstring = "[]";
            }
            List<Cart> cartList = JSON.parseArray(cartListstring, Cart.class);
            return cartList;
        } else {
            //展示redis中的购物车的数据
            List<Cart> redisList = cartService.getCartListFromRedis(name);


            //合并购物车的数据

            //1.获取cookie中的购物车的数据
            String cartListstring = CookieUtil.getCookieValue(request, "cartList", true);
            if (StringUtils.isEmpty(cartListstring)) {
                cartListstring = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListstring, Cart.class);

            //2.获取redis中的购物车的数据
            if (redisList == null) {
                redisList = new ArrayList<>();
            }


            //3.合并(业务) 之后 返回一个最新的购物车的数据

            List<Cart> newestList = cartService.mergeCartList(cookieCartList, redisList);
            //4.将最新的数据重新的设置回redis中
            cartService.saveToRedis(name, newestList);

            //5.cookie中的购物车清除
            CookieUtil.deleteCookie(request, response, "cartList");

            return newestList;
        }


    }
}
