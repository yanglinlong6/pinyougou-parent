package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.common.util.ImportExcel;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.OrderService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemSearchService itemSearchService;

    @Reference
    private ItemPageService itemPageService;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(name);
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
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
    public Goods findOne(@PathVariable(value = "id") Long id) {
        return goodsService.findOne(id);
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
            goodsService.delete(ids);
//            itemSearchService.deleteByIds(ids);
            MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_delete_tag", "delete", ids, MessageInfo.METHOD_DELETE);
            producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {
//		goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
        return goodsService.findPage(pageNo, pageSize, goods);
    }

    @Autowired
    private DefaultMQProducer producer;


    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@RequestBody Long[] ids, @PathVariable(value = "status") String status) {
        try {
            goodsService.updateStatus(ids, status);
            if ("1".equals(status)) {
                System.out.println(Arrays.toString(ids));
                List<TbItem> tbItemListByIds = goodsService.findTbItemListByIds(ids);
                System.out.println(tbItemListByIds);
                MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_update_tag", "updateStatus", tbItemListByIds, MessageInfo.METHOD_UPDATE);
                System.out.println(messageInfo.toString());
                SendResult send = producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
                System.out.println(">>>>" + send.getSendStatus());
//                itemSearchService.updateIndex(tbItemListByIds);
//                for (Long id : ids) {
//                    itemPageService.genItemHtml(id);
//                    System.out.println(id);
//                }
            }
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    /**
     * 商品上下架  0：未上架
     *            1：已上架
     * @param ids
     * @param isMarke
     * @return
     */
    @RequestMapping("/updateIsMarke/{isMarke}")
    public Result updateIsMarke(@RequestBody Long[] ids, @PathVariable(value = "isMarke") String isMarke) {
        try {
            //更新数据库
            goodsService.updateIsMarke(ids,isMarke);
            //要求上架
            if ("1".equals(isMarke)) {
                System.out.println(ids.toString());
                List<TbItem> tbItemListByIds = goodsService.findTbItemListByIds(ids);
                System.out.println(tbItemListByIds);
                MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_update_tag", "updateStatus", tbItemListByIds, MessageInfo.METHOD_UPDATE);
                System.out.println(messageInfo.toString());
                SendResult send = producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
                System.out.println(">>>>" + send.getSendStatus());
    //                itemSearchService.updateIndex(tbItemListByIds);
    //                for (Long id : ids) {
    //                    itemPageService.genItemHtml(id);
    //                    System.out.println(id);
    //                }
                //上架驳回
            }else if("0".equals(isMarke)) {
                MessageInfo messageInfo = new MessageInfo("Goods_Topic", "goods_delete_tag", "delete", ids, MessageInfo.METHOD_DELETE);
                producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
            }
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上架失败");
        }
    }


    @Autowired
    ImportExcel importExcel;

    @RequestMapping("/importExcel")
    public void importExcel(HttpServletResponse response){
        try {
            System.out.println("执行导出请求");
            List<TbGoods> tbGoods = goodsService.selectAll();
            importExcel.importExcel(tbGoods, TbGoods.class, "Goods.xls",response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Reference
    private OrderService orderService;

    @RequestMapping("/importExcelForOrder")
    public void importExcelForOrder(HttpServletResponse response){
        try {
            System.out.println("执行导出请求");
            List<TbOrder> tbOrders = orderService.selectAll();
            importExcel.importExcel(tbOrders, TbOrder.class, "TbOrder.xls",response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
