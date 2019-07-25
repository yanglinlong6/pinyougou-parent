package com.pinyougou.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.Error;
import entity.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add/{smscode}")
    public Result add(@Valid @RequestBody TbUser user, BindingResult bindingResult, @PathVariable(value = "smscode") String smscode){
        try {
            if(bindingResult.hasErrors()){
                Result result = new Result(false,"失败");
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                for (FieldError fieldError : fieldErrors) {
                    result.getErrorsList().add(new Error(fieldError.getField(),fieldError.getDefaultMessage()));
                }
                return result;
            }
            boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smscode);

            if(checkSmsCode==false){
                Result result = new Result(false,"验证码输入错误");
                result.getErrorsList().add(new Error("smsCode","验证码输入错误"));
                return result;
            }

            user.setCreated(new Date());
            user.setUpdated(new Date());
            String password = DigestUtils.md5Hex(user.getPassword());
            user.setPassword(password);
            userService.add(user);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
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
    public TbUser findOne(@PathVariable(value = "id") Long id) {
        return userService.findOne(id);
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
            userService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                     @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }

    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            return new Result(false, "手机号格式不正确");
        }
        try {
            userService.createSmsCode(phone);
            return new Result(true, "验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "验证码发送失败");
        }
    }

    @RequestMapping(path = "/updateDetail")
    /**
    *@Description //更新用户个人信息
    *@param  []
    *@return entity.Result
    *@time 2019-7-24 10:27
    */
    public Result updateDetail(@RequestBody TbUser tbUser){
        try {
            tbUser.setUpdated(new Date());
            userService.updateByPrimaryKeySelective(tbUser);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");

        }
    }

    @RequestMapping(path = "/findByUserId")
    /**
    *@Description //用户的原始信息
    *@param  []
    *@return com.pinyougou.pojo.TbUser
    *@time 2019-7-24 21:17
    */
    public TbUser findByUserId(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser tbUser = new TbUser();
        tbUser.setUsername(userId);
        return userService.selectOne(tbUser);
    }

    @RequestMapping("/findFootMark")
    public Map findFootMark() {
        return userService.findFootMark();

    }
}
