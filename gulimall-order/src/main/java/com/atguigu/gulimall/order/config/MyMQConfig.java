package com.atguigu.gulimall.order.config;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {
    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.printf("rrrrri:{}" + order);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        //60s for quick test
        arguments.put("x-message-ttl", 60000);
        /*
            Queue(String name,  队列名字
            boolean durable,  是否持久化
            boolean exclusive,  是否排他
            boolean autoDelete, 是否自动删除
            Map<String, Object> arguments) 属性
         */
        return new Queue("order.delay.queue", true, false, false, arguments);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue", true, false, false);
    }

    @Bean
    public TopicExchange orderEventExchange() {
//        TopicExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        return new TopicExchange("order-event-exchange", true, false);
    }


    @Bean
    public Binding orderCreateBinding() {
        //todo builder or new
//        return BindingBuilder.bind(orderDelayQueue()).to(orderEventExchange()).with("order.create.order");
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE
                , "order-event-exchange"
                , "order.create.order", null);
    }


    @Bean
    public Binding orderReleaseBinding() {
        return BindingBuilder.bind(orderReleaseOrderQueue()).to(orderEventExchange()).with("order.release.order");
    }


}
