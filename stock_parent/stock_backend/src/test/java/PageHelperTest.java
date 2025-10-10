import com.ax.stock.BackendApplication;
import com.ax.stock.mapper.SysUserMapper;
import com.ax.stock.pojo.entity.SysUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = BackendApplication.class)
public class PageHelperTest {

    @Autowired
    private SysUserMapper userMapper;

    @Test
    public void pageHelperModel(){
        PageHelper.startPage(1,10);
        List<SysUser> all = userMapper.getAll();
        PageInfo<SysUser> pageInfo = new PageInfo<>(all);
        System.out.println("pageInfo = " + pageInfo);
    }

}
