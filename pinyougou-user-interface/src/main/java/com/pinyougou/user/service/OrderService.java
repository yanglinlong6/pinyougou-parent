package com.pinyougou.user.service;

import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbOrder;

import java.util.List;

public interface OrderService extends CoreService<TbOrder> {
    List<TbOrder> getAllOrder(String userId);
}
