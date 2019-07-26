import com.pinyougou.mapper.TbUserCollectMapper;
import com.pinyougou.pojo.TbUserCollect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
public class App {

    @Autowired

    private TbUserCollectMapper userCollectMapper;
    @Test
    public void test01(){
        TbUserCollect tbUserCollect = new TbUserCollect();
        tbUserCollect.setUserId("zhangsanfeng");
        tbUserCollect.setItemId(123456l);
        userCollectMapper.insert(tbUserCollect);
    }
}
