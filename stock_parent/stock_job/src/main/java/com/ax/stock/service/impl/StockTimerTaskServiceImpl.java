package com.ax.stock.service.impl;

import com.ax.stock.constant.ParseType;
import com.ax.stock.mapper.StockBusinessMapper;
import com.ax.stock.mapper.StockMarketIndexInfoMapper;
import com.ax.stock.mapper.StockRtInfoMapper;
import com.ax.stock.pojo.entity.StockMarketIndexInfo;
import com.ax.stock.pojo.entity.StockRtInfo;
import com.ax.stock.pojo.vo.StockInfoConfig;
import com.ax.stock.service.StockTimerTaskService;
import com.ax.stock.util.DateTimeUtil;
import com.ax.stock.util.IdWorker;
import com.ax.stock.util.ParserStockInfoUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    @Autowired
    private StockBusinessMapper stockBusinessMapper;
    @Autowired
    private ParserStockInfoUtil parserStockInfoUtil;
    @Autowired
    private StockRtInfoMapper stockRtInfoMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // 必须保证该对象无状态
    private HttpEntity<Object> httpEntity;

    /**
     * @PostConstruct注解：在构造器执行之后自动执行这个方法
     */
    @PostConstruct
    public void initData(){
        HttpHeaders headers = new HttpHeaders();
//         防盗链
        headers.add("Referer","https://finance.sina.com.cn/stock/");
//         用户标识
        headers.add("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36");
        // 把数据和请求头封装到 【HttpEntity】中
        httpEntity = new HttpEntity<>(headers);
    }

    @Transactional(timeout = 10)
    @Override
    public void getInnerMarketInfo() {
        // 阶段一：采集原始数据
        // 1. 组装url地址  https://hq.sinajs.cn/list=sh601003,sh601002
        // String.join("以什么进行拼接",list列表)
//              stockInfoConfig.getInner() = [sh000001,sz399001]
//              https://hq.sinajs.cn/list=sh000001,sz399001
        String marketUrl = stockInfoConfig.getMarketUrl()+String.join(",",stockInfoConfig.getInner());

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
            // 大盘数据采集完毕后通知backend工程【刷新缓存】
            // 发送日期对象，接收方通过接受的日期与当前日期比对，能判断出数据延迟的时长，用于运维通知，方便后期优化
            rabbitTemplate.convertAndSend("stockExchange","inner.Market",new Date());
            log.info("当前时间点{}，插入了{}条数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
        }else {
            log.error("当前时间点{}，插入了{}条数失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void getStockRtIndex() {
        // 获取所有个股的集合（正常3000多条）
        List<String> allCodes = stockBusinessMapper.getAllStockCodes();
        // stream流 【给List集合中的数据添加前缀】   匹配6开头的数据，如果匹配到就添加前缀sh，否则添加sz
        allCodes = allCodes.stream().map(code->code.startsWith("6")?"sh"+code:"sz"+code).collect(Collectors.toList());
        // 一次性将所有集合拼接到url 会导致地址过长！参数过多 没准直接拉黑ip
//        String url = stockInfoConfig.getMarketUrl()+String.join(",",codes);
        // 把大集合分装成小集合，分批次进行拉取
        List<List<String>> partition = Lists.partition(allCodes, 15);
        long startTime = System.currentTimeMillis();
//  ============================================================================================================
        // 原始方案
        /*partition.forEach(codes->{
            // 阶段一：采集原始数据
            String marketUrl = stockInfoConfig.getMarketUrl()+String.join(",",codes);

            // 发起请求 获取实体对象
            ResponseEntity<String> responseEntity = restTemplate.exchange(marketUrl, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue()!=200) {
                // 当前请求失败
                log.error("当前时间点{}，采集数据失败，状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),
                        responseEntity.getStatusCodeValue());
                // 其他操作（发送邮件、企业微信等） 给相关运营人员提醒
                return;
            }
            // 获取原始Js格式数据
            String jsData = responseEntity.getBody();
            // 调用工具类解析
            List<StockRtInfo> entities = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
            log.info("采集个股数据");
            // todo 批量插入
            int count = stockRtInfoMapper.insertBatch(entities);
            if (count>=0 && count==entities.size()){
                log.info("当前时间点{}，插入了{}条数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
            }else {
                log.error("当前时间点{}，插入了{}条数失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
            }
            // 原始方案采集各个股票数据时将集合分片，串行采集数据，效率不高，存在较高采集延迟。引入多线程
//            new Thread(()->{});
        });*/

//  ============================================================================================================
        // 方案一：使用Java自带的多线程
        /* 代码的问题：
         * 1、每次来任务，就创建一个线程，复用性很差
         * 2、如果多线程使用不当，会造成cpu竞争过大，导致频繁上下文切换从而性能更低
         */
/*
        partition.forEach(codes->{
            new Thread(()->{
                // 阶段一：采集原始数据
                String marketUrl = stockInfoConfig.getMarketUrl()+String.join(",",codes);

                // 发起请求 获取实体对象
                ResponseEntity<String> responseEntity = restTemplate.exchange(marketUrl, HttpMethod.GET, httpEntity, String.class);
                if (responseEntity.getStatusCodeValue()!=200) {
                    // 当前请求失败
                    log.error("当前时间点{}，采集数据失败，状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),
                            responseEntity.getStatusCodeValue());
                    // 其他操作（发送邮件、企业微信等） 给相关运营人员提醒
                    return;
                }
                // 获取原始Js格式数据
                String jsData = responseEntity.getBody();
                // 调用工具类解析
                List<StockRtInfo> entities = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
                log.info("采集个股数据");
                // todo 批量插入
                int count = stockRtInfoMapper.insertBatch(entities);
                if (count>=0 && count==entities.size()){
                    log.info("当前时间点{}，插入了{}条数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
                }else {
                    log.error("当前时间点{}，插入了{}条数失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
                }
                // 原始方案采集各个股票数据时将集合分片，串行采集数据，效率不高，存在较高采集延迟。引入多线程
//            new Thread(()->{});
            }).start();
        });
*/

//  ============================================================================================================
        // 方案二：引入线程池
        partition.forEach(codes->{
            threadPoolTaskExecutor.execute(()->{

                // 阶段一：采集原始数据
                String marketUrl = stockInfoConfig.getMarketUrl()+String.join(",",codes);

                // 发起请求 获取实体对象
                ResponseEntity<String> responseEntity = restTemplate.exchange(marketUrl, HttpMethod.GET, httpEntity, String.class);
                if (responseEntity.getStatusCodeValue()!=200) {
                    // 当前请求失败
                    log.error("当前时间点{}，采集数据失败，状态码：{}", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),
                            responseEntity.getStatusCodeValue());
                    // 其他操作（发送邮件、企业微信等） 给相关运营人员提醒
                    return;
                }
                // 获取原始Js格式数据
                String jsData = responseEntity.getBody();
                // 调用工具类解析
                List<StockRtInfo> entities = parserStockInfoUtil.parser4StockOrMarketInfo(jsData, ParseType.ASHARE);
                log.info("采集个股数据");
                // todo 批量插入
                int count = stockRtInfoMapper.insertBatch(entities);
                if (count>=0 && count==entities.size()){
                    log.info("当前时间点{}，插入了{}条数据成功",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
                }else {
                    log.error("当前时间点{}，插入了{}条数失败",DateTime.now().toString("yyyy-MM-dd HH:mm:ss"),count);
                }
                // 原始方案采集各个股票数据时将集合分片，串行采集数据，效率不高，存在较高采集延迟。引入多线程
//            new Thread(()->{});
            });
        });
        long takeTime = System.currentTimeMillis() - startTime;
        log.info("本次采集花费时间：{}ms",takeTime);

    }
}
