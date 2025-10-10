package com.ax.stock.mapper;

import com.ax.stock.pojo.entity.SysLog;

/**
* @author 刘轩赫
* @description 针对表【sys_log(系统日志)】的数据库操作Mapper
* @createDate 2025-09-16 19:08:30
* @Entity com.ax.stock.pojo.entity.SysLog
*/
public interface SysLogMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysLog record);

    int insertSelective(SysLog record);

    SysLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysLog record);

    int updateByPrimaryKey(SysLog record);

}
