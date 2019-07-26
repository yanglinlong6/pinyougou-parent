package com.pinyougou.user.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbAreasMapper;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class AddressServiceImpl extends CoreServiceImpl<TbAddress> implements AddressService {


    private TbAddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(TbAddressMapper addressMapper) {
        super(addressMapper, TbAddress.class);
        this.addressMapper = addressMapper;
    }

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public PageInfo<TbAddress> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbAddress> all = addressMapper.selectAll();
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbAddress> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbAddress> findPage(Integer pageNo, Integer pageSize, TbAddress address) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();

        if (address != null) {
            if (StringUtils.isNotBlank(address.getUserId())) {
                criteria.andLike("userId", "%" + address.getUserId() + "%");
                //criteria.andUserIdLike("%"+address.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(address.getProvinceId())) {
                criteria.andLike("provinceId", "%" + address.getProvinceId() + "%");
                //criteria.andProvinceIdLike("%"+address.getProvinceId()+"%");
            }
            if (StringUtils.isNotBlank(address.getCityId())) {
                criteria.andLike("cityId", "%" + address.getCityId() + "%");
                //criteria.andCityIdLike("%"+address.getCityId()+"%");
            }
            if (StringUtils.isNotBlank(address.getTownId())) {
                criteria.andLike("townId", "%" + address.getTownId() + "%");
                //criteria.andTownIdLike("%"+address.getTownId()+"%");
            }
            if (StringUtils.isNotBlank(address.getMobile())) {
                criteria.andLike("mobile", "%" + address.getMobile() + "%");
                //criteria.andMobileLike("%"+address.getMobile()+"%");
            }
            if (StringUtils.isNotBlank(address.getAddress())) {
                criteria.andLike("address", "%" + address.getAddress() + "%");
                //criteria.andAddressLike("%"+address.getAddress()+"%");
            }
            if (StringUtils.isNotBlank(address.getContact())) {
                criteria.andLike("contact", "%" + address.getContact() + "%");
                //criteria.andContactLike("%"+address.getContact()+"%");
            }
            if (StringUtils.isNotBlank(address.getIsDefault())) {
                criteria.andLike("isDefault", "%" + address.getIsDefault() + "%");
                //criteria.andIsDefaultLike("%"+address.getIsDefault()+"%");
            }
            if (StringUtils.isNotBlank(address.getNotes())) {
                criteria.andLike("notes", "%" + address.getNotes() + "%");
                //criteria.andNotesLike("%"+address.getNotes()+"%");
            }
            if (StringUtils.isNotBlank(address.getAlias())) {
                criteria.andLike("alias", "%" + address.getAlias() + "%");
                //criteria.andAliasLike("%"+address.getAlias()+"%");
            }

        }
        List<TbAddress> all = addressMapper.selectByExample(example);
        PageInfo<TbAddress> info = new PageInfo<TbAddress>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbAddress> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    @Override
    public int insert(TbAddress record) {
        record.setAddress(record.getProvinceId()+record.getCityId()+record.getTownId()+record.getAddress());
        //将省市替换成Id
        String provinceId = (String) redisTemplate.boundHashOps("Province").get(record.getProvinceId());
        record.setProvinceId(provinceId);
        String cityId = (String) redisTemplate.boundHashOps("City").get(record.getCityId());
        record.setCityId(cityId);
        String townId = (String) redisTemplate.boundHashOps("Area").get(record.getTownId());
        record.setTownId(townId);
        //创建时间
        record.setCreateDate(new Date());
        return super.insert(record);
    }


    @Override
    public List<TbAddress> findByUserId(String userId) {
        Example example = new Example(TbAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        List<TbAddress> tbAddresses = addressMapper.selectByExample(example);

        return tbAddresses;
    }

    @Override
    public TbAddress selectByPrimaryKey(Object key) {
        TbAddress tbAddress = addressMapper.selectByPrimaryKey(key);
        //将ID替换成名称
        String province = (String) redisTemplate.boundHashOps("ProvinceNum").get(tbAddress.getProvinceId());
        tbAddress.setProvinceId(province);
        String cityId = (String) redisTemplate.boundHashOps("CityNum").get(tbAddress.getCityId());
        tbAddress.setCityId(cityId);
        String townId = (String) redisTemplate.boundHashOps("AreaNum").get(tbAddress.getTownId());
        tbAddress.setTownId(townId);

        return tbAddress;
    }
}
