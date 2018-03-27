package com.demo.springcloudconsumer.hystrix;

import org.springframework.stereotype.Component;

import com.demo.springcloudconsumer.client.HelloClient;

@Component
public class HelloClientHystrix implements HelloClient {

	@Override
	public String hello(String name) {
		return "hello(name) 服务失败！";
	}

}
