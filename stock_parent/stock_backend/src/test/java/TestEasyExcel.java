import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ax.stock.BackendApplication;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pojo.User;

import java.util.ArrayList;
import java.util.List;
public class TestEasyExcel {

    public List<User> init(){
        // 组装数据
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setAddress("沈阳"+i);
            user.setUserName("张三"+i);
            user.setAge(14+i);
            DateTime dateTime = DateTime.now().withTimeAtStartOfDay().plusDays(i);
            user.setBirthday(dateTime.toDate());
            users.add(user);
        }
        return users;
    }

    /**
     * 测试EasyExcel导出
     */
    @Test
    public void test(){
        List<User> users = this.init();
        // 数据导出到Excel下
        EasyExcel.write("C:\\Users\\刘轩赫\\Desktop\\Excel\\EasyExcelTest.xlsx",User.class)
                .sheet("用户信息").doWrite(users);
    }

    @Test
    public void readExcel(){
        List<User> users = new ArrayList<>();
        EasyExcel.read("C:\\Users\\刘轩赫\\Desktop\\Excel\\EasyExcelTest.xlsx", User.class, new AnalysisEventListener<User>() {
            @Override
            public void invoke(User o, AnalysisContext analysisContext) {
                users.add(o);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("完成");
            }
        }).sheet("用户信息").doRead();
        System.out.println("users = " + users);
    }

}
