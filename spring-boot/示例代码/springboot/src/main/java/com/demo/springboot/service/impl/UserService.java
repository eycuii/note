package com.demo.springboot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.springboot.dao.IUserDao;
import com.demo.springboot.entities.User;
import com.demo.springboot.service.IUserService;

@Service
public class UserService implements IUserService {

	@Autowired
    private IUserDao userDao;
	
	@Override
	public User getById(int id) {
		return userDao.getUserById(id);
	}

	@Transactional
	@Override
	public int update(User user) {
		return userDao.update(user);
	}

}
