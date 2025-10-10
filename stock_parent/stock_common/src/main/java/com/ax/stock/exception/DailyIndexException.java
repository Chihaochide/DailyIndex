package com.ax.stock.exception;

import com.ax.stock.pojo.vo.resp.ResponseCode;
import lombok.Getter;

@Getter
public class DailyIndexException extends RuntimeException {
    private Integer status; // 状态码

    public DailyIndexException(Integer status,String msg){
        super(msg);
        this.status = status;
    }

    public DailyIndexException(ResponseCode msg){
        super(msg.getMessage());
        this.status = msg.getCode();
    }


}
