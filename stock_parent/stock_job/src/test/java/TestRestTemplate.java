import com.ax.stock.JobApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pojo.User;

import java.util.List;

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


    @Test
    public void getStockInfo(){
        String url = "http://hq.sinajs.cn/list=int_dji,int_nasdaq,int_hangseng,int_nikkei,b_FSSTI";
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        headers.add("Referer","https://finance.sina.com.cn/stock/");
        HttpEntity<List> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<List> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, List.class);
        List body = exchange.getBody();
        System.out.println("body = " + body);
    }

}
