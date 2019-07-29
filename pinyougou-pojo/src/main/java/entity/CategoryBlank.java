package entity;

import com.pinyougou.pojo.TbItemCat;

import java.io.Serializable;
import java.util.List;

public class CategoryBlank implements Serializable {
    private TbItemCat category1;

    private List<SubItemCat> category2;

    public TbItemCat getCategory1() {
        return category1;
    }

    public void setCategory1(TbItemCat category1) {
        this.category1 = category1;
    }

    public List<SubItemCat> getCategory2() {
        return category2;
    }

    public void setCategory2(List<SubItemCat> category2) {
        this.category2 = category2;
    }
}
