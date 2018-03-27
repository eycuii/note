package com.demo.springbootkafka.msg;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MsgListener {

    @KafkaListener(topics = "test")
    public void processMessage(String content) {
        System.out.println("收到消息：" + content);
    }
}
