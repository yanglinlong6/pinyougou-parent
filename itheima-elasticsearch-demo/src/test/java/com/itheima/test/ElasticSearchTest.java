package com.itheima.test;

import com.itheima.es.dao.ItemDao;
import com.itheima.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名:pinyougou-parent
 * 包名: com.itheima.test
 * 作者: Yanglinlong
 * 日期: 2019/6/29 20:33
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemDao itemDao;

    /**
     * 创建索引和映射
     */
    @Test
    public void testCreateIndexAndMapping() {
        //创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
        //创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    @Test
    public void saveData() {
        TbItem tbitem = new TbItem();
        tbitem.setId(20000L);
        tbitem.setTitle("测试商品");
        itemDao.save(tbitem);
    }

    @Test
    public void deleteById() {
        itemDao.deleteById(20000L);
    }

    @Test
    public void update() {
        TbItem tbitem = new TbItem();
        tbitem.setId(30000L);
        tbitem.setTitle("测试商品111");
        tbitem.setCategory("商品分类111");
        tbitem.setBrand("三星");
        tbitem.setSeller("三星旗舰店");

        itemDao.save(tbitem);
    }

    @Test
    public void QueryAll() {
        Iterable<TbItem> all = itemDao.findAll();
        for (TbItem tbItem : all) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void QueryById() {
        System.out.println(itemDao.findById(30000L).get().getTitle());
    }

    //分页查询 所有数据
    @Test
    public void queryByPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TbItem> all = itemDao.findAll(pageable);
        for (TbItem tbItem : all) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void queryByWialdQuery() {

        SearchQuery query = new NativeSearchQuery(QueryBuilders.wildcardQuery("title", "商?"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);


        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }

    }

    @Test
    public void queryByMatchQuery() {
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("title", "商品111"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);


        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void queryByCopyTo() {
        //select * from xx where keyword="三星"   <====> select * from xx where seller like '三星' OR category like '三星' OR ....
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("keyword", "三星"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数：" + totalElements);
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void saveDatato() {
        TbItem tbitem = new TbItem();
        tbitem.setId(30000L);
        tbitem.setTitle("测试商品");
        tbitem.setCategory("商品分类1");
        tbitem.setBrand("三星");
        tbitem.setSeller("三星旗舰店");
        Map<String, String> map = new HashMap<>();
        map.put("网络制式", "移动4G");
        map.put("机身内存", "G");
        tbitem.setSpecMap(map);
        itemDao.save(tbitem);
    }

    @Test
    public void queryobject() {
        NativeSearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("specMap.网络制式.keyword", "移动4G"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录:" + totalElements);
        List<TbItem> content = tbItems.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle() + ":" + tbItem.getSpecMap());
        }
    }

    @Test
    public void test() {
        //1.创建查询对象的构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //2.创建 查询条件

        queryBuilder.withIndices("pinyougou");//设置从哪一个索引查询
        queryBuilder.withTypes("item");//设置从哪一个类型中查询

        queryBuilder.withQuery(QueryBuilders.matchQuery("title","商品"));//从title 中查询内容为商品的数据

        //3.创建 过滤查询（规格的过滤查询 多个过滤使用bool查询 ）

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword","移动4G"));

        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.机身内存.keyword","16G"));

        queryBuilder.withFilter(boolQueryBuilder);

        //4.构建 查询条件
        NativeSearchQuery searchQuery = queryBuilder.build();
        //5.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        //6.获取结果集
        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数："+totalElements);
        List<TbItem> content = tbItems.getContent();

        for (TbItem tbItem : content) {
            //7.打印
            System.out.println(tbItem.getTitle()+":>>>"+tbItem.getSpecMap());
        }
    }

}