package entity;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名:pinyougou-parent
 * 包名: entity
 * 作者: Yanglinlong
 * 日期: 2019/6/23 21:18
 */
public class Specification implements Serializable {
    private TbSpecification specification;
    private List<TbSpecificationOption> optionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<TbSpecificationOption> optionList) {
        this.optionList = optionList;
    }
}
