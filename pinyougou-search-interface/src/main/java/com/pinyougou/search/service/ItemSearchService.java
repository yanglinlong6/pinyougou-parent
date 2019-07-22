package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     * 更新数据到索引库中
     * @param items  就是数据
     */
    public void updateIndex(List<TbItem> items);

    /**
     *mana
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
