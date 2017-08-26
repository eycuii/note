package demo.server;

import javax.xml.ws.Endpoint;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

import demo.interceptor.ServerInInterceptor;
import demo.service.impl.DemoService;

public class DemoServer {
	
	static String address = "http://localhost:8081/cxf";

	public static void main(String[] args) {
	    
		publishJaxWsServer();
        
	}
	
	public static void publishJaxWsServer(){
		JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean(); 
	    factory.setServiceClass(DemoService.class); 
	    factory.setAddress(address); 

//	    factory.getInInterceptors().add(new ServerInInterceptor()); 
	    
//      List<Feature> features = new ArrayList<Feature>();
//      Feature f = new ServerFeature();
//      features.add(f);
//      factory.setFeatures(features);
	    
//	    Map<String,Object> inProps = new HashMap<String,Object>();
//	    inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
//		// Password type : plain text
//		inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
//		// for hashed password use:
//		//properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
//		// Callback used to retrieve password for given user.
//		inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ServerPasswordCallbackHandler.class.getName());
//	    factory.getInInterceptors().add(new WSS4JInInterceptor(inProps)); 
	    
	    Server server = factory.create(); 
	    server.start(); 
	}
	
	public static void publishEndpointServer(){
	    DemoService demoService= new DemoService();
	    Endpoint.publish(address, demoService);
	}
	
	public static void publishJAXRSServer(){
		JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();  
		factoryBean.getInInterceptors().add(new LoggingInInterceptor());  
		factoryBean.getOutInterceptors().add(new ServerInInterceptor());  
		factoryBean.setResourceClasses(DemoService.class);  
		factoryBean.setAddress(address);  
		factoryBean.create(); 
	}
}
