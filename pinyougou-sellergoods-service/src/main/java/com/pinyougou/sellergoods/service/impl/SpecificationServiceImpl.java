package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.List;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import entity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSpecificationMapper;

import com.pinyougou.sellergoods.service.SpecificationService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl extends CoreServiceImpl<TbSpecification> implements SpecificationService {


    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    @Autowired
    public SpecificationServiceImpl(TbSpecificationMapper specificationMapper) {
        super(specificationMapper, TbSpecification.class);
        this.specificationMapper = specificationMapper;
    }


    @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSpecification> all = specificationMapper.selectAll();
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (StringUtils.isNotBlank(specification.getSpecName())) {
                criteria.andLike("specName", "%" + specification.getSpecName() + "%");
                //criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }

        }
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    /**
     * 增加
     */
    @Override
    public void add(Specification specification){
        TbSpecification specification1 = specification.getSpecification();

        specificationMapper.insert(specification1);
        List<TbSpecificationOption> optionList = specification.getOptionList();

        for (TbSpecificationOption tbSpecificationOption : optionList) {
            tbSpecificationOption.setSpecId(specification1.getId());
            optionMapper.insert(tbSpecificationOption);
        }
    }
    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(tbSpecification.getId());
        List<TbSpecificationOption> options = optionMapper.select(option);
        specification.setSpecification(tbSpecification);
        specification.setOptionList(options);
        return specification;
    }

    @Override
    public void delete(Long[] ids) {
        //删除规格
        Example example = new Example(TbSpecification.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        specificationMapper.deleteByExample(example);
        //删除规格关联的规格选项
        Example exampleOption = new Example(TbSpecificationOption.class);
        exampleOption.createCriteria().andIn("specId", Arrays.asList(ids));
        optionMapper.deleteByExample(exampleOption);
    }

    @Override
    public void update(Specification specification){
       specificationMapper.updateByPrimaryKey(specification.getSpecification());
        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(specification.getSpecification().getId());
        int delete = optionMapper.delete(option);
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption tbSpecificationOption : optionList) {
            tbSpecificationOption.setSpecId(specification.getSpecification().getId());
            optionMapper.insert(tbSpecificationOption);
        }
    }

    @Override
    public void updateStatus(Long[] ids) {
        TbSpecification tbSpecification = new TbSpecification();
        tbSpecification.setStatus("1");


        Example exmaple = new Example(TbBrand.class);
        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        specificationMapper.updateByExampleSelective(tbSpecification,exmaple);

    }
}
