package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.SysConstants;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.service.ContentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl extends CoreServiceImpl<TbContent> implements ContentService {
    
    private TbContentMapper contentMapper;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private TbItemCatMapper itemCatMapper;
    
    /**
     * Instantiates a new Content service.
     *
     * @param contentMapper the content mapper
     */
    @Autowired
    public ContentServiceImpl(TbContentMapper contentMapper) {
        super(contentMapper, TbContent.class);
        this.contentMapper = contentMapper;
    }
    
    @Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbContent> all = contentMapper.selectAll();
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);
        
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }
    
    @Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent content) {
        PageHelper.startPage(pageNo, pageSize);
        
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        
        if (content != null) {
            if (StringUtils.isNotBlank(content.getTitle())) {
                criteria.andLike("title", "%" + content.getTitle() + "%");
                // criteria.andTitleLike("%"+content.getTitle()+"%");
            }
            if (StringUtils.isNotBlank(content.getUrl())) {
                criteria.andLike("url", "%" + content.getUrl() + "%");
                // criteria.andUrlLike("%"+content.getUrl()+"%");
            }
            if (StringUtils.isNotBlank(content.getPic())) {
                criteria.andLike("pic", "%" + content.getPic() + "%");
                // criteria.andPicLike("%"+content.getPic()+"%");
            }
            if (StringUtils.isNotBlank(content.getContent())) {
                criteria.andLike("content", "%" + content.getContent() + "%");
                // criteria.andContentLike("%"+content.getContent()+"%");
            }
            if (StringUtils.isNotBlank(content.getStatus())) {
                criteria.andLike("status", "%" + content.getStatus() + "%");
                // criteria.andStatusLike("%"+content.getStatus()+"%");
            }
            
        }
        List<TbContent> all = contentMapper.selectByExample(example);
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);
        
        return pageInfo;
    }
    
    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {
        List<TbContent> contentsFromRedis =
            (List<TbContent>)redisTemplate.boundHashOps(SysConstants.CONTENT_REDIS_KEY).get(categoryId);
        if (contentsFromRedis != null && contentsFromRedis.size() > 0) {
            return contentsFromRedis;
        }
        TbContent record = new TbContent();
        record.setCategoryId(categoryId);
        record.setStatus("1");// 正常的
        List<TbContent> contents = contentMapper.select(record);
        System.out.println("contents:" + contents);
        redisTemplate.boundHashOps(SysConstants.CONTENT_REDIS_KEY).put(categoryId, contents);
        return contents;
    }
    
    @Override
    public List<TbItemCat> findByItemCat3(Long parentId) {
        // 1.先从redis缓存中 , 获取三级分类信息!
        List<TbItemCat> itemCat01List = (List<TbItemCat>)redisTemplate.boundValueOps("itemCat03").get();
        TbItemCat cat = new TbItemCat();
        cat.setParentId(parentId);
        // 2.若缓存中没有数据 , 从数据库中查询( 并放到缓存中 )
        if (itemCat01List == null) {
            // 缓存穿透 -> 请求排队等候.
            synchronized (this) {
                // 进行二次校验?
                itemCat01List = (List<TbItemCat>)redisTemplate.boundValueOps("itemCat03").get();
                if (itemCat01List == null) {
                    // 创建一个集合 , 存放一级分类
                    itemCat01List = new ArrayList<>();
                    // 根据parent_id = 0 , 获取一级分类信息!
                    List<TbItemCat> itemCatList = itemCatMapper.selectByParentId(parentId);
                    for (TbItemCat itemCat : itemCatList) {
                        // // 设置一级分类信息!
                        TbItemCat itemCat01 = new TbItemCat();
                        itemCat01.setId(itemCat.getId());
                        itemCat01.setName(itemCat.getName());
                        itemCat01.setParentId(itemCat.getParentId());
                        
                        // 根据一级分类的id -> 找到对应的二级分类!
                        List<TbItemCat> itemCatList02 = new ArrayList<>();
                        TbItemCat tbItemCat02 = new TbItemCat();
                        tbItemCat02.setParentId(itemCat.getId());
                        List<TbItemCat> itemCat02List = itemCatMapper.select(tbItemCat02);
                        // System.out.println(itemCat02List);
                        for (TbItemCat itemCat2 : itemCat02List) {
                            // 设置二级分类信息!
                            TbItemCat itemCat02 = new TbItemCat();
                            itemCat02.setId(itemCat2.getId());
                            itemCat02.setName(itemCat2.getName());
                            itemCat02.setParentId(itemCat2.getParentId());
                            // 根据二级分类的id -> 找到对应的三级分类!
                            List<TbItemCat> itemCatList03 = new ArrayList<>();
                            TbItemCat tbItemCat03 = new TbItemCat();
                            tbItemCat03.setParentId(itemCat2.getId());
                            List<TbItemCat> itemCat03List = itemCatMapper.select(tbItemCat03);
                            for (TbItemCat itemCat3 : itemCat03List) {
                                itemCatList03.add(itemCat3);
                            }
                            itemCat02.setItemCatList(itemCatList03);
                            itemCatList02.add(itemCat02);
                            // System.out.println(itemCat02List);
                            
                        }
                        itemCat01.setItemCatList(itemCatList02);
                        itemCat01List.add(itemCat01); // 添加一级分类
                    }
                    // 将查询到的数据放入缓存中!
                    redisTemplate.boundValueOps("itemCat03").set(itemCat01List);
                    System.out.println(itemCat01List);
                    return itemCat01List;
                }
            }
            
        }
        
        // 3.若缓存中有数据 , 直接返回即可!
        System.out.println(itemCat01List);
        return itemCat01List;
    }
}
