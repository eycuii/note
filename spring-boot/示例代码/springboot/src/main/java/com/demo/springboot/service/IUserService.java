package com.demo.springboot.service;

import com.demo.springboot.entities.User;

public interface IUserService {

	User getById(int id);
	
	int update(User user);
}
