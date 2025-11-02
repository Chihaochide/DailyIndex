package com.ax.stock.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义rabbitMq相关配置
 */
@Configuration
public class MqConfig {

    /**
     * 重新定义消息序列化的方式，改为基于json格式序列化和反序列化
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    /**
     * 定义交换机-主题交换机
     * @return
     */
    @Bean
    public TopicExchange stockTopicExchange(){
        // 参数一：交换机名字；参数二：是否持久化（默认持久化）；参数三：是否自动删除
        return new TopicExchange("stockExchange",true,false);
    }

    /**
     * 定义队列
     * @return
     */
    @Bean
    public Queue innerMarketQueue(){
        // 参数一：队列名字；参数二：是否持久化
        return new Queue("innerMarketQueue",true);
    }


    /**
     * 绑定国内大盘队列到股票主题交换机
     * @return
     */
    @Bean
    public Binding bindingInnerMarketQueueToStockTopicExchange(){
        return BindingBuilder.bind(innerMarketQueue()).to(stockTopicExchange()).with("inner.Market");
    }

}
