package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/address")
public class AddressController {
    @Reference
    private AddressService addressService;

    @RequestMapping(path = "/addAddress")
    public Result addAddress(@RequestBody TbAddress tbAddress){
        try {
            tbAddress.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
            addressService.insert(tbAddress);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping(path = "/findAll")
    public List<TbAddress> findAll(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findByUserId(userId);
    }

    @RequestMapping(path = "/deleteAddress")
    public Result findAll(@RequestParam Long id){
        try {
            addressService.deleteByPrimaryKey(id);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }

    @RequestMapping(path = "/findById")
    public TbAddress findById(@RequestParam Long id) {
        TbAddress tbAddress = addressService.selectByPrimaryKey(id);
        return tbAddress;
    }
}
