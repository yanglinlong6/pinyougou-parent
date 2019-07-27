package entity;

import java.io.Serializable;

/**
 * @program: entity
 * @author: Sun jinwei
 * @create: 2019-07-26 18:57
 * @description: 饼状图的类
 **/
public class SaleCount implements Serializable {
    private String categoryName; //一级分类名字
    private String count; //销量比

    public SaleCount() {
    }

    public SaleCount(String categoryName, String count) {
        this.categoryName = categoryName;
        this.count = count;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}