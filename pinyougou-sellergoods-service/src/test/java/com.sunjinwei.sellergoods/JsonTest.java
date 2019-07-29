package com.sunjinwei.sellergoods;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbTypeTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * @program: com.sunjinwei.sellergoods
 * @author: Sun jinwei
 * @create: 2019-06-26 08:59
 * @description:
 **/
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class JsonTest {
    @Autowired
    private TbTypeTemplateMapper tbTypeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;

    @Test
    public void test01(){
        TbTypeTemplate tbTypeTemplate = tbTypeTemplateMapper.selectByPrimaryKey(35L);
        System.out.println(tbTypeTemplate.getSpecIds());
        String specIds = tbTypeTemplate.getSpecIds();
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        System.out.println(maps);
        for (Map map : maps) {
            Example example = new Example(TbSpecificationOption.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("specId",map.get("id"));
            System.out.println(map.get("id"));
            List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
            map.put("options",tbSpecificationOptions);
        }
        System.out.println(maps);
    }
}