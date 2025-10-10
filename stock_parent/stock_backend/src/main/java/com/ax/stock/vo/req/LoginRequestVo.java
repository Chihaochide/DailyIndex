package com.ax.stock.vo.req;

import lombok.Data;

@Data
public class LoginRequestVo {
    private String username;
    private String password;
    private String sessionId;
    private String code;
}
