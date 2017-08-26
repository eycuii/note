package demo.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

import demo.interceptor.ServerInInterceptor;

public class ServerFeature extends AbstractFeature {
	
	@Override  
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {  
		
        provider.getInInterceptors().add(new ServerInInterceptor());  
    }
}
