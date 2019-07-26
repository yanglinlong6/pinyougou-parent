package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.search.service.ItemSearchService;
import entity.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名:pinyougou-parent 包名: com.pinyougou.search 作者: Yanglinlong 日期: 2019/6/27 23:16
 */
@RestController
@RequestMapping("/itemSearch")
@CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
public class ItemSearchController {
    
    @Reference
    private ItemSearchService itemSearchService;
    
    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String, Object> searchMap) {
        if (searchMap == null) {
            searchMap = new HashMap<>();
        }
        return itemSearchService.search(searchMap);
    }
    
    // @RequestMapping("/tiaoZhaun")
    // public TbGoods tiaoZhaun(Long id){
    // return itemSearchService.tiaoZhaun(id);
    //
    // }
    
    @RequestMapping("/addFootMark")
    public Result addFootMark(@RequestParam(value = "id") Long id) {
        System.out.println(id);
        try {
            itemSearchService.addFootMark(id);
            return new Result(true, "添加收藏成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加收藏失败");
        }
    }
}