package entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShopOrderCount implements Serializable {

    private Long shopGoodsId;

    private String shopName;

    private Integer shopCount;

    private BigDecimal shopTotalFee;

    public Long getShopGoodsId() {
        return shopGoodsId;
    }

    public void setShopGoodsId(Long shopGoodsId) {
        this.shopGoodsId = shopGoodsId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getShopCount() {
        return shopCount;
    }

    public void setShopCount(Integer shopCount) {
        this.shopCount = shopCount;
    }

    public BigDecimal getShopTotalFee() {
        return shopTotalFee;
    }

    public void setShopTotalFee(BigDecimal shopTotalFee) {
        this.shopTotalFee = shopTotalFee;
    }

    @Override
    public String toString() {
        return "ShopOrderCount{" +
                "shopGoodsId=" + shopGoodsId +
                ", shopName='" + shopName + '\'' +
                ", shopCount=" + shopCount +
                ", shopTotalFee=" + shopTotalFee +
                '}';
    }
}
