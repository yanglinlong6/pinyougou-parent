package entity;

import java.io.Serializable;

/**
 * @program: entity
 * @author: Sun jinwei
 * @create: 2019-07-26 12:55
 * @description: 销售饼状图的pojo类
 **/
public class Sale implements Serializable {
    private Long goodsId;
    private Integer num;
    private String name;
    private Long category1Id;

    public Sale() {
    }

    public Sale(Long goodsId, Integer num, String name, Long category1Id) {
        this.goodsId = goodsId;
        this.num = num;
        this.name = name;
        this.category1Id = category1Id;
    }

    public Long getCategory1Id() {
        return category1Id;
    }

    public void setCategory1Id(Long category1Id) {
        this.category1Id = category1Id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}