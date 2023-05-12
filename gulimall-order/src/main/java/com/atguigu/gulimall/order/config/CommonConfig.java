package com.atguigu.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CommonConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);

        log.info("rabbit confirm return ready:{}", rabbitTemplate);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            //记录日志、发送邮件通知、落库定时任务扫描重发
            log.info("correlationData:{},ack:{},cause:{}", correlationData, ack, cause);
        });
        //当消息成功发送到交换机没有路由到队列触发此监听
        rabbitTemplate.setReturnCallback(((message, replyCode, replyText, exchange, routingKey) -> {
            //记录日志、发送邮件通知、落库定时任务扫描重发
            log.info("消息发送失败,应答码{}，原因：{}，交换机{}，路由{}，消息{}",
                    replyCode, replyText, exchange, routingKey, message.toString());
        }));
    }
}
