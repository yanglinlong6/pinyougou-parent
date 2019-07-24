package com.pinyougou.manager.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.pinyougou.common.util.ImportExcel;
import com.pinyougou.pojo.TbBrand;
import entity.Result;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;

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
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    
    @Reference
    private TypeTemplateService typeTemplateService;
    
    /**
     * 返回全部列表
     * 
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbTypeTemplate> findAll() {
        return typeTemplateService.findAll();
    }
    
    @RequestMapping("/findPage")
    public PageInfo<TbTypeTemplate> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return typeTemplateService.findPage(pageNo, pageSize);
    }
    
    /**
     * 增加
     * 
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbTypeTemplate typeTemplate) {
        try {
            typeTemplateService.add(typeTemplate);
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
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbTypeTemplate typeTemplate) {
        try {
            typeTemplateService.update(typeTemplate);
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
    public TbTypeTemplate findOne(@PathVariable(value = "id") Long id) {
        return typeTemplateService.findOne(id);
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
            typeTemplateService.delete(ids);
            return new Result(true, "删除成功");
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }
    
    @RequestMapping("/search")
    public PageInfo<TbTypeTemplate> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
        @RequestBody TbTypeTemplate typeTemplate) {
        return typeTemplateService.findPage(pageNo, pageSize, typeTemplate);
    }
    
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String status, @RequestBody Long[] ids) {
        try {
            typeTemplateService.updateStatus(ids);
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
            List<Map<String, String>> forExcel = importExcel.importDataForExcel(fo);
            typeTemplateService.insertAll(forExcel);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
