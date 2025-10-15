package com.ax.stock.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 定义股票相关的值对象进行封装
 */
@ApiModel(description = "定义股票相关的值对象进行封装")
@Data
@ConfigurationProperties(prefix = "stock")
public class StockInfoConfig {
    /**
     * 这是封装国内A股大盘的编码集合
     */
    @ApiModelProperty("这是封装国内A股大盘的编码集合")
    private List<String> inner;

    /**
     * 外盘编码集合
     */
    @ApiModelProperty("外盘编码集合")
    private List<String> outer;

    /**
     *   股票涨幅区间标题集合
     */
    @ApiModelProperty("股票涨幅区间标题集合")
    private List<String> upDownRange;

    /**
     * 大盘 外盘 个股的公共url
     */
    @ApiModelProperty("大盘 外盘 个股的公共url")
    private String marketUrl;

    /**
     * 板块采集的url地址
     */
    @ApiModelProperty("板块采集的url地址")
    private String blockUrl;
}



