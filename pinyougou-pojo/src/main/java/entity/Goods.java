package entity;

import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名:pinyougou-parent
 * 包名: entity
 * 作者: Yanglinlong
 * 日期: 2019/6/25 10:19
 */
public class Goods implements Serializable {
    private Goods Goods;//商品SPU
    private TbGoodsDesc TbGoodsDesc;//商品扩展
    private List<TbItem> itemList;//商品SKU列表

    public Goods getGoods() {
        return Goods;
    }

    public void setGoods(Goods Goods) {
        this.Goods = Goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return TbGoodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc TbGoodsDesc) {
        this.TbGoodsDesc = TbGoodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
