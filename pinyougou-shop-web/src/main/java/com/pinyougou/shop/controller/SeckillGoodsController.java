package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.sellergoods.service.SeckillGoodsService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目名:pinyougou-parent 包名: com.pinyougou.shop.controller 作者: Yanglinlong 日期: 2019/7/23 17:37
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Reference
    private SeckillGoodsService seckillGoodsService;
    
    @RequestMapping("/search")
    public PageInfo<TbSeckillGoods> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
        @RequestBody TbSeckillGoods seckillGoods) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(sellerId);
        return seckillGoodsService.findPage(pageNo, pageSize, seckillGoods);
    }
    
    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            seckillGoodsService.delete(ids);
            return new Result(true, "删除成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    
    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbSeckillGoods findOne(@PathVariable(value = "id") Long id) {
        return seckillGoodsService.findOne(id);
    }
    
    /**
     * 修改
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
            return new Result(true, "修改成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }
    
    /**
     * 增加
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.add(seckillGoods);
            return new Result(true, "增加成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }
    
    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsService.findAll();
    }
    
    @RequestMapping("/findPage")
    public PageInfo<TbSeckillGoods> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return seckillGoodsService.findPage(pageNo, pageSize);
    }
    
}
