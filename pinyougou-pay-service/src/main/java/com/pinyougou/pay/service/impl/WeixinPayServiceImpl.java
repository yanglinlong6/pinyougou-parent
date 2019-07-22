package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名:pinyougou-parent
 * 包名: com.pinyougou.pay.service.impl
 * 作者: Yanglinlong
 * 日期: 2019/7/14 22:11
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 生成二维码
     *
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
//        String a = "1";
//        total_fee = a;
        try {
            //1.组合参数集 存储到map中 map转换成XML
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("body", "品优购");
            paramMap.put("out_trade_no", out_trade_no);
            paramMap.put("total_fee", total_fee);
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify");
            paramMap.put("trade_type", "NATIVE");

            //......
            //自动添加签名 而且转成字符串
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");


            //2.使用httpclient 调用 接口 发送请求

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//请求体
            httpClient.post();
            //3.获取结果 XML  转成MAP(code_url)
            String resultXml = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(resultXml);

            Map<String, String> resultMap = new HashMap<>();

            resultMap.put("code_url", map.get("code_url"));
            resultMap.put("out_trade_no", out_trade_no);
            resultMap.put("total_fee", total_fee);
            //4.返回map
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            //1.组合参数集 存储到map中 map转换成XML

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", out_trade_no);

            //自动添加签名 而且转成字符串
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");


            //2.使用httpclient 调用 接口 发送请求

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//请求体
            httpClient.post();
            //3.获取结果 XML  转成MAP(code_url)
            String resultXml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭订单
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map closePay(String out_trade_no) {
        try {
            //参数设置
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("appid", "wx8397f8696b538317"); //应用ID
            paramMap.put("mch_id", "1473426802");    //商户编号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符
            paramMap.put("out_trade_no", out_trade_no);   //商家的唯一编号

            //将Map数据转成XML字符
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //确定url
            String url = "https://api.mch.weixin.qq.com/pay/closeorder";

            //发送请求
            HttpClient httpClient = new HttpClient(url);
            //https
            httpClient.setHttps(true);
            //提交参数
            httpClient.setXmlParam(xmlParam);

            //提交
            httpClient.post();

            //获取返回数据
            String content = httpClient.getContent();

            //将返回数据解析成Map
            return WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }
}