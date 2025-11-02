import com.ax.stock.JobApp;
import com.ax.stock.mapper.StockBusinessMapper;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = JobApp.class)
public class TestMapper {

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    @Test
    public void getAllCodes(){
        List<String> codes = stockBusinessMapper.getAllStockCodes();
        // stream流 【给List集合中的数据添加前缀】   匹配6开头的数据，如果匹配到就添加前缀sh，否则添加sz
        codes = codes.stream().map(code->code.startsWith("6")?"sh"+code:"sz"+code).collect(Collectors.toList());
        System.out.println("codes = " + codes);
        List<List<String>> partition = Lists.partition(codes, 15);
        System.out.println("partition = " + partition);
    }



}
