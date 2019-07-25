package com.pinyougou.sellergoods.service.impl;

import java.util.*;

import com.pinyougou.pojo.TbBrand;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;

import com.pinyougou.sellergoods.service.ItemCatService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat> implements ItemCatService {
    
    private TbItemCatMapper itemCatMapper;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
        super(itemCatMapper, TbItemCat.class);
        this.itemCatMapper = itemCatMapper;
    }
    
    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbItemCat> all = itemCatMapper.selectAll();
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }
    
    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo, pageSize);
        
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        
        if (itemCat != null) {
            if (StringUtils.isNotBlank(itemCat.getName())) {
                criteria.andLike("name", "%" + itemCat.getName() + "%");
                // criteria.andNameLike("%"+itemCat.getName()+"%");
            }
            
        }
        List<TbItemCat> all = itemCatMapper.selectByExample(example);
        for (TbItemCat tbItemCat : all) {
            System.out.println(tbItemCat.getStatus());
        }
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        
        return pageInfo;
    }
    
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCat cat = new TbItemCat();
        cat.setParentId(parentId);
        List<TbItemCat> itemCats = itemCatMapper.select(cat);
        // 每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
        List<TbItemCat> list = findAll();
        for (TbItemCat itemCat : list) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
        }
        return itemCats;
    }
    
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbItemCat itemCat = new TbItemCat();
        itemCat.setStatus(status);
        Example example = new Example(TbItemCat.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        itemCatMapper.updateByExampleSelective(itemCat, example);
    }

    @Override
    public void updateStatus(Long[] ids) {
        TbItemCat cat = new TbItemCat();
        cat.setStatus("1");

        Example exmaple = new Example(TbBrand.class);
        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        itemCatMapper.updateByExampleSelective(cat,exmaple);

    }




    @Override
    public void insertAll(List<TbItemCat> itemCats) {
        for (TbItemCat itemCat : itemCats) {
            itemCat.setStatus("0");
            itemCatMapper.insert(itemCat);
        }
    }

    @Override
    public Map findByItemCat3(Long parentId01) {
        // 1.先从redis缓存中 , 获取三级分类信息!
        List<TbItemCat> itemCat01List  = (List<TbItemCat>) redisTemplate.boundValueOps("itemCat03").get();
        TbItemCat cat = new TbItemCat();
        cat.setParentId(parentId01);
        Map map = new HashMap();
        // 2.若缓存中没有数据 , 从数据库中查询( 并放到缓存中 )
        if (itemCat01List==null){
            // 缓存穿透 -> 请求排队等候.
            synchronized (this){
                // 进行二次校验?
                itemCat01List  = (List<TbItemCat>) redisTemplate.boundValueOps("itemCat03").get();
                if (itemCat01List==null){
                    // 创建一个集合 , 存放一级分类
                    itemCat01List = new ArrayList<>();

                    // 根据parent_id = 0 , 获取一级分类信息!

                    List<TbItemCat> itemCatList = itemCatMapper.select(cat);
                    map.put("list0",itemCatList);
                    for (TbItemCat itemCat : itemCatList) {

                        // 根据一级分类的id -> 找到对应的二级分类!
                        TbItemCat tbItemCat02 = new TbItemCat();
                        tbItemCat02.setParentId(itemCat.getId());
                        List<TbItemCat> itemCat02List = itemCatMapper.select(tbItemCat02);
                        map.put("list1",tbItemCat02);
                        System.out.println(itemCat02List);
                        for (TbItemCat itemCat2 : itemCat02List) {

                            // 根据二级分类的id -> 找到对应的三级分类!
                            TbItemCat tbItemCat03 = new TbItemCat();
                            tbItemCat03.setParentId(itemCat2.getId());
                            List<TbItemCat> itemCat03 = itemCatMapper.select(tbItemCat03);
                            map.put("list2",itemCat03);
                            System.out.println(itemCat02List);

                        }
                    }
                    // 将查询到的数据放入缓存中!
                    redisTemplate.boundValueOps("itemCat03").set(map);
                    return map;
                }
            }

        }

        // 3.若缓存中有数据 , 直接返回即可!
        return map;

    }

}
