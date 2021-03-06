package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {
    public Map createNative(String out_trade_no, String total_fee);

    public Map queryStatus(String out_trade_no);

    Map closePay(String out_trade_no);
}
