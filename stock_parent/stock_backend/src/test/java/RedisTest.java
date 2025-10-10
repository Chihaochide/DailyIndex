import com.ax.stock.BackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = BackendApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void redisTest(){

        // 存入值
        redisTemplate.opsForValue().set("name","ax");
        // 获取值

        String name = redisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);
    }
}
