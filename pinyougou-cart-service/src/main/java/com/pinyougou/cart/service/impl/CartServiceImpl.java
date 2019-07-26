package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service.impl *
 * @since 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据商品的ID 获取商品的对象数据
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

        //2.获取商品对象数据中所属的 商家的ID
        String sellerId = tbItem.getSellerId();

        Cart cart = searchCartBySellerId(cartList,sellerId);
        
        if(cart==null) {
            //3.判断 已有的购物中 是否 有商家ID  如果没有  直接添加商品
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());//店铺名
            List<TbOrderItem> orderitemList = new ArrayList<>();

            TbOrderItem tborderItem = new TbOrderItem();

            tborderItem.setItemId(itemId);
            tborderItem.setGoodsId(tbItem.getGoodsId());
            tborderItem.setTitle(tbItem.getTitle());
            tborderItem.setPrice(tbItem.getPrice());
            tborderItem.setNum(num);
            double v = tbItem.getPrice().doubleValue() * num;
            tborderItem.setTotalFee(new BigDecimal( v ));
            tborderItem.setPicPath(tbItem.getImage());
            tborderItem.setSellerId(sellerId);

            orderitemList.add(tborderItem);

            cart.setOrderItemList(orderitemList);

            cartList.add(cart);
        }else {
            //4.判断 已有的购物中 是否 有商家ID  如果有
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem tbOrderItemif = searchItemByItemId(orderItemList,itemId);

            if(tbOrderItemif!=null) {
                //4.1 判断 商家的购物明细中 是否有 要添加的商品 如果 有  数量相加

                tbOrderItemif.setNum(tbOrderItemif.getNum()+num);
                double v = tbOrderItemif.getPrice().doubleValue() * tbOrderItemif.getNum();
                tbOrderItemif.setTotalFee(new BigDecimal(v));
                //当商品的数量 为0 的时候 删除商品
                if(tbOrderItemif.getNum()==0){
                    orderItemList.remove(tbOrderItemif);
                }
                //当商家 里面没有商品的时候 删除商家 Cart
                if(orderItemList.size()==0){
                    cartList.remove(cart);
                }


            }else {
                //4.1 判断 商家的购物明细中 是否有 要添加的商品 如果 没有  直接添加
                tbOrderItemif= new TbOrderItem();
                tbOrderItemif.setItemId(itemId);
                tbOrderItemif.setGoodsId(tbItem.getGoodsId());
                tbOrderItemif.setTitle(tbItem.getTitle());
                tbOrderItemif.setPrice(tbItem.getPrice());
                tbOrderItemif.setNum(num);
                double v = tbItem.getPrice().doubleValue() * num;
                tbOrderItemif.setTotalFee(new BigDecimal( v ));
                tbOrderItemif.setPicPath(tbItem.getImage());
                tbOrderItemif.setSellerId(sellerId);

                orderItemList.add(tbOrderItemif);
            }
        }
        return cartList;
    }


    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void saveToRedis(String name, List<Cart> newestList) {
        // key  feild  value
        //     field  value

        redisTemplate.boundHashOps("CART_REDIS_KEY").put(name,newestList);
    }

    @Override
    public List<Cart> getCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART_REDIS_KEY").get(name);
        if(cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisList) {
        for (Cart cart : cookieCartList) {
            //向已有的购物车中添加商品
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                redisList =addGoodsToCartList(redisList,orderItem.getItemId(),orderItem.getNum());
            }
        }

        return redisList;
    }

    /**
     * 添加我的收藏
     * @param itemId
     */
    @Override
    public void addGoodsToCollectionList(Long itemId,String name) {

        //通过id查询商品
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        // 存入redis
        redisTemplate.boundHashOps("userCollect").put(itemId,tbItem);
    }

    /**
     * 查询
     * @return
     */
    @Override
    public List<TbItem> selectCollect() {
        return redisTemplate.boundHashOps("userCollect").values();
    }

    private TbOrderItem searchItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {

            if(orderItem.getItemId()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
