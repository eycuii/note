package com.demo.springbootkafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@GetMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name) {
		kafkaTemplate.send("test", "springboot-key",  "springboot-hello "+ name +"~");
        return "hello " + name;
    }
}
