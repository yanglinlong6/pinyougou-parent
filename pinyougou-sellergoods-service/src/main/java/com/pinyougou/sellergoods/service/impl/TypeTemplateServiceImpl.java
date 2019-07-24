package com.pinyougou.sellergoods.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbTypeTemplateMapper;

import com.pinyougou.sellergoods.service.TypeTemplateService;

import javax.swing.plaf.basic.BasicTreeUI;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate> implements TypeTemplateService {
    
    private TbTypeTemplateMapper typeTemplateMapper;
    
    @Autowired
    private TbSpecificationOptionMapper optionMapper;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
        super(typeTemplateMapper, TbTypeTemplate.class);
        this.typeTemplateMapper = typeTemplateMapper;
    }
    
    @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }
    
    @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNo, pageSize);
        
        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        
        if (typeTemplate != null) {
            if (StringUtils.isNotBlank(typeTemplate.getName())) {
                criteria.andLike("name", "%" + typeTemplate.getName() + "%");
                // criteria.andNameLike("%"+typeTemplate.getName()+"%");
            }
            if (StringUtils.isNotBlank(typeTemplate.getSpecIds())) {
                criteria.andLike("specIds", "%" + typeTemplate.getSpecIds() + "%");
                // criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
            }
            if (StringUtils.isNotBlank(typeTemplate.getBrandIds())) {
                criteria.andLike("brandIds", "%" + typeTemplate.getBrandIds() + "%");
                // criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
            }
            if (StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())) {
                criteria.andLike("customAttributeItems", "%" + typeTemplate.getCustomAttributeItems() + "%");
                // criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
            }
            
        }
        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        // 序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        
        // 获取模板数据
        List<TbTypeTemplate> typeTemplateList = this.findAll();
        for (TbTypeTemplate typeTemplate1 : typeTemplateList) {
            // 存储品牌列表
            List<Map> brandList = JSON.parseArray(typeTemplate1.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate1.getId(), brandList);
            // 存储规格列表
            List<Map> specList = findSpecList(typeTemplate1.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate1.getId(), specList);
        }
        
        return pageInfo;
    }
    
    @Override
    public List<Map> findSpecList(Long id) {
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        String specIds = tbTypeTemplate.getSpecIds();
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        for (Map map : maps) {
            Integer id1 = (Integer)map.get("id");// 规格的ID
            TbSpecificationOption record = new TbSpecificationOption();
            record.setSpecId(Long.valueOf(id1));
            List<TbSpecificationOption> optionsList = optionMapper.select(record);
            map.put("options", optionsList);
        }
        return maps;
    }

    @Override
    public void updateStatus(Long[] ids) {
        TbTypeTemplate template = new TbTypeTemplate();
        template.setStatus("1");

        Example exmaple = new Example(TbBrand.class);
        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        typeTemplateMapper.updateByExampleSelective(template,exmaple);

    }

    @Override
    public void insertAll(List<Map<String, String>> forExcel) {
        for (Map<String, String> template : forExcel) {
            TbTypeTemplate insert = new TbTypeTemplate();
            insert.setName(template.get("name"));
            insert.setSpecIds(getSpecIds(template.get("specIds")));
            insert.setBrandIds(getBrandIds(template.get("brandIds")));
            insert.setCustomAttributeItems(getCustomAttributeItems(template.get("customAttributeItems")));
            insert.setStatus("0");
            typeTemplateMapper.insert(insert);
        }
    }

    @Autowired
    private TbSpecificationMapper tbSpecificationMapper;

    private String getSpecIds(String specIds){
        StringBuilder builder = new StringBuilder();
        if (specIds.contains(",")){
            String[] split = specIds.split(",");
            Long[] ids = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                ids[i] = Long.valueOf(split[i]);
            }

            Example example = new Example(TbSpecification.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id",Arrays.asList(ids));
            List<TbSpecification> tbSpecifications = tbSpecificationMapper.selectByExample(example);


            builder.append("[");
            for (TbSpecification tbSpecification : tbSpecifications) {
                builder.append("{").
                        append("\"id\"").
                        append(":").
                        append(tbSpecification.getId()).
                        append(",").
                        append("\"text\"").
                        append(":").
                        append("\"").
                        append(tbSpecification.getSpecName()).
                        append("\"").
                        append("}").
                        append(",");
            }
            builder.replace(builder.length()-1, builder.length(), "]");
        }else{
            TbSpecification tbSpecification = tbSpecificationMapper.selectByPrimaryKey(Long.valueOf(specIds));
            builder.append("[");
            builder.append("{").
                    append("\"id\"").
                    append(":").
                    append(tbSpecification.getId()).
                    append(",").
                    append("\"text\"").
                    append(":").
                    append("\"").
                    append(tbSpecification.getSpecName()).
                    append("\"").
                    append("}").
                    append("]");
        }
        return builder.toString();
    }

    @Autowired
    private TbBrandMapper tbBrandMapper;

    private String getBrandIds(String brandIds){
        StringBuilder builder = new StringBuilder();
        if (brandIds.contains(",")){
            String[] split = brandIds.split(",");
            Long[] ids = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                ids[i] = Long.valueOf(split[i]);
            }

            Example example = new Example(TbBrand.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id",Arrays.asList(ids));
            List<TbBrand> tbBrands = tbBrandMapper.selectByExample(example);

            builder.append("[");
            for (TbBrand tbBrand : tbBrands) {
                builder.append("{").
                        append("\"id\"").
                        append(":").
                        append(tbBrand.getId()).
                        append(",").
                        append("\"text\"").
                        append(":").
                        append("\"").
                        append(tbBrand.getName()).
                        append("\"").
                        append("}").
                        append(",");
            }
            builder.replace(builder.length()-1, builder.length(), "]");
        }else{
            TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(Long.valueOf(brandIds));
            builder.append("[");
            builder.append("{").
                    append("\"id\"").
                    append(":").
                    append(tbBrand.getId()).
                    append(",").
                    append("\"text\"").
                    append(":").
                    append("\"").
                    append(tbBrand.getName()).
                    append("\"").
                    append("}").
                    append("]");
        }
        return builder.toString();
    }

    private String getCustomAttributeItems(String customAttributeItems){
        StringBuilder builder = new StringBuilder();
        if (customAttributeItems.contains(",")){
            String[] split = customAttributeItems.split(",");
            builder.append("[");
            for (String str : split) {
                builder.append("{").
                        append("\"text\"").
                        append(":").
                        append("\"").
                        append(str).
                        append("\"").
                        append("}").
                        append(",");
            }
            builder.replace(builder.length()-1, builder.length(), "]");
        }else{
            builder.append("[");
            builder.append("{").
                    append("\"text\"").
                    append(":").
                    append("\"").
                    append(customAttributeItems).
                    append("\"").
                    append("}").
                    append("]");
        }
        return builder.toString();
    }

}
