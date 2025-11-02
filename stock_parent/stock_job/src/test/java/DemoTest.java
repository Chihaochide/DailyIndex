import com.ax.stock.JobApp;
import com.ax.stock.service.StockTimerTaskService;
import com.ax.stock.service.impl.StockTimerTaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = JobApp.class)
public class DemoTest {

    @Autowired
    private StockTimerTaskService stockTimerTaskService;

    @Test
    public void test(){
        stockTimerTaskService.getInnerMarketInfo();
    }

    @Test
    public void getStockRtIndex() throws Exception{
        stockTimerTaskService.getStockRtIndex();
        Thread.sleep(50000);
    }

}
