package com.ax.stock.config;

import com.ax.stock.util.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public IdWorker idWorker(){
        /**
         * 参数1：机器id
         * 参数2：机房id
         * 机房和机器编号一般由运维做
         */
        return new IdWorker(2L,3L);
    }

}
