package com.ax.stock.config;

import com.ax.stock.pojo.vo.TaskThreadPoolInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 定义线程池配置类
 */
@Configuration
public class TaskExecutePoolConfig {

    @Autowired
    private TaskThreadPoolInfo info;

    @Bean(name="threadPoolTaskExecutor",destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(info.getCorePoolSize());
        // 设置最大线程数
        executor.setMaxPoolSize(info.getMaxPoolSize());
        // 设置空闲最大存活时间
        executor.setKeepAliveSeconds(info.getKeepAliveSeconds());
        // 设置任务队列长度
        executor.setQueueCapacity(info.getQueueCapacity());
        // 设置拒绝策略
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 将设置的参数初始化
        executor.initialize();
        return executor;
    }

}
