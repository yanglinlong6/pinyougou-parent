package com.pinyougou.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: com.pinyougou.common.util
 * @author: Sun jinwei
 * @create: 2019-07-26 10:19
 * @description:
 **/
public class MyDateUtil {
    /**
     * 将字符串的日期格式转化为pattern类型的日期格式
     * 24h
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date toDate(String date, String pattern) throws ParseException {
        Date parse = null;
        if (StringUtils.isNoneBlank(date) && StringUtils.isNoneBlank(pattern)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            parse = dateFormat.parse(date);
        }
        return parse;
    }

    /**
     * 将日期格式类型转化为字符串类型
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String toString(Date date, String pattern) {
        String format = null;
        if (StringUtils.isNoneBlank(pattern) && date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            format = dateFormat.format(date);
        }
        return format;
    }

    /**
     * 判断两个日期之间是否超过三个月
     *
     */
    public static boolean isGreaterThanThreeMonths(String date01, String date02, String format) throws ParseException {
        boolean flag = false;
        if (StringUtils.isNoneBlank(date01) && StringUtils.isNoneBlank(date02) && StringUtils.isNoneBlank(format)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date date1 = dateFormat.parse(date01);
            Date date2 = dateFormat.parse(date02);
            long time01 = date1.getTime();
            long time02 = date2.getTime();
            long time = time02 - time01;
            //三个月的毫秒数：90天*24h*60m*60s*1000ms
            long timeTh = (long) 90 * 24 * 60 * 60 * 1000;
            //如果传进来的date02-date01大于三个月的毫秒数 那么返回true
            if (time > timeTh) {
                flag = true;
            }
        }
        return flag;
    }

    public static void main(String[] args) throws ParseException {
        boolean b = isGreaterThanThreeMonths("2019-03-01", "2019-04-04", "yyyy-MM-dd");
        System.out.println(b);
    }
}