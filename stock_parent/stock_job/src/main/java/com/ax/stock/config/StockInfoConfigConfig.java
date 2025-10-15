package com.ax.stock.config;

import com.ax.stock.pojo.vo.StockInfoConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(StockInfoConfig.class)
public class StockInfoConfigConfig {
}
