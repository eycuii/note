package com.demo.springcloudconsumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.springcloudconsumer.hystrix.HelloClientHystrix;

@FeignClient(name= "spring-cloud-producer", fallback = HelloClientHystrix.class)
public interface HelloClient {
	
    @GetMapping(value = "/hello/{name}")
    public String hello(@PathVariable(value = "name") String name);
}

