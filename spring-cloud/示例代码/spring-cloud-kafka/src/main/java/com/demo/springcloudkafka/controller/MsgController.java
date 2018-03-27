package com.demo.springcloudkafka.controller;

import com.demo.springcloudkafka.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MsgController {

    @Autowired
    private SendService service;

    @RequestMapping(value = "/msg/{content}", method = RequestMethod.GET)
    public String hello(@PathVariable("content") String content) {
        return "hello " + service.sendMessage(content);
    }
}
