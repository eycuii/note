package demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.jws.WebService;

import demo.entity.User;
import demo.service.IDemoService;

@WebService
//(
//targetNamespace = "http://service.demo/", 
//serviceName = "demoService", 
//endpointInterface = "demo.service.IDemoService"
//)
public class DemoService implements IDemoService{

	//@WebMethod(exclude=true)  // 默认public方法可以发布为ws服务, 如果要排除则配置  exclude=true
	@Override
	public String say(String name) {
		return "hello " + name;
	}

	@Override
	public User getUser(int id) {
		User user = new User();
		user.setId(1);
		user.setName("admin");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("a", "aa");
		map.put("b", "bb");
		user.setEmailMap(map);
		return user;
	}

	@Override
	public User get(int id) {
		User user = new User();
		user.setId(1);
		user.setName("admin");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("a", "aa");
		map.put("b", "bb");
		user.setEmailMap(map);
		return user;
	}

	@Override
	public List<User> getList(User pUser) {
		System.out.println(pUser);
		List<User> list = new ArrayList<User>();
		User user = new User();
		user.setId(1);
		user.setName("admin");
		list.add(user);
		user = new User();
		user.setId(2);
		user.setName("admin2");
		list.add(user);
		return list;
	}

	@Override
	public HashMap<String, Object> getMap() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("a", "aa");
		map.put("b", "bb");
		User user = new User();
		user.setId(1);
		user.setName("admin");
		map.put("user", user);
		return map;
	}

}
