package com.ax.stock.service;

import com.ax.stock.pojo.entity.SysUser;
import com.ax.stock.vo.req.LoginRequestVo;
import com.ax.stock.vo.resp.LoginRespVo;
import com.ax.stock.pojo.vo.resp.R;

import java.util.Map;

public interface UserService {
    SysUser findByUserName(String userName);

    R<LoginRespVo> login(LoginRequestVo vo);

    R<Map> getCaptchaCod();

}
