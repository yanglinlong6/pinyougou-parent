package com.sunjinwei.sellergoods;


import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @program: com.sunjinwei.sellergoods
 * @author: Sun jinwei
 * @create: 2019-06-18 12:55
 * @description:
 **/
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class MybatisTest {
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbUserMapper tbUserMapper;

    @Test
    public void testUser(){
        TbUser tbUser = new TbUser();
        tbUser.setUsername("zhangsanfeng");
        TbUser tbUser1 = tbUserMapper.selectOne(tbUser);

        System.out.println(tbUser1.getUsername());
    }

    /**
     * 查询 selectAll和selectByExample两种方法
     */
    @Test
    public void testSelect() {
        System.out.println("11111");
        System.out.println("hhhhhh=====" + tbBrandMapper);
        //List<TbBrand> tbBrands = tbBrandMapper.selectAll();
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);

        for (TbBrand tbBrand : tbBrands) {
            System.out.println(tbBrand.getName());
        }
    }

    /**
     * 增加 两种方法 insertSelective和insert方法
     */
    @Test
    public void testAdd() {
        TbBrand tbBrand = new TbBrand();
        tbBrand.setName("李宁");
        tbBrand.setFirstChar("A");
        tbBrandMapper.insert(tbBrand);
        //tbBrandMapper.insertSelective(tbBrand);
    }

    /**
     * 改--两种方法 分为有条件的 和 没有条件 的 各两种
     * 使用updateByPrimaryKey 那么如果别的属性没有进行设置 会自动设置为null
     * 如果使用updateByPrimaryKeySelective 那么如果有的属性没有进行更改 也不会进行设置为null
     */
    @Test
    public void update01() {
        TbBrand brandupdated = new TbBrand();
        brandupdated.setId(36L);
        brandupdated.setName("埃利斯");
        //tbBrandMapper.updateByPrimaryKey(brandupdated);
        tbBrandMapper.updateByPrimaryKeySelective(brandupdated);
    }

    /**
     * 改--如果使用updateByPrimaryKeySelective 那么如果有的属性没有进行更改 也不会进行设置为null
     */
    @Test
    public void update02() {
        TbBrand brandupdated = new TbBrand();
        brandupdated.setId(37L);
        brandupdated.setName("哈哈哈");
        tbBrandMapper.updateByPrimaryKeySelective(brandupdated);
    }

    /**
     * 删除--三种
     * deleteByPrimaryKey 根据主键删除 直接传入ID删除即可
     * delete 跟上面一样
     * deleteByExample 根据条件删除
     */
    @Test
    public void delete() {
        TbBrand brand = new TbBrand();
        brand.setId(38L);
        //tbBrandMapper.deleteByPrimaryKey(brand);
        //tbBrandMapper.delete(brand);
        tbBrandMapper.deleteByPrimaryKey(37L);
    }

    /**
     * 根据ID查询 直接将ID放入selectByPrimaryKey()括号中即可
     */
    @Test
    public void findOne() {

        TbBrand brand02 = tbBrandMapper.selectByPrimaryKey(1L);
        System.out.println(brand02.getName());

        TbItem tbItem = tbItemMapper.selectByPrimaryKey(1009237L);
        System.out.println(tbItem.getSellerId());


    }

}