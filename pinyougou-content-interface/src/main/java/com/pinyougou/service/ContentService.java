package com.pinyougou.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbItemCat;

import java.util.List;
import java.util.Map;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface ContentService extends CoreService<TbContent> {
    
    /**
     * 返回分页列表
     * 
     * @return
     */
    PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize);
    
    /**
     * 分页
     * 
     * @param pageNo 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent Content);
    
    List<TbContent> findByCategoryId(Long categoryId);
    
    List<TbItemCat> findByItemCat3(Long parentId);
}
