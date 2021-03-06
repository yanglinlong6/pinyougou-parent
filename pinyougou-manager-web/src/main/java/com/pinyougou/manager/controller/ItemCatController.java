package com.pinyougou.manager.controller;

import java.io.File;
import java.util.List;

import com.pinyougou.common.util.ImportExcel;
import com.pinyougou.pojo.TbBrand;
import entity.Result;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;

import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    
    @Reference
    private ItemCatService itemCatService;
    
    /**
     * 返回全部列表
     * 
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbItemCat> findAll() {
        return itemCatService.findAll();
    }
    
    @RequestMapping("/findPage")
    public PageInfo<TbItemCat> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return itemCatService.findPage(pageNo, pageSize);
    }
    
    /**
     * 增加
     * 
     * @param itemCat
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbItemCat itemCat) {
        try {
            itemCatService.add(itemCat);
            return new Result(true, "增加成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }
    
    /**
     * 修改
     * 
     * @param itemCat
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbItemCat itemCat) {
        try {
            itemCatService.update(itemCat);
            return new Result(true, "修改成功");
        }
        catch (Exception e) {
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
    public TbItemCat findOne(@PathVariable(value = "id") Long id) {
        return itemCatService.findOne(id);
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
            itemCatService.delete(ids);
            return new Result(true, "删除成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    
    @RequestMapping("/search")
    public PageInfo<TbItemCat> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
        @RequestBody TbItemCat itemCat) {
        return itemCatService.findPage(pageNo, pageSize, itemCat);
    }
    
    @RequestMapping("/findByParentId/{parentId}")
    public List<TbItemCat> findByParentId(@PathVariable(value = "parentId") Long parentId) {
        return itemCatService.findByParentId(parentId);
        
    }
    
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String status, @RequestBody Long[] ids) {
        try {
            itemCatService.updateStatus(ids, status);
            return new Result(true, "审核成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }

    @Autowired
    ImportExcel importExcel;

    @RequestMapping("/importData")
    public Result importData(@RequestParam MultipartFile file){
        try {
            CommonsMultipartFile cmf= (CommonsMultipartFile)file;
            DiskFileItem dfi=(DiskFileItem) cmf.getFileItem();
            File fo=dfi.getStoreLocation();
            List<TbItemCat> itemCats = importExcel.importDataForExcel(fo, TbItemCat.class);

            itemCatService.insertAll(itemCats);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
