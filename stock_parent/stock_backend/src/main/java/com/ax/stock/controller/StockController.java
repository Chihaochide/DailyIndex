package com.ax.stock.controller;

import com.ax.stock.pojo.domain.*;
import com.ax.stock.service.StockService;
import com.ax.stock.pojo.vo.resp.R;
import com.ax.stock.vo.resp.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api("/api/quot")
@RestController
@RequestMapping("/api/quot")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * 获取国内大盘最新数据
     * @return
     */
    @ApiOperation(value = "获取国内大盘最新数据", notes = "获取国内大盘最新数据", httpMethod = "GET")
    @GetMapping("/index/all")
    public R<List<InnerMarketDomain>> getInnerMarketInfo(){
        return stockService.getInnerMarketInfo();
    }

    /**
     * 获取板块指数
     */
    @ApiOperation(value = "获取板块指数", notes = "获取板块指数", httpMethod = "GET")
    @GetMapping("/sector/all")
    public R<List<StockBlockDomain>> getStockBlockInfo(){
        return stockService.getStockBlockInfo();
    }

    /**
     * 获取涨幅榜
     */
    // @RequestParam(value = "page",required = false,defaultValue = "1"
    // required：是否必填  defaultValue：默认值
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = ""),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "")
    })
    @ApiOperation(value = "获取涨幅榜", notes = "获取涨幅榜", httpMethod = "GET")
    @GetMapping("/stock/all")
    public R<PageResult<StockUpdownDomain>> getStockInfoByPage(@RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                                                   @RequestParam(value = "pageSize",required = false,defaultValue = "20") int pageSize){
        return stockService.getStockInfoByPage(page,pageSize);
    }

    /**
     * 获取涨幅功能榜
     */
    @ApiOperation(value = "获取涨幅功能榜", notes = "获取涨幅功能榜", httpMethod = "GET")
    @GetMapping("/stock/increase")
    public R<List<StockUpdownDomain>> getIncreaseInfoByPage(){
        return stockService.getIncreaseInfoByPage(1,4);
    }

    /**
     * 统计最新股票交易日内，每分钟的涨跌停的股票交易数量
     * @return
     */
    @ApiOperation(value = "统计最新股票交易日内，每分钟的涨跌停的股票交易数量", notes = "统计最新股票交易日内，每分钟的涨跌停的股票交易数量", httpMethod = "GET")
    @GetMapping("/stock/updown/count")
    public R<Map<String,List<Map<Integer,String>>>> getStockUpDownCount(){
        return stockService.getStockUpDownCount();
    }

    /**
     * 导出股票涨幅信息
     * @param response
     * @param page  当前页
     * @param pageSize 每页大小
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "当前页"),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页大小")
    })
    @ApiOperation(value = "导出股票涨幅信息", notes = "导出股票涨幅信息", httpMethod = "GET")
    @GetMapping("/stock/export")
    public void exportStockUpDownInfo (@RequestParam(value = "page",required = false,defaultValue = "1") int page,
                                       @RequestParam(value = "pageSize",required = false,defaultValue = "20") int pageSize,
                                       HttpServletResponse response){
        stockService.exportStockUpDownInfo(page,pageSize,response);
    }

    /**
     * 统计大盘T日和T-1日每分钟的交易量
     * @return
     */
    @ApiOperation(value = "统计大盘T日和T-1日每分钟的交易量", notes = "统计大盘T日和T-1日每分钟的交易量", httpMethod = "GET")
    @GetMapping("/stock/tradeAmt")
    public R<Map<String,List>> getCompareStockTradeAmt(){
        return stockService.getCompareStockTradeAmt();
    }

    /**
     * 统计最新交易时间点（A股）个股分时涨幅度
     */
    @ApiOperation(value = "统计最新交易时间点（A股）个股分时涨幅度", notes = "统计最新交易时间点（A股）个股分时涨幅度", httpMethod = "GET")
    @GetMapping("/stock/updown")
    public R<Map<String,Object>> getIncreaseRangeInfo(){
        return stockService.getIncreaseRangeInfo();
    }

    /**
     * 获取指定股票T日的分时数据
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "code", value = "股票编码", required = true)
    })
    @ApiOperation(value = "获取指定股票T日的分时数据", notes = "获取指定股票T日的分时数据", httpMethod = "GET")
    @GetMapping("/stock/screen/time-sharing")
    public R<List<Stock4MinuteDomain>> getStockScreenTimeSharing(@RequestParam(value = "code",required = true) String code){
        return stockService.getStockScreenTimeSharing(code);
    }

    /**
     * 统计股票日K线数据
     * @param code 股票编码
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "code", value = "股票编码", required = true)
    })
    @ApiOperation(value = "统计股票日K线数据", notes = "统计股票日K线数据", httpMethod = "GET")
    @GetMapping("/stock/screen/dkline")
    public R<List<Stock4EvrDayDomain>> getStockScreenDKLine(@RequestParam(value = "code",required = true) String code){
        return stockService.getStockScreenDKLine(code);
    }
}
