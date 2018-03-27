package com.demo.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.springboot.entities.User;
import com.demo.springboot.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private IUserService userService;
	
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User get(@PathVariable("id") int id) {
    	User user = userService.getById(id);
        return user;
    }
}
