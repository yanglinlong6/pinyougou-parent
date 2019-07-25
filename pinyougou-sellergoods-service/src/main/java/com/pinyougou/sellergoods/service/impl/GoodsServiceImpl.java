package com.pinyougou.sellergoods.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.SeckillGoodsService;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {
    
    private TbGoodsMapper goodsMapper;
    
    @Autowired
    private TbItemMapper itemMapper;
    
    @Autowired
    private TbBrandMapper brandMapper;
    
    @Autowired
    private TbItemCatMapper itemCatMapper;
    
    @Autowired
    private TbSellerMapper sellerMapper;
    
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    
    @Autowired
    private SeckillGoodsService seckillGoodsService;
    
    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }
    
    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }
    
    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);
        
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        
        criteria.andEqualTo("isDelete", false);// 只查询没有被删除的
        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                // criteria.andLike("sellerId","%"+goods.getSellerId()+"%");
                criteria.andEqualTo("sellerId", goods.getSellerId());
                // criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
                // criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andLike("auditStatus", "%" + goods.getAuditStatus() + "%");
                // criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsMarketable())) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
                // criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
            }
            if (StringUtils.isNotBlank(goods.getCaption())) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
                // criteria.andCaptionLike("%"+goods.getCaption()+"%");
            }
            if (StringUtils.isNotBlank(goods.getSmallPic())) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
                // criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsEnableSpec())) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
                // criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
            }
            
        }
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        
        return pageInfo;
    }
    
    @Override
    public void add(Goods goods) {
        // 1.获取goods
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");
        tbGoods.setIsDelete(false);//
        goodsMapper.insert(tbGoods);
        // 2.获取goodsdesc
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(goodsDesc);
        
        saveItems(goods, tbGoods, goodsDesc);
    }
    
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        TbItem item = new TbItem();
        item.setGoodsId(id);
        List<TbItem> tbItemList = itemMapper.select(item);
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(tbItemList);
        return goods;
    }
    
    @Override
    public void update(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        TbItem item = new TbItem();
        item.setGoodsId(tbGoods.getId());
        itemMapper.delete(item);
        // 新增就可以了 这里也要判断是否为启用的状态
        saveItems(goods, tbGoods, goods.getGoodsDesc());
    }
    
    private void saveItems(Goods goods, TbGoods goods1, TbGoodsDesc goodsDesc) {
        if ("1".equals(goods1.getIsEnableSpec())) {
            
            // TODO
            // 先获取SKU的列表
            List<TbItem> itemList = goods.getItemList();
            
            for (TbItem tbItem : itemList) {
                // 设置title SPU名 + 空格+ 规格名称 +
                String spec = tbItem.getSpec();// {"网络":"移动4G","机身内存":"16G"}
                String title = goods1.getGoodsName();
                Map map = JSON.parseObject(spec, Map.class);
                for (Object key : map.keySet()) {
                    String o1 = (String)map.get(key);
                    title += " " + o1;
                }
                tbItem.setTitle(title);
                
                // 设置图片从goodsDesc中获取
                // [{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
                String itemImages = goodsDesc.getItemImages();//
                
                List<Map> maps = JSON.parseArray(itemImages, Map.class);
                
                String url = maps.get(0).get("url").toString();// 图片的地址
                tbItem.setImage(url);
                
                // 设置分类
                TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
                tbItem.setCategoryid(tbItemCat.getId());
                tbItem.setCategory(tbItemCat.getName());
                
                // 时间
                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(new Date());
                
                // 设置spu的id
                tbItem.setGoodsId(goods1.getId());
                
                // 设置商家
                TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods1.getSellerId());
                tbItem.setSellerId(tbSeller.getSellerId());
                tbItem.setSeller(tbSeller.getNickName());// 店铺名
                
                // 设置品牌明后
                TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods1.getBrandId());
                tbItem.setBrand(tbBrand.getName());
                itemMapper.insert(tbItem);
            }
        }
        else {
            // 插入到SKU表 一条记录
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods1.getGoodsName());
            tbItem.setPrice(goods1.getPrice());
            tbItem.setNum(999);// 默认一个
            tbItem.setStatus("1");// 正常启用
            tbItem.setIsDefault("1");// 默认的
            
            tbItem.setSpec("{}");
            
            // 设置图片从goodsDesc中获取
            // [{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
            String itemImages = goodsDesc.getItemImages();//
            
            List<Map> maps = JSON.parseArray(itemImages, Map.class);
            
            String url = maps.get(0).get("url").toString();// 图片的地址
            tbItem.setImage(url);
            
            // 设置分类
            TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods1.getCategory3Id());
            tbItem.setCategoryid(tbItemCat.getId());
            tbItem.setCategory(tbItemCat.getName());
            
            // 时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(new Date());
            
            // 设置SPU的ID
            tbItem.setGoodsId(goods1.getId());
            
            // 设置商家
            TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods1.getSellerId());
            tbItem.setSellerId(tbSeller.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());// 店铺名
            
            // 设置品牌明后
            TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods1.getBrandId());
            tbItem.setBrand(tbBrand.getName());
            itemMapper.insert(tbItem);
        }
    }
    
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods record = new TbGoods();
        record.setAuditStatus(status);
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(record, example);// update set status=1 where id in (12,3)
    }
    
    @Override
    public List<TbItem> findTbItemListByIds(Long[] ids) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andIn("goodsId", Arrays.asList(ids)).andEqualTo("status", "1");
        return itemMapper.selectByExample(example);
    }
    
    @Override
    public void setKill(String startTime, String endTime, String sellerId, Long[] ids) {
        for (Long id : ids) {
            System.out.println(id);
            TbSeckillGoods seckillGoods = new TbSeckillGoods();
            seckillGoods.setGoodsId(id);
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            Long itemId = tbGoods.getDefaultItemId();
            seckillGoods.setItemId(itemId);
            TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
            seckillGoods.setTitle(tbGoods.getGoodsName());
            // seckillGoods.setTitle(tbItem.getTitle());
            seckillGoods.setSmallPic(tbGoods.getSmallPic());
            seckillGoods.setPrice(tbGoods.getPrice());
            seckillGoods.setCostPrice(tbItem.getCostPirce());
            seckillGoods.setSellerId(sellerId);
            seckillGoods.setCreateTime(new Date());
            seckillGoods.setCheckTime(new Date());
            seckillGoods.setStatus("0");
            
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date starttime = null;
            Date endtime = null;
            try {
                starttime = df.parse(startTime);
                endtime = df.parse(endTime);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(starttime);
            System.out.println(endtime);
            seckillGoods.setStartTime(starttime);
            seckillGoods.setEndTime(endtime);
            seckillGoods.setNum(tbItem.getNum());
            seckillGoods.setStockCount(tbItem.getStockCount());
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
            seckillGoods.setIntroduction(goodsDesc.getIntroduction());
            seckillGoodsService.insert(seckillGoods);
        }
    }
    
    @Override
    public void delete(Object[] ids) {
        // update tb_goods set is_delete=1 where id in (1,2,3)
        
        Example exmaple = new Example(TbGoods.class);
        
        Long[] issss = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            issss[i] = (Long)ids[i];
        }
        exmaple.createCriteria().andIn("id", Arrays.asList(issss));
        
        TbGoods tbgoods = new TbGoods();
        tbgoods.setIsDelete(true);
        goodsMapper.updateByExampleSelective(tbgoods, exmaple);
        
    }
}
