package demo.interceptor;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;

public class ServerInInterceptor extends AbstractPhaseInterceptor<SoapMessage>{
	
	public ServerInInterceptor() {
        //定义拦截器阶段
        super(Phase.INVOKE);
    }

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		System.out.println("server handleMessage=====");
		Header header = message.getHeader(new QName("auth"));
		if(header == null){
        	throw new Fault(new IllegalArgumentException("没有头信息"));
		}
        Element appleEle=(Element) header.getObject();
        String username = appleEle.getElementsByTagName("username").item(0).getTextContent();
        String password = appleEle.getElementsByTagName("password").item(0).getTextContent();
        if ("admin".equals(username)&&"123123".equals(password)) {
            System.out.println("通过拦截器");
            return;
        } else {
        	throw new Fault(new IllegalArgumentException("用户名密码错误"));
        }
	}
	
	/**
     * @Description:handleMessage异常后执行
     * @param message
     */
    @Override
    public void handleFault(SoapMessage message) {
		System.out.println("handleFault=====");
        super.handleFault(message);
    }
}
