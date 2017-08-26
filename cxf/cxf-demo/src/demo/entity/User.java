package demo.entity;

import java.util.HashMap;

//@XmlRootElement(name="User")
public class User {

	int id;
	
	String name;
	
	HashMap<String, Object> emailMap;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Object> getEmailMap() {
		return emailMap;
	}

	public void setEmailMap(HashMap<String, Object> emailMap) {
		this.emailMap = emailMap;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", emailMap=" + emailMap
				+ "]";
	}
}
