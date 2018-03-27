package com.demo.springcloudkafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
public class SendService {

    @Autowired
    private Source source;

    public boolean sendMessage(String msg) {
        return source.output().send(
                MessageBuilder.withPayload(msg)
                        .setHeader("partitionKey", "springcloud-key").build());
    }
}
