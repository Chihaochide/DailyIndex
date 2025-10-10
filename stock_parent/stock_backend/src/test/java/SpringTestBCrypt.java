import com.ax.stock.BackendApplication;
import com.ax.stock.mapper.SysUserMapper;
import com.ax.stock.pojo.entity.SysUser;
import com.ax.stock.util.BCrypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BackendApplication.class)
public class SpringTestBCrypt {
    @Autowired
    private SysUserMapper userMapper;
    @Test
    public void createPass(){
        String password = "123";
        String salt = BCrypt.gensalt();
        String hashpw = BCrypt.hashpw(password, salt);
        System.out.println("hashpw = " + hashpw);
        SysUser admin = userMapper.findUserInfoByUserName("admin");
        boolean checkpw = BCrypt.checkpw(password, admin.getPassword());

        System.out.println("checkpw = " + checkpw);
    }
}
