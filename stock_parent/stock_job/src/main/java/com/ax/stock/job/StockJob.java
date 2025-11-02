package com.ax.stock.job;

import com.ax.stock.service.StockTimerTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定义xxljob任务执行器的bean
 */
@Component
public class StockJob {

    @Autowired
    private StockTimerTaskService stockTimerTaskService;

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("MyJobHandler")
    public void demoJobHandler() throws Exception {
        System.out.println("当前时间为："+ DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 定时采集A股大盘数据
     */
    @XxlJob("getStockInfos")
    public void getStockInfos(){
        stockTimerTaskService.getInnerMarketInfo();
    }

    /**
     * 定时采集A股个股大盘数据
     */
    @XxlJob("getStockRtIndex")
    public void getStockRtInfos(){
        stockTimerTaskService.getStockRtIndex();
    }

}
