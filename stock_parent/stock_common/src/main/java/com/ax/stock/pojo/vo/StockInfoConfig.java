package com.ax.stock.pojo.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 定义股票相关的值对象进行封装
 */
@Data
@ConfigurationProperties(prefix = "stock")
public class StockInfoConfig {
    /**
     * 这是封装国内A股大盘的编码集合
     */
    private List<String> inner;

    /**
     * 外盘编码集合
     */
    private List<String> outer;
}
