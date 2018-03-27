package com.demo.springboot.dao;

import org.apache.ibatis.annotations.Param;
import com.demo.springboot.entities.User;

public interface IUserDao {
	
	User getUserById(@Param("id") int id);
	
	int update(User user);
}
