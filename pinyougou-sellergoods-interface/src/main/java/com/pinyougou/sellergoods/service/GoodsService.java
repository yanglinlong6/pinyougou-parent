package com.pinyougou.sellergoods.service;

import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;
import entity.Goods;

/**
 * 服务层接口
 * 
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
    
    /**
     * 返回分页列表
     * 
     * @return
     */
    PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
    
    /**
     * 分页
     * 
     * @param pageNo 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);
    
    public void add(Goods goods);
    
    public Goods findOne(Long id);
    
    public void update(Goods goods);
    
    public void updateStatus(Long[] ids, String status);
    
    /**
     * 根据商品SPU的数组对象查询所有的该商品的列表数据
     * 
     * @param ids
     * @return
     */
    List<TbItem> findTbItemListByIds(Long[] ids);
    
    void setKill(String startTime, String endTime, String sellerId, Long[] ids);
}
