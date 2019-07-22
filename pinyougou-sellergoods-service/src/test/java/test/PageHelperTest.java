package test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 项目名:pinyougouDemo
 * 包名: test
 * 作者: Yanglinlong
 * 日期: 2019/6/18 22:46
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class PageHelperTest {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Test
    public void pageHelper() {
        PageHelper.startPage(1, 5);
        List<TbBrand> tbBrands = tbBrandMapper.selectAll();
        PageInfo<TbBrand> pageInfo = new PageInfo<>(tbBrands);
        List<TbBrand> list = pageInfo.getList();
        for (TbBrand tbBrand : list) {
            System.out.println(tbBrand);
        }

    }

}
