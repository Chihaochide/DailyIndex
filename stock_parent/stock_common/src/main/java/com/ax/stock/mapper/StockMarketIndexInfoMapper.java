package com.ax.stock.mapper;

import com.ax.stock.pojo.domain.InnerMarketDomain;
import com.ax.stock.pojo.entity.StockMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author 刘轩赫
* @description 针对表【stock_market_index_info(国内大盘数据详情表)】的数据库操作Mapper
* @createDate 2025-09-16 19:08:30
* @Entity com.ax.stock.pojo.entity.StockMarketIndexInfo
*/
public interface StockMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockMarketIndexInfo record);

    int insertSelective(StockMarketIndexInfo record);

    StockMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockMarketIndexInfo record);

    int updateByPrimaryKey(StockMarketIndexInfo record);

    List<InnerMarketDomain> getMarketInfo(@Param("curDate") Date curDate,@Param("marketCodes") List<String> marketCodes);

    /**
     * 统计指定日期范围内指定大盘每分钟成交量
     * @param openData 起始时间-开盘时间
     * @param endDate 截止时间-一般与openData同一天
     * @param marketCodes 大盘编码结合
     * @return
     */
    List<Map> getSumAmtInfo(@Param("openData") Date openData, @Param("endDate") Date endDate, @Param("marketCodes") List<String> marketCodes);


    /**
     * 批量插入大盘数据
     * @param entities 大盘实体对象集合
     * @return
     */
    int insertBatch(@Param("infos") List<StockMarketIndexInfo> entities);
}
