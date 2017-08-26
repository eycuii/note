package demo.service;

import java.util.HashMap;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import demo.entity.User;

@WebService
//@Path(value = "/demoservice")   
//@Produces("application/json")
//@Produces("application/xml")
public interface IDemoService {
	
	String say(@WebParam(name="name")String name);
	
	//@XmlJavaTypeAdapter(MapAdapter.class)
	//Map<String, Object> getMap();
	HashMap<String, Object> getMap();
	
	@GET  
    @Path(value = "/user/{id}")
	User getUser(@PathParam("id")int id);
	
	User get(int id);
	
	List<User> getList(User pUser);
}
