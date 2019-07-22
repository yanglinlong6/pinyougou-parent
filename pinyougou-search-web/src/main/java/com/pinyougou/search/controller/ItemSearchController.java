package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.search
 * 作者: Yanglinlong
 * 日期: 2019/6/27 23:16
 */
@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {


    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map<String,Object> searchMap){
        if(searchMap==null){
            searchMap=new HashMap<>();
        }
        return itemSearchService.search(searchMap);
    }
}