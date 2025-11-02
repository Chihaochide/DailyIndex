package com.ax.stock.config;

import com.ax.stock.util.IdWorker;
import com.ax.stock.util.ParserStockInfoUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 定义股票 大盘 外盘 板块 个股 相关工具的工具类Bean
 */
@Component
public class ParserStockInfoUtilConfig {

    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(IdWorker idWorker){
        return new ParserStockInfoUtil(idWorker);
    }

}
