package com.ax.stock.service;

import com.ax.stock.pojo.domain.InnerMarketDomain;
import com.ax.stock.pojo.domain.StockBlockDomain;
import com.ax.stock.pojo.domain.StockUpdownDomain;
import com.ax.stock.pojo.vo.resp.R;
import com.ax.stock.vo.resp.PageResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface StockService {
    R<List<InnerMarketDomain>> getInnerMarketInfo();


    R<List<StockBlockDomain>> getStockBlockInfo();

    R<PageResult<StockUpdownDomain>> getStockInfoByPage(int page,int pageSize);

    R<List<StockUpdownDomain>> getIncreaseInfoByPage(int page,int pageSize);

    R<Map<String, List<Map<Integer, String>>>> getStockUpDownCount();

    void exportStockUpDownInfo(int page, int pageSize, HttpServletResponse response);
}
