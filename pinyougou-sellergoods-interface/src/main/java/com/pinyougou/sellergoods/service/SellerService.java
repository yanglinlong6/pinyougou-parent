package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.TbSeller;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.ShopOrderCount;

import java.util.Date;
import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SellerService extends CoreService<TbSeller> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeller> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeller> findPage(Integer pageNo, Integer pageSize, TbSeller Seller);

	public void updateStatus(String id,String status);

    List<ShopOrderCount> findDateMoney(String username, String forDate, String toDate);
}
