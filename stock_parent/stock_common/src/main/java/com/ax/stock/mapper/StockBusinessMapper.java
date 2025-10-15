package com.ax.stock.mapper;

import com.ax.stock.pojo.entity.StockBusiness;

import java.util.List;

/**
* @author 刘轩赫
* @description 针对表【stock_business(主营业务表)】的数据库操作Mapper
* @createDate 2025-09-16 19:08:30
* @Entity com.ax.stock.pojo.entity.StockBusiness
*/
public interface StockBusinessMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBusiness record);

    int insertSelective(StockBusiness record);

    StockBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBusiness record);

    int updateByPrimaryKey(StockBusiness record);

    /**
     * 获取所有A股股票编码集合
     * @return
     */
    List<String> getAllStockCodes();
}
