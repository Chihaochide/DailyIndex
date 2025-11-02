package com.ax.stock.service;

/**
 * 定义股票采集接口
 */
public interface StockTimerTaskService {
    /**
     * 获取国内大盘实时数据情况
     */
    void getInnerMarketInfo();

    /**
     * 采集个股
     */
    void getStockRtIndex();
}
