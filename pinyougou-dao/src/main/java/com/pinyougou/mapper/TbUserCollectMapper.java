package com.pinyougou.mapper;

import com.pinyougou.pojo.TbUserCollect;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TbUserCollectMapper extends Mapper<TbUserCollect> {
    TbUserCollect selectByUserAndItem(TbUserCollect tbUserCollect);
}
