package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    
    @Reference
    private GoodsService goodsService;
    
    /**
     * 返回全部列表
     * 
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }
    
    @RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
    
    /**
     * 增加
     * 
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(name);
            goodsService.add(goods);
            return new Result(true, "增加成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }
    
    /**
     * 修改
     * 
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }
    
    /**
     * 获取实体
     * 
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public Goods findOne(@PathVariable(value = "id") Long id) {
        return goodsService.findOne(id);
    }
    
    /**
     * ` 批量删除
     * 
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    
    @RequestMapping("/search")
    public PageInfo<TbGoods> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
        @RequestBody TbGoods goods) {
        goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
        return goodsService.findPage(pageNo, pageSize, goods);
    }
    
    @RequestMapping("/setKill")
    public Result setKill(@RequestBody Long[] ids,
        @RequestParam(value = "startTime", defaultValue = "2019-02-03", required = true) String startTime,
        @RequestParam(value = "endTime", defaultValue = "2019-02-04", required = true) String endTime) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        try {
            System.out.println(startTime);
            System.out.println(endTime);
            goodsService.setKill(startTime, endTime, sellerId, ids);
            return new Result(true, "设置成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "设置失败");
        }
    }
}
