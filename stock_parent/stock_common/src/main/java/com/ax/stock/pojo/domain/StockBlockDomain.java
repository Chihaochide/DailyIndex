package com.ax.stock.pojo.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockBlockDomain {

    /**
     * 公司数量
     */
    private Integer companyNum;
    /**
     * 交易量
     */
    private Long tradeAmt;
    /**
     * 版跨编码
     */
    private String code;
    /**
     * 平均价格
     */
    private BigDecimal avgPrice;
    /**
     * 版跨名称
     */
    private String name;
    /**
     * 当前日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date curDate;
    /**
     *交易金额
     */
    private BigDecimal tradeVol;
    /**
     * 涨跌率
     */
    private BigDecimal updownRate;

}
