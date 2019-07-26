package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名:pinyougou-parent 包名: com.pinyougou.search.service.impl 作者: Yanglinlong 日期: 2019/6/27 23:09
 *
 * @author 59276
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemDao dao;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        // 1.获取关键字
        String keywords = (String)searchMap.get("keywords");

        // 创建查询条件
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        // builder.withIndices("pinyougou");
        // builder.withTypes("item");
        // builder.withQuery(QueryBuilders.matchQuery("keyword", keywords));
        if (StringUtils.isNotBlank(keywords) && !keywords.equals("undefined")) {
            builder.withQuery(QueryBuilders.multiMatchQuery(keywords, "title", "seller", "category", "brand"));
            // 设置一个聚合查询的条件 ：1.设置聚合查询的名称（别名）2.设置分组的字段
            builder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));
        }
        else {
            builder.withQuery(QueryBuilders.matchAllQuery());
        }
        // (1) 设置高亮显示的域 并设置前缀 后缀
        builder.withHighlightFields(new HighlightBuilder.Field("title"))
            .withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));

        // 过滤查询 商品分类过滤
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String category = (String)searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            // builder.withFilter(QueryBuilders.termQuery("category", category));
            boolQueryBuilder.filter(QueryBuilders.termQuery("category", category));
        }
        // 过滤查询 ----商品的品牌的过滤查询
        String brand = (String)searchMap.get("brand");
        if (StringUtils.isNotBlank(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand", brand));
        }
        // 过滤查询 ----规格的过滤查询 获取到规格的名称 和规格的值 执行过滤查询
        Map<String, String> spec = (Map<String, String>)searchMap.get("spec");
        if (spec != null) {
            for (String key : spec.keySet()) {
                // 该路径上去查询
                TermQueryBuilder termQueryBuilder =
                    QueryBuilders.termQuery("specMap." + key + ".keyword", spec.get(key));
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        String price = (String)searchMap.get("price");
        if (StringUtils.isNotBlank(price)) {
            String[] split = price.split("-");
            if ("*".equals(split[1])) {
                // 价格大于
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }
            else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
            }
        }

        builder.withFilter(boolQueryBuilder);

        // 构建查询对象
        NativeSearchQuery query = builder.build();

        // 3.1设置分页条件
        Integer pageNo = (Integer)searchMap.get("pageNo");
        Integer pageSize = (Integer)searchMap.get("pageSize");
        if (pageNo == null) {
            pageNo = 1;
        }

        if (pageSize == null) {
            pageSize = 40;
        }
        query.setPageable(PageRequest.of(pageNo - 1, pageSize));
        // 排序条件 价格排序
        String sortField = (String)searchMap.get("sortField");
        String sortType = (String)searchMap.get("sortType");
        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            if (sortType.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, sortField);
                query.addSort(sort);
            }
            else if (sortType.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, sortField);
                query.addSort(sort);
            }
            else {
                System.out.println("不排序");
            }
        }
        // 4.执行查询 自定义结果封装 获取高亮的值
        AggregatedPage<TbItem> tbItems =
            elasticsearchTemplate.queryForPage(query, TbItem.class, new SearchResultMapper()
            {
                // 自定义处理封装结果
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    // 1.获取当前的页结果集
                    SearchHits hits = response.getHits();
                    List<T> content = new ArrayList<>();
                    // 如果没有搜索到记录
                    if (hits == null || hits.getHits().length <= 0) {
                        return new AggregatedPageImpl<T>(content);
                    }
                    for (SearchHit hit : hits) {
                        // 获取高亮
                        // 获取高亮的域为title的高亮对象
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        HighlightField highlightField = highlightFields.get("title");
                        // 就是每一个文档对应的json数据
                        String sourceAsString = hit.getSourceAsString();
                        TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);
                        System.out.println("高亮前的数据:" + tbItem.getTitle());

                        if (highlightField != null) {
                            // 获取高亮的碎片
                            Text[] fragments = highlightField.getFragments();
                            // 高亮的数据
                            StringBuffer sb = new StringBuffer();
                            if (fragments != null) {
                                for (Text fragment : fragments) {
                                    // 获取到的高亮碎片的值<em styple="colore:red">
                                    sb.append(fragment.string());
                                }
                            }
                            // 不为空的时候 存储值
                            if (StringUtils.isNotBlank(sb.toString())) {
                                tbItem.setTitle(sb.toString());
                            }
                        }

                        content.add((T)tbItem);
                    }
                    // 2.获取分页的信息对象 已有
                    long totalHits = hits.getTotalHits();
                    // 4.获取聚合查询的对象（ 统计 count sum groupby ）
                    Aggregations aggregations = response.getAggregations();
                    // 5.获取scrollid 深度分页的ID
                    String scrollId = response.getScrollId();

                    return new AggregatedPageImpl<T>(content, pageable, totalHits, aggregations, scrollId);
                }
            });

        // 5.获取结果 封装结果集
        // 获取分组的数据
        List<String> categoryList = new ArrayList<>();
        StringTerms stringTerms = (StringTerms)tbItems.getAggregation("category_group");
        if (stringTerms != null) {
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                // 分组的值 手机 笔记本
                String keyAsString = bucket.getKeyAsString();
                categoryList.add(keyAsString);
            }
        }
        // 获取第一个分类的下的所有的品牌和规格的列表
        if (StringUtils.isNotBlank(category)) {
            Map map = searchBrandAndSpecList(category);
            resultMap.putAll(map);
        }
        else {
            if (categoryList.size() != 0) {
                Map map = searchBrandAndSpecList(categoryList.get(0));
                resultMap.putAll(map);
            }
        }
        List<TbItem> itemList = tbItems.getContent();

        long totalElements = tbItems.getTotalElements();

        int totalPages = tbItems.getTotalPages();

        for (TbItem tbItem : itemList) {
            System.out.println("数据：" + tbItem.getTitle());
        }
        resultMap.put("rows", itemList);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", totalPages);
        resultMap.put("categoryList", categoryList);
        System.out.println(resultMap);

        return resultMap;
    }

    @Override
    public void updateIndex(List<TbItem> items) {
        // 先设置map 再一次性插入
        for (TbItem item : items) {
            String spec = item.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            item.setSpecMap(map);
        }
        System.out.println("更新成功...");
        dao.saveAll(items);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        DeleteQuery query = new DeleteQuery();
        //删除多个goodsid
        query.setQuery(QueryBuilders.termsQuery("goodsId", ids));
        //根据删除条件 索引名 和类型
        elasticsearchTemplate.delete(query, TbItem.class);
    }

    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long)redisTemplate.boundHashOps("itemCat").get(category);// 获取模板ID
        if (typeId != null) {
            // 根据模板ID查询品牌列表
            List brandList = (List)redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);// 返回值添加品牌列表
            // 根据模板ID查询规格列表
            List specList = (List)redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }
}
