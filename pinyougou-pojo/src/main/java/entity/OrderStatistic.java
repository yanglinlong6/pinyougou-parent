package entity;


import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoods;
import java.math.BigDecimal;

public class OrderStatistic {
    private TbGoods goods;
    private TbGoodsDesc TbGoodsDesc;//
    private BigDecimal price;//商品单价
    private Integer num;//商品购买数量
    private BigDecimal totalFee;//商品总金额
    private Long goodsId;//SPU ID

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods Goods) {
        this.goods = Goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return TbGoodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc TbGoodsDesc) {
        this.TbGoodsDesc = TbGoodsDesc;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "OrderStatistic{" +
                "TbGoods=" + goods +
                ", TbGoodsDesc=" + TbGoodsDesc +
                ", price=" + price +
                ", num=" + num +
                ", totalFee=" + totalFee +
                ", goodsId=" + goodsId +
                '}';
    }
}
