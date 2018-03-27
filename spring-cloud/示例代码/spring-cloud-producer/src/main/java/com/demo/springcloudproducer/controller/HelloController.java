package com.demo.springcloudproducer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
    public String hello(@PathVariable("name") String name) {
        return "Hello " + name + "~";
    }

	@GetMapping("/hi/{name}")
    public String hi(@PathVariable("name") String name) {
        return "Hi " + name + "~";
    }
}
