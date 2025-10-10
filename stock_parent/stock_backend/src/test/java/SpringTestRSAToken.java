import com.ax.stock.BackendApplication;
import com.ax.stock.mapper.SysUserMapper;
import com.ax.stock.pojo.entity.SysUser;
import com.ax.stock.util.JwtUtils;
import com.ax.stock.util.RsaUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;

@SpringBootTest(classes = BackendApplication.class)
public class SpringTestRSAToken {
    private final String publicKeyPath = "E:\\MyProjectFile\\rsa-key.pub";
    private final String privateKeyPath = "E:\\MyProjectFile\\rsa-key";

    @Autowired
    private SysUserMapper userMapper;
    @Test
    public void creatGenericRSA() throws Exception{
        RsaUtils.generateKey(publicKeyPath,privateKeyPath,"xuanhe",1024);
    }

    @Test
    public void createToken() throws Exception{
        SysUser admin = userMapper.findUserInfoByUserName("admin");
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        JwtUtils.generateTokenExpireInMinutes(admin,privateKey,30);


    }
}
