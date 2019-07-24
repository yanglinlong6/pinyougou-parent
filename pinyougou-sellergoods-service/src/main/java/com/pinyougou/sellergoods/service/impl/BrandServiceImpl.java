package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;  

import com.pinyougou.sellergoods.service.BrandService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class BrandServiceImpl extends CoreServiceImpl<TbBrand>  implements BrandService {

	
	private TbBrandMapper brandMapper;

	@Autowired
	public BrandServiceImpl(TbBrandMapper brandMapper) {
		super(brandMapper, TbBrand.class);
		this.brandMapper=brandMapper;
	}

	
	

	
	@Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbBrand> all = brandMapper.selectAll();
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand brand) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        if(brand!=null){			
						if(StringUtils.isNotBlank(brand.getName())){
				criteria.andLike("name","%"+brand.getName()+"%");
				//criteria.andNameLike("%"+brand.getName()+"%");
			}
			if(StringUtils.isNotBlank(brand.getFirstChar())){
				criteria.andLike("firstChar","%"+brand.getFirstChar()+"%");
				//criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
			}
	
		}
        List<TbBrand> all = brandMapper.selectByExample(example);
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    /**
     * 新增需求--运营商对品牌的审核
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbBrand record = new TbBrand();
        record.setStatus(status);
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        brandMapper.updateByExampleSelective(record, example);//update set status=1 where id in (12,3)
    }

    @Override
    public void updateStatus(Long[] ids) {
        TbBrand tbBrand = new TbBrand();
        tbBrand.setStatus("1");

        Example exmaple = new Example(TbBrand.class);
        Example.Criteria criteria = exmaple.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        brandMapper.updateByExampleSelective(tbBrand,exmaple);

    }

}
