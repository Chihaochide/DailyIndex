package com.ax.stock.service.impl;

import com.ax.stock.mapper.StockMarketIndexInfoMapper;
import com.ax.stock.pojo.entity.StockMarketIndexInfo;
import com.ax.stock.pojo.vo.StockInfoConfig;
import com.ax.stock.service.StockTimerTaskService;
import com.ax.stock.util.DateTimeUtil;
import com.ax.stock.util.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class StockTimerTaskServiceImpl implements StockTimerTaskService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockInfoConfig stockInfoConfig;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private StockMarketIndexInfoMapper stockMarketIndexInfoMapper;
    @Transactional(timeout = 10)
    @Override
    public void getInnerMarketInfo() {
        // 阶段一：采集原始数据
        // 1. 组装url地址  https://hq.sinajs.cn/list=sh601003,sh601002
        // String.join("以什么进行拼接",list列表)
//              stockInfoConfig.getInner() = [sh000001,sz399001]
//              https://hq.sinajs.cn/list=sh000001,sz399001
        String marketUrl = stockInfoConfig.getMarketUrl()+String.join(",",stockInfoConfig.getInner());
        // 1.2 维护请求头 添加防盗链和用户标识
        HttpHeaders headers = new HttpHeaders();
//         防盗链
        headers.add("Referer","https://finance.sina.com.cn/stock/");
//         用户标识
        headers.add("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        // 把数据和请求头封装到 【HttpEntity】中
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        // 发起请求 获取实体对象
        ResponseEntity<String> responseEntity = restTemplate.exchange(marketUrl, HttpMethod.GET, httpEntity, String.class);
        if (responseEntity.getStatusCodeValue()!=200) {
             // 当前请求失败
            log.error("当前时间点{}，采集数据失败，状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),
                    responseEntity.getStatusCodeValue());
            // 其他操作（发送邮件、企业微信等） 给相关运营人员提醒
            return;
        }
        // 获取js格式数据
        String jsData = responseEntity.getBody();

        // 解析原始数据
        String regex = "var hq_str_(.+)=\"(.+)\";";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsData);
        List<StockMarketIndexInfo> entities = new ArrayList<>();
        while (matcher.find()){
            // 获取大盘编码
            String marketCode = matcher.group(1);
            // 获取其他信息
            String other = matcher.group(2);
            // 将other字符串以 ， 进行分割
//            List<String> getList = Arrays.asList(",", other);
            String[] splitArr = other.split(",");
            //大盘名称
            String marketName=splitArr[0];
            //获取当前大盘的开盘点数
            BigDecimal openPoint=new BigDecimal(splitArr[1]);
            //前收盘点
            BigDecimal preClosePoint=new BigDecimal(splitArr[2]);
            //获取大盘的当前点数
            BigDecimal curPoint=new BigDecimal(splitArr[3]);
            //获取大盘最高点
            BigDecimal maxPoint=new BigDecimal(splitArr[4]);
            //获取大盘的最低点
            BigDecimal minPoint=new BigDecimal(splitArr[5]);
            //获取成交量
            Long tradeAmt=Long.valueOf(splitArr[8]);
            //获取成交金额
            BigDecimal tradeVol=new BigDecimal(splitArr[9]);
            //时间 格式化一下
            Date curTime = DateTime.parse(splitArr[30] + " " + splitArr[31], DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
            // 封装解析完成的数据
            StockMarketIndexInfo entity = StockMarketIndexInfo.builder()
                    .id(idWorker.nextId())
                    .marketName(marketName)
                    .openPoint(openPoint)
                    .preClosePoint(preClosePoint)
                    .curPoint(curPoint)
                    .maxPoint(maxPoint)
                    .minPoint(minPoint)
                    .tradeAmount(tradeAmt)
                    .tradeVolume(tradeVol)
                    .marketCode(marketCode)
                    .curTime(curTime).build();
            entities.add(entity);
        }
        log.info("解析数据完毕");
        System.out.println("entities = " + entities);
        // 调用mybatis批量入库
        int count = stockMarketIndexInfoMapper.insertBatch(entities);
        System.out.println("cont = " + count);
        System.out.println("entities.size() = " + entities.size());
        if (count>=0 && count==entities.size()){
            log.info("当前时间点{}，插入了{}条数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
        }else {
            log.error("当前时间点{}，插入了{}条数失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
        }
    }
}
