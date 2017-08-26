package demo.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.http.HttpStatus;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import demo.callback.ClientPasswordCallbackHandler;
import demo.service.IDemoService;

public class DemoClient {
	
	static String address = "http://localhost:8081/cxf/";
	
	public static void main(String[] args) throws HttpException, IOException { 
		
		createJaxWsClient();
		
    }
	
	public static void createJaxWsClient(){ 
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
		factory.setAddress(address); 
        factory.setServiceClass(IDemoService.class); 
        //factory.getInInterceptors().add(new GZIPInInterceptor()); //数据压缩
        //factory.getOutInterceptors().add(new ClientOutInterceptor("admin", "123123")); 
        
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        //添加用户名
        outProps.put(WSHandlerConstants.USER, "admin");
        outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientPasswordCallbackHandler.class.getName());
        factory.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));
        
        IDemoService demoService = (IDemoService) factory.create(); 
        String msg = demoService.say("world"); 
        System.out.println(msg); 

	}
	
	public static void createRestfulClient(){
		try {
		    HttpClient client = new HttpClient();
		    GetMethod method = new GetMethod(address + "user/1");  
			int statusCode = client.executeMethod(method);
		    if (statusCode != HttpStatus.SC_OK) {  
		        System.err.println("Method failed: " + method.getStatusLine());  
		    }  
		    byte[] responseBody = method.getResponseBody();  
		    System.out.println(new String(responseBody));
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public static void createJaxWsDynamicClient(){

	    JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
	    Client client = dcf.createClient("http://localhost:8081/cxf?wsdl", new QName("http://service.demo/","demoService"));
//      Object user = Thread.currentThread().getContextClassLoader().loadClass("demo.entity.User").newInstance();
//      Method m = user.getClass().getMethod("get", Integer.class);
//      m.invoke(user, 1);
       
	    try {
			Object[] resultArr = client.invoke("say", "world");
			System.out.println(resultArr.length + "," + resultArr[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
