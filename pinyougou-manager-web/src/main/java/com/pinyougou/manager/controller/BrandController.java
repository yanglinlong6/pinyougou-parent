package com.pinyougou.manager.controller;

import java.io.File;
import java.util.List;

import com.pinyougou.common.util.ImportExcel;
import entity.Result;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;

import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * controller222
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String status, @RequestBody Long[] ids){

        try {
            brandService.updateStatus(ids,status);
            return new Result(true,"审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"审核失败");
        }
    }

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return brandService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbBrand findOne(@PathVariable(value = "id") Long id) {
        return brandService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbBrand brand) {
        return brandService.findPage(pageNo, pageSize, brand);
    }


    @Autowired
    ImportExcel importExcel;

    /**
     * @param response
     * 使用POI提供的文档进行数据导出
     * 在ImportExcel 封装importExcel方法
     * 该方法对数据进行操作返回给用户一个Excel表格
     */
    @RequestMapping("/importExcel")
    public void importExcel(HttpServletResponse response){
        try {
            System.out.println("执行导出请求");
            List<TbBrand> tbBrands = brandService.selectAll();
            importExcel.importExcel(tbBrands, TbBrand.class, "brands.xls",response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用POI提供的文档进行数据导入
     * 在ImportExcel 封装importDataForExcel方法
     * 该方法对用户提交的Excel表格进行操作取出对应的数据插入数据库
     */
    @RequestMapping("/importData")
    public Result importData(@RequestParam MultipartFile file){
        try {
            CommonsMultipartFile cmf= (CommonsMultipartFile)file;
            DiskFileItem dfi=(DiskFileItem) cmf.getFileItem();
            File fo=dfi.getStoreLocation();
            List<TbBrand> tbBrands = importExcel.importDataForExcel(fo, TbBrand.class);

            brandService.insertAll(tbBrands);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }



}
