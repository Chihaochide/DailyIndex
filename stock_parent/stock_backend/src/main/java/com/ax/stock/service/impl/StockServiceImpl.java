package com.ax.stock.service.impl;

import com.alibaba.excel.EasyExcel;
import com.ax.stock.exception.DailyIndexException;
import com.ax.stock.mapper.StockBlockRtInfoMapper;
import com.ax.stock.mapper.StockMarketIndexInfoMapper;
import com.ax.stock.mapper.StockRtInfoMapper;
import com.ax.stock.pojo.domain.*;
import com.ax.stock.pojo.vo.StockInfoConfig;
import com.ax.stock.pojo.vo.resp.ResponseCode;
import com.ax.stock.service.StockService;
import com.ax.stock.pojo.vo.resp.R;
import com.ax.stock.util.DateTimeUtil;
import com.ax.stock.vo.resp.PageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockServiceImpl implements StockService {


    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Autowired
    private StockBlockRtInfoMapper stockBlockRtInfoMapper;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Override
    public R<List<InnerMarketDomain>> getInnerMarketInfo() {
        // 1.获取股票的最新最新的交易时间点（精确到分钟，秒和毫秒设置为0）
//        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
//        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
//        Date curDate = curDateTime.toDate();
        // 先使用mock数据，等后续工程完成后再将代码删除即可
        Date curDate = DateTime.parse("2021-12-28 09:31:00",
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        // 2.获取大盘集合
        List<String> innerCodes = stockInfoConfig.getInner();
        System.out.println("innerCodes = " + innerCodes);
        // 3.调用mapper查询数据
        List<InnerMarketDomain> data = stockMarketIndexInfoMapper.getMarketInfo(curDate,innerCodes);
        System.out.println("data = " + data);
        // 4.封装并响应
        return R.ok(data);
    }

    @Override
    public R<List<StockBlockDomain>> getStockBlockInfo() {
        Date mockDate = DateTime.parse("2021-12-21 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();

        List<StockBlockDomain> data = stockBlockRtInfoMapper.getPlateInfo(mockDate);

        return R.ok(data);
    }

    @Override
    public R<PageResult<StockUpdownDomain>> getStockInfoByPage(int page,int pageSize) {
        // 1.获取股票的最新最新的交易时间点（精确到分钟，秒和毫秒设置为0）
//        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
//        Date curDate = DateTimeUtil.getLastDate4Stock(DateTime.now()).toDate();
//        Date curDate = curDateTime.toDate();
        // 先使用mock数据，等后续工程完成后再将代码删除即可
        Date curDate = DateTime.parse("2021-12-30 09:42:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        // 设置分页参数
        PageHelper.startPage(page,pageSize);
        // 调用mapper进行查询
        List<StockUpdownDomain> pageData = stockRtInfoMapper.getStockInfoByTime(curDate);
        // 组装配置result对象
        PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(pageData);
        PageResult<StockUpdownDomain> pageResult = new PageResult<>(pageInfo);

        return R.ok(pageResult);
    }

    @Override
    public R<List<StockUpdownDomain>> getIncreaseInfoByPage(int page, int pageSize) {
        // 先使用mock数据，等后续工程完成后再将代码删除即可
        Date curDate = DateTime.parse("2021-12-30 09:42:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
        PageHelper.startPage(page,pageSize);
        List<StockUpdownDomain> stockInfoByTime = stockRtInfoMapper.getStockInfoByTime(curDate);
        PageInfo<StockUpdownDomain> pageInfo = new PageInfo<>(stockInfoByTime);
        List<StockUpdownDomain> list = pageInfo.getList();
        return R.ok(list);

    }

    @Override
    public R<Map<String, List<Map<Integer, String>>>> getStockUpDownCount() {
        // 获取最新股票的交易时间点（截止时间）
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // 先使用mock数据，等后续工程完成后再将代码删除即可
        curDateTime = DateTime.parse("2022-01-06 14:25:00",
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = curDateTime.toDate();

        // 获取最新交易时间点对应的开盘时间点
        Date startDate = DateTimeUtil.getOpenDate(curDateTime).toDate();
        // 统计涨停数据(1涨停)
        List<Map<Integer,String>> upList = stockRtInfoMapper.getStockUpdownCount(startDate,endDate,1);
        // 统计跌停数据(0跌停)
        List<Map<Integer,String>> downList = stockRtInfoMapper.getStockUpdownCount(startDate,endDate,0);
        // 组装数据
        Map<String, List<Map<Integer, String>>> info = new HashMap<>();
        info.put("upList",upList);
        info.put("downList",downList);
        // 响应数据
        return R.ok(info);
    }

    @Override
    public void exportStockUpDownInfo(int page, int pageSize, HttpServletResponse response) {
        // 1、获取分页的数据
        R<PageResult<StockUpdownDomain>> r = this.getStockInfoByPage(page, pageSize);
        List<StockUpdownDomain> rows = r.getData().getRows();
        // 2、将数据导出到excel中
        // 这里注意 使用Swagger可能会出现问题，建议使用postman或者浏览器直接访问
        // 告知浏览器传入的数据是excel格式的文件
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            // 这里URLEncoder.encode可以防止中文乱码，和Easyexcel没有关系（还可以指定默认文件名字，这里是股票信息表）
            String fileName = URLEncoder.encode("股票信息表","UTF-8");
            response.setHeader("Content-disposition","attachment;filename="+fileName+".xlsx");
            EasyExcel.write(response.getOutputStream(),StockUpdownDomain.class).sheet("股票涨幅信息").doWrite(rows);
        } catch (Exception e) {
            log.error("当前页码：{}，每页大小：{}，当前时间点{}，异常信息：{}",
                        page,pageSize,DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),e.getMessage());
            // 通知前端异常 稍后重试
            response.setContentType("application/json");
            response.setCharacterEncoding("Utf-8");
            R<Object> error = R.error(ResponseCode.ERROR);
            try {
                String json = new ObjectMapper().writeValueAsString(error);
                response.getWriter().write(json);
            } catch (IOException ioException) {
                log.error("exportStockUpDownInfo 响应错误信息失败，时间{}",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            }
        }
    }

    @Override
    public R<Map<String, List>> getCompareStockTradeAmt() {
        // 1.获取T日  （最新的股票交易日）
        DateTime tEndDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // mock data
        tEndDateTime = DateTime.parse("2022-01-03 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date tEndDate = tEndDateTime.toDate();
        // 开盘时间
        Date tStartDate = DateTimeUtil.getOpenDate(tEndDateTime).toDate();

        // 获取T-1的时间范围
        DateTime preTEndTime = DateTimeUtil.getPreviousTradingDay(tEndDateTime);
        // mock data
        preTEndTime = DateTime.parse("2022-01-02 14:40:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date preTEndDate = preTEndTime.toDate();
        // 开盘时间
        Date preTStartDate = DateTimeUtil.getOpenDate(preTEndTime).toDate();

        // 调用mapper
        // 统计T日
        List<Map> tData = stockMarketIndexInfoMapper.getSumAmtInfo(tStartDate,tEndDate,stockInfoConfig.getInner());
        // 统计T-1
        List<Map> preTData = stockMarketIndexInfoMapper.getSumAmtInfo(preTStartDate,preTEndDate,stockInfoConfig.getInner());
        // 组装数据
        HashMap<String, List> info = new HashMap<>();
        info.put("amtList",tData);
        info.put("yesAmtList",preTData);
        return R.ok(info);
    }

    @Override
    public R<Map<String, Object>> getIncreaseRangeInfo() {
        // 获取当前最新的股票交易时间点
        DateTime curDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // mock data
        curDateTime = DateTime.parse("2022-01-06 9:55:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date curDate = curDateTime.toDate();
        // 调用mapper获取数据
        List<Map<String,Object>> infos =stockRtInfoMapper.getIncreaseRangeInfoByDate(curDate);

        // 将顺序的涨幅区间内的每个元素
        // 方式一：普通循环
/*        List<Map<String,Object>> allInfos = new ArrayList<>();
        for (String title : upDownRange) {
            Map<String,Object> tmp = null;
            for (Map<String, Object> info : infos) {
                if (info.containsValue(title)){
                    tmp = info;
                    break;
                }
            }
            if (tmp == null){
                // 不存在则补齐
                tmp = new HashMap<>();
                tmp.put("count",0);
                tmp.put("title",title);
            }
            allInfos.add(tmp);
        }*/

      /*  // 获取有序的涨幅区间标题集合
        List<String> upDownRange = stockInfoConfig.getUpDownRange();
        // 方式2：Lambda表达式
        List<Map<String, Object>> allInfos = upDownRange.stream().map(title -> {
            // Optional防止空指针
            Optional<Map<String, Object>> result = infos.stream().filter(map -> map.containsValue(title)).findFirst();
            if (result.isPresent()) {
                return result.get();
            } else {
                HashMap<String, Object> tmp = new HashMap<>();
                tmp.put("count", 0);
                tmp.put("title", title);
                return tmp;
            }
        }).collect(Collectors.toList());*/

        List<String> upDownRanges = stockInfoConfig.getUpDownRange();
        List<Map<String,Object>> allInfos = upDownRanges.stream().map(title -> {
            Optional<Map<String, Object>> find = infos.stream().filter(map -> map.containsValue(title)).findFirst();
            if (find.isPresent()) {
                return find.get();
            } else {
                HashMap<String, Object> tmp = new HashMap<>();
                tmp.put("title", title);
                tmp.put("count", 0);
                return tmp;
            }
        }).collect(Collectors.toList());


        // 组装数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("time",curDateTime.toString("yyyy-MM-dd HH:mm:ss"));
        data.put("infos",allInfos);
        // 响应
        return R.ok(data);
    }

    @Override
    public R<List<Stock4MinuteDomain>> getStockScreenTimeSharing(String code) {
        // 获取T日最新股票交易时间点 endTime openTime
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // 依旧mock数据
        endDateTime = DateTime.parse("2021-12-30 14:30:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = endDateTime.toDate();
        Date openDate = DateTimeUtil.getOpenDate(endDateTime).toDate();
        // 查询数据
        List<Stock4MinuteDomain> data = stockRtInfoMapper.getStock4MinuteInfo(openDate,endDate,code);
        // 返回
        return R.ok(data);
    }

    @Override
    public R<List<Stock4EvrDayDomain>> getStockScreenDKLine(String code) {
        // 获取统计日K线的时间范围
        // 获取截止时间
        DateTime endDateTime = DateTimeUtil.getLastDate4Stock(DateTime.now());
        // 依旧mock数据
        endDateTime = DateTime.parse("2022-06-06 14:25:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = endDateTime.toDate();
        // 获取起始时间
        // endDateTime.minusMonths(5)  endDateTime的时间往前推5个月
        DateTime startDateTime = endDateTime.minusMonths(5);
        Date startDate = startDateTime.toDate();
        // 调用mapper
        List<String> curTimes = stockRtInfoMapper.getLastCurTimes(startDate,endDate,code);
        System.out.println("curTimes = " + curTimes);
        List<Stock4EvrDayDomain> info = stockRtInfoMapper.getStockScreenDKLine(startDate,endDate,curTimes,code);
        return R.ok(info);
    }
}
