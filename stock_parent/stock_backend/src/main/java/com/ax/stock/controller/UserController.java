package com.ax.stock.controller;

import com.ax.stock.pojo.entity.SysUser;

import com.ax.stock.service.UserService;
import com.ax.stock.vo.req.LoginRequestVo;
import com.ax.stock.vo.resp.LoginRespVo;
import com.ax.stock.pojo.vo.resp.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
//Swagger注解
@Api(tags = "接口服务", value = "/api/v1/swagger/**")
// @RestController = @ResponseBody + @Controller
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation("根据用户名查询用户信息")
    @ApiImplicitParam(name = "userName",value = "用户名",dataType = "String",required = true,type = "path")
    @GetMapping("/user/{userName}")
    public SysUser getUserByUserName(@PathVariable("userName") String userName){

        return  userService.findByUserName(userName);
    }

    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public R<LoginRespVo> login(@RequestBody LoginRequestVo vo){
        return userService.login(vo);
    }


    /**
     * 生成图片验证码
     * @return
     */
    @GetMapping("/captcha")
    public R<Map> getCaptchaCod(){
        return userService.getCaptchaCod();
    }
}
