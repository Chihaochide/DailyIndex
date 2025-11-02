package com.ax.stock.config;

import com.ax.stock.pojo.vo.TaskThreadPoolInfo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(TaskThreadPoolInfo.class)
public class TaskThreadPoolInfoConfig {
}
