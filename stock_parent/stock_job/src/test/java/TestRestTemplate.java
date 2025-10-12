import com.ax.stock.JobApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pojo.User;

@SpringBootTest(classes = JobApp.class)
public class TestRestTemplate {

    @Autowired
    public RestTemplate restTemplate;

    @Test
    public void test(){
        String url = "localhost:8081/account/getUserByName?userName=admin";
        // 假数据
        // user ：userName = admin,password = root,address = sy
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        // 获取响应头
        HttpHeaders headers = result.getHeaders();
        System.out.println("headers.toString() = " + headers.toString());
        // [Content-Length:"81", Content-Type:"text/html", Server:"bfe", Date:"Sun, 12 Oct 2025 16:09:55 GMT"]

        // 获取响应状态码
        int statusCode = result.getStatusCodeValue();
        System.out.println("statusCode = " + statusCode);

        // 获取响应的数据 对应前端Response返回的数据
        String body = result.getBody();
        System.out.println("body = " + body);
        // {"userName":"admin","password":"root","address":"sy"}

        // 如果想直接返回对象，可以使用
        User user = restTemplate.getForObject(url, User.class);
        System.out.println("user = " + user);
    }

}
