package entity;

import com.pinyougou.pojo.TbItemCat;

import java.io.Serializable;
import java.util.List;

public class SubItemCat implements Serializable {
    private String name;

    private Long typeId;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    private List<TbItemCat> category3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TbItemCat> getCategory3() {
        return category3;
    }

    public void setCategory3(List<TbItemCat> category3) {
        this.category3 = category3;
    }
}
