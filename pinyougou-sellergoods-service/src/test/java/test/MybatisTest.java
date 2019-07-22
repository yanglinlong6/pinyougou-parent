package test;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;

/**
 * 项目名:pinyougouDemo
 * 包名: test
 * 作者: Yanglinlong
 * 日期: 2019/6/18 21:02
 */
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class MybatisTest {
    @Autowired
    private TbBrandMapper tbBrandMapper;
        @Test
            public void test(){
            Example example  =new Example(TbBrand.class);
            Example.Criteria criteria = example.createCriteria();
            Long[] ids={2L,3L,4L};
            criteria.andIn("id", Arrays.asList(ids));
            tbBrandMapper.deleteByExample(example);
        }

}
