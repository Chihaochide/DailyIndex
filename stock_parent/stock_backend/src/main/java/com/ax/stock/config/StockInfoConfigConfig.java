package com.ax.stock.config;

import com.ax.stock.pojo.vo.StockInfoConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({StockInfoConfig.class}) // 开启对象相关配置对象的加载
public class StockInfoConfigConfig {
}
