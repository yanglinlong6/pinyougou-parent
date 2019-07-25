package com.pinyougou.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/11/17 9:03
 *      时间转换工具类
 ****/
public class DateUtil {

    //时间格式1
    public static final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

    /****
     * 将时间类型转成字符类型
     * @param date
     * @return
     */
    public static String date2str1(Date date){
        return  simpleDateFormat1.format(date);
    }

    /****
     * 将时间类型转成字符类型
     * @param date
     * @return
     */
    public static String date2str2(Date date){
        return  simpleDateFormat2.format(date);
    }

    public static void main(String[] args) {
        System.out.println(new Date());

        System.out.println(date2str1(new Date()));
        System.out.println(date2str2(new Date()));
    }
}
