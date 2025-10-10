package com.ax.stock.mapper;

import com.ax.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 刘轩赫
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2025-09-16 19:08:30
* @Entity com.ax.stock.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findUserInfoByUserName(@Param("userName") String userName);
    @Select("select * from sys_user")
    List<SysUser> getAll();
}
