import com.alibaba.excel.EasyExcel;
import com.ax.stock.JobApp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pojo.User;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


    @Test
    public void getDemo() throws JsonProcessingException {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)); // 例如 Clash 或 VPN 代理
        factory.setProxy(proxy);

        restTemplate = new RestTemplate(factory);
        String rex = "   抖音 童锣烧 岛遇 NO.018期 【2P4V】</div>";
//        headers.add("Cookie","__51vcke__Kj49Yf37bPJaCBBa=21782289-1181-5cc5-b7fb-b439252b070c; __51vuft__Kj49Yf37bPJaCBBa=1760781250872; deviceId=hK8JNiTDxDZR6JdJtfPr5d8P; userStore=%7B%22info%22%3A%7B%22userId%22%3A%221979486164498743296%22%2C%22merchantAcct%22%3A%22sf06%22%2C%22masterAcct%22%3A%22sf06_m%22%2C%22agentAcct%22%3A%22sf06_m_no_agent%22%2C%22userAcct%22%3A%22PZXYFHOE%22%2C%22acctType%22%3A3%2C%22referCode%22%3Anull%2C%22shareCode%22%3A%22PZXYFHOE%22%2C%22isPartner%22%3A0%2C%22phoneNumber%22%3Anull%2C%22background%22%3Anull%2C%22headUrl%22%3A%22%2Femp%2Fhead%2F4372dab2bea64228a9f47764904cc725%22%2C%22nickName%22%3A%22PZXYFHOE%22%2C%22signature%22%3Anull%2C%22loginType%22%3Anull%2C%22coinBalance%22%3A0%2C%22balance%22%3A0%2C%22exp%22%3A0%2C%22expLevel%22%3A0%2C%22iconFree%22%3Anull%2C%22vipBegin%22%3Anull%2C%22vipEnd%22%3Anull%2C%22vipFlag%22%3Afalse%2C%22vipTitle%22%3Anull%2C%22vipPackageId%22%3Anull%2C%22userStatus%22%3A0%2C%22followers%22%3Anull%2C%22followed%22%3Anull%2C%22lastLoginDate%22%3Anull%2C%22currentLoginDate%22%3Anull%2C%22city%22%3A%22%E6%96%B0%E8%A5%BF%E4%BC%AF%E5%88%A9%E4%BA%9A%22%2C%22gender%22%3Anull%2C%22videoFreeBegin%22%3Anull%2C%22videoFreeEnd%22%3Anull%2C%22actorFreeBegin%22%3Anull%2C%22actorFreeEnd%22%3Anull%2C%22expand%22%3Anull%2C%22levelIcon%22%3Anull%2C%22headIcon%22%3Anull%7D%2C%22searchList%22%3A%5B%5D%2C%22scrollLeft%22%3A0%2C%22dialogTime%22%3A0%7D; token=16f32943be434af89dd6fca1c5647b1c.Td5K1WIF%2FyfgzZA%2F8QW9hEmmtVUQPm00UhVZooj%2BIROJ%2FoGMPF29pinVLK7tQlSlKpo7AcixUhq2WHPnnP3fQsgaSZwr6uiP0ouV9x6vpXFLYxafRoZtQ3%2F4arXKhgZemkWk0TExsvwxrxFmY6I%2BsL88ENr%2BebCL.7555d9fada1732b8211946d574c6bb8c; __51uvsct__Kj49Yf37bPJaCBBa=2; __vtins__Kj49Yf37bPJaCBBa=%7B%22sid%22%3A%20%22a82c69cf-d736-5b6b-b17b-25e296190c62%22%2C%20%22vd%22%3A%202%2C%20%22stt%22%3A%208373%2C%20%22dr%22%3A%208373%2C%20%22expires%22%3A%201760784938697%2C%20%22ct%22%3A%201760783138697%7D");

        List<String> list = new ArrayList<>();
        String url = "https://wemecat.com/category/1686327512891142144";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/json; charset=utf-8");
        headers.add("Referer","https://wemecat.com/category/1686327512891142144");
        headers.add("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("categoryId","1686327512891142144");
        hashMap.put("mediaType",3);
        hashMap.put("orderType","SORT_PUBLISH_WEIGHT");
        hashMap.put("pageSize",59);
        for (int i = 1; i <= 49; i++) {

            hashMap.put("pageNo",i);
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(hashMap);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonBody,headers);
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            Pattern compile = Pattern.compile(    "<div[^>]*class=\"[^\"]*font-bold[^\"]*\"[^>]*>(.*?)</div>",
                    Pattern.DOTALL);
            Matcher matcher = compile.matcher(exchange.getBody());
            while (matcher.find()){
                if (!matcher.group(1).startsWith("抖")) continue;
                list.add(matcher.group(1));
            }
        }
        List<List<String>> collect = list.stream().map(Collections::singletonList).collect(Collectors.toList());
        EasyExcel.write("C:\\Users\\刘轩赫\\Desktop\\Excel\\EasyExcelTest.xlsx",String.class).sheet().doWrite(collect);


    }

    @Test
    public void test123() throws Exception{
        String cipherText= new String(
                Files.readAllBytes(Paths.get("C:\\Users\\刘轩赫\\Desktop\\Excel\\123.txt")),
                StandardCharsets.UTF_8
        );
        cipherText = cipherText.replaceAll("\\s+", "");
          // 密钥（从JS中找到的固定值）
        String key = "GcgzsKdDZTumABNz7uujrCfPIk9TQ355";

        // AES/ECB/PKCS5Padding 等价于 CryptoJS 的 AES + ECB + PKCS7Padding
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

        System.out.println("✅ 解密结果：");
        System.out.println(decryptedText);
    }
}
