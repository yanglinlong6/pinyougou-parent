package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List; /**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service *
 * @since 1.0
 */
public interface CartService {
    /**
     * 向已有的购物车中添加商品 返回一个最新的购物车列表
     * @param cartList  已有的购物车列表
     * @param itemId 要添加的商品的ID
     * @param num 要购买的数量
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 存储购物车到Reids中
     * @param name  用户名
     * @param newestList 最新的购物车数据
     */
    void saveToRedis(String name, List<Cart> newestList);

    /**
     *
     * @param name 用户名
     * @return 该用户名对应的购物车数据
     */
    List<Cart> getCartListFromRedis(String name);

    /**
     *  返回一个最新的购物车数据
     * @param cookieCartList  cookie中的购物车数据
     * @param redisList  redis中的购物车数据
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisList);
}
