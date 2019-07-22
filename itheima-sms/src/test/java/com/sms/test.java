package com.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 项目名:pinyougou-parent
 * 包名: com.sms
 * 作者: Yanglinlong
 * 日期: 2019/7/6 21:20
 */
public class test {
    public static void main(String[] args) throws Exception {
        int i = test.invokeHttpTest();
        System.out.println(i);
    }
    public static int invokeHttpTest() throws Exception
    {
        URL url = new URL(
                "http://jiekou.56dxw.com/sms/HttpInterface.aspx?comid=企业ID&username=用户名"
                        + "&userpwd=密码&handtel=手机号&sendcontent=内容限制为个字，小灵通个字"
                        + "&sendtime=定时时间&smsnumber=所用平台");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                httpCon.getInputStream()));
        String line = in.readLine();
        System.out.println(" </p>     result:   " + line);
        int i_ret = httpCon.getResponseCode();
        String sRet = httpCon.getResponseMessage();
        System.out.println("sRet   is:   " + sRet);
        System.out.println("i_ret   is:   " + i_ret);
        return 0;
    }
}
