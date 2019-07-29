package com.pinyougou.order.service;
import com.pinyougou.pojo.TbOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbPayLog;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService extends CoreService<TbOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder Order);

    TbPayLog searchPayLogFromRedis(String userId);

    void updateOrderStatus(String out_trade_no, String transaction_id);

	/**
	 *@Description //用户所有订单，及订单的详情
	 *@param  userId
	 *@return java.util.List<com.pinyougou.pojo.TbOrder>
	 *@time 2019-7-24 22:06
	 */
	List<TbOrder> getOrderByStatus(String userId, String status);

    void updateOrderStatusAndCreateLog(String out_trade_no, String transaction_id, String userId);

}
