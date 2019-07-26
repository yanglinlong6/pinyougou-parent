package com.pinyougou.mapper;

import com.pinyougou.pojo.TbItemCat;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TbItemCatMapper extends Mapper<TbItemCat> {
    // 根据父类id - 0 , 查询分类信息

    List<TbItemCat> selectByParentId(Long parentId01);
}