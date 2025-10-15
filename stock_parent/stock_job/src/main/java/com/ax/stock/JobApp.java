package com.ax.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// 开启声明式事物（数据库回滚）
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.ax.stock.mapper") // 扫描持久层的mapper接口 生成代理对象，维护到spring的ioc容器中
public class JobApp {
    public static void main(String[] args) {
        SpringApplication.run(JobApp.class, args);
    }
}
