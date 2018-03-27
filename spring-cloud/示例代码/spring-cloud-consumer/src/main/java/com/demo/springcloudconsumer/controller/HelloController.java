package com.demo.springcloudconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.demo.springcloudconsumer.client.HelloClient;

@RestController
public class HelloController {
	
	@Autowired
	private HelloClient helloClient;
	
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	public HelloController(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@GetMapping("/hi/{name}")
    public String hi(@PathVariable("name") String name) {
		kafkaTemplate.send("test", "hi!");
        return "hi";
    }

	@GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name) {
        return helloClient.hello(name);
    }
}
