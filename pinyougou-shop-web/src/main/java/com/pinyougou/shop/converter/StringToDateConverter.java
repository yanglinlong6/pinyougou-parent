package com.pinyougou.shop.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * @ClassName StringToDateConverter
 * @Description TODO
 * @Author ly
 * @Company 深圳黑马程序员
 * @Date 2019/5/31 16:45
 * @Version V1.0
 */
// 第一步：定义一个类型转换器
public class StringToDateConverter implements Converter<String, Date> {
    @Nullable
    @Override
    public Date convert(String s) {
        Date date = null;
        if (s == null) {
            return date;
        }
        else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = df.parse(s);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println(date);
        return date;
    }
}
