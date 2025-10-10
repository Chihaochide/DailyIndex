package com.ax.stock.service.impl;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.ax.stock.exception.DailyIndexException;
import com.ax.stock.mapper.SysUserMapper;
import com.ax.stock.pojo.entity.SysUser;
import com.ax.stock.service.UserService;
import com.ax.stock.util.BCrypt;
import com.ax.stock.util.IdWorker;
import com.ax.stock.vo.req.LoginRequestVo;
import com.ax.stock.vo.resp.LoginRespVo;
import com.ax.stock.pojo.vo.resp.R;
import com.ax.stock.pojo.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 股票业务
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public SysUser findByUserName(String userName) {
        SysUser userInfoByUserName = sysUserMapper.findUserInfoByUserName(userName);
        return userInfoByUserName;
    }

    @Override
    public R<LoginRespVo> login(LoginRequestVo vo) {
        if (vo==null || StringUtils.isBlank(vo.getUsername())||StringUtils.isBlank(vo.getPassword())||StringUtils.isBlank(vo.getSessionId()))
                throw new DailyIndexException(ResponseCode.DATA_ERROR);

        // 判断验证码
        String sessionId = vo.getSessionId();
        if (StringUtils.isBlank(vo.getCode())) throw new DailyIndexException(ResponseCode.CHECK_CODE_NOT_EMPTY);
        String code = redisTemplate.opsForValue().get("CK:" + sessionId)+"";
        if (!code.equalsIgnoreCase(vo.getCode())) throw new DailyIndexException(ResponseCode.CHECK_CODE_ERROR);
        LoginRespVo res = new LoginRespVo();

        // 判断user
        SysUser user = sysUserMapper.findUserInfoByUserName(vo.getUsername());
        if (user==null) throw new DailyIndexException(ResponseCode.ACCOUNT_NOT_EXISTS);
        if (!BCrypt.checkpw(vo.getPassword(),user.getPassword())) throw new DailyIndexException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);

        try {
            BeanUtils.copyProperties(res,user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return R.ok(res);
    }

    /**
     * 生成图片验证码功能
     *  hutool
     * @return
     */
    @Override
    public R<Map> getCaptchaCod() {
        // 1. 生成图片验证码
        /*
            参数1：宽度
            参数2：高度
            参数3：验证码长度
            参数4：干扰线的数量

         */
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
        captcha.setBackground(Color.ORANGE);
        // 获取校验码
        String code = captcha.getCode();
        // 获取经过base64编码处理的图片数据
        String imageData = captcha.getImageBase64();
        // 生成sessionId 转String避免精度丢失
        String sessionId = idWorker.nextId()+"";
        log.info("当前生成的图片校验码：{}，会话Id：{}",imageData,sessionId);
        // 将sessionID作为key，校验码作为value保存到redis中
        redisTemplate.opsForValue().set("CK:"+sessionId,code,1, TimeUnit.MINUTES);
        // 组装数据给前端返回
        Map<String,String> data = new HashMap();
        data.put("imageData",imageData);
        data.put("sessionId",sessionId);
        return R.ok(data);
    }
}
