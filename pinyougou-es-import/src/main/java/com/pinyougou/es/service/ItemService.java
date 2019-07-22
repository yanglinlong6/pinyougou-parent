package com.pinyougou.es.service;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.es.service
 * 作者: Yanglinlong
 * 日期: 2019/6/29 20:25
 */
public interface ItemService {
    /**
     * 从数据库中获取数据 导入到ES的索引库
     */
    public void ImportDataToEs();
}
