# cxf 学习笔记

cxf 版本：3.1.12

jdk 版本：1.7

使用的 Frontend ：JAX-WS

​    

### 1. Hello World

- 服务接口

```java
@WebService
public interface IDemoService {
	String say(@WebParam(name="name")String name);
}
```

- 服务接口实现类

```java
@WebService
public class DemoService implements IDemoService{
  	
  	//@WebMethod(exclude=true) // 默认public方法可以发布为ws服务, 如果要排除则配置 exclude=true
	@Override
	public String say(String name) {
		return "hello " + name;
	}
}
```

- 服务端

```java
JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 
factory.setServiceClass(DemoService.class); 
Server server = factory.create(); 
server.start();
```

- 客户端

```java
JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 
factory.setServiceClass(IDemoService.class); 
IDemoService demoService = (IDemoService) factory.create(); 
System.out.println(demoService.say("world")); 
```

​    

### 2. Map 对象的传递

Map 对象需要以 `HashMap` 类型来传，不能直接用 Map 接口类型，否则会返回 null 。

或者也可以自定义用于转换的类（`JAXB` 数据绑定）：

- MapConvertor

```java
@XmlType(name = "MapConvertor")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapConvertor {
  
    private List<MapEntry> entries = new ArrayList<MapEntry>();
  
    public void addEntry(MapEntry entry) {  
        entries.add(entry);  
    }

    public List<MapEntry> getEntries() {  
        return entries;  
    }  

    public static class MapEntry {  

        private String key;  

        private Object value;  

        public MapEntry() {  
            super();  
        }  

        public MapEntry(Map.Entry<String, Object> entry) {  
            super();  
            this.key = entry.getKey();  
            this.value = entry.getValue();  
        }  

        public MapEntry(String key, Object value) {  
            super();  
            this.key = key;  
            this.value = value;  
        }  

        // getters, setters...
    }  
}
```

- MapAdapter

```java
public class MapAdapter extends XmlAdapter<MapConvertor, Map<String, Object>> {  
  
    @Override  
    public MapConvertor marshal(Map<String, Object> map) throws Exception {  
        MapConvertor convertor = new MapConvertor();  
        for (Map.Entry<String, Object> entry : map.entrySet()) {  
            MapConvertor.MapEntry e = new MapConvertor.MapEntry(entry);  
            convertor.addEntry(e);  
        }  
        return convertor;  
    }  
  
    @Override  
    public Map<String, Object> unmarshal(MapConvertor map) throws Exception {  
        Map<String, Object> result = new HashMap<String, Object>();  
        for (MapConvertor.MapEntry e : map.getEntries()) {  
            result.put(e.getKey(), e.getValue());  
        }  
        return result;  
    }  
}
```

- 服务接口（需要在方法上加 `@XmlJavaTypeAdapter(MapAdapter.class)` ）

```java
@WebService
public interface IDemoService {
  
	@XmlJavaTypeAdapter(MapAdapter.class)
	Map<String, Object> getMap();
}
```

​    

### 3. 拦截器

cxf 有提供一些拦截器，其大部分默认添加到`拦截器链`中，有些拦截器也可以手动添加，如日志拦截器、数据压缩拦截器等。每当服务被调用时，会经过拦截器链的多个拦截器。（注：拦截器是线程不安全的）

**拦截器分为：**服务端输入、输出拦截器和客户端输入、输出拦截器。



#### 阶段

拦截器链有多个阶段（`Phase`），每个阶段都可以有多个拦截器。

- 输入拦截器的阶段

| 阶段名称                     | 说明                                       |
| :----------------------- | :--------------------------------------- |
| RECEIVE                  | Transport level processing（接收阶段，传输层处理）   |
| (PRE/USER/POST)_STREAM   | Stream level processing/transformations（流处理/转换阶段） |
| READ                     | This is where header reading typically occurs（SOAPHeader读取） |
| (PRE/USER/POST)_PROTOCOL | Protocol processing, such as JAX-WS SOAP handlers（协议处理阶段，例如JAX-WS的Handler处理） |
| UNMARSHAL                | Unmarshalling of the request（SOAP请求解码阶段） |
| (PRE/USER/POST)_LOGICAL  | Processing of the umarshalled request（SOAP请求解码处理阶段） |
| PRE_INVOKE               | Pre invocation actions（调用业务处理之前进入该阶段）    |
| INVOKE                   | Invocation of the service（调用业务阶段）        |
| POST_INVOKE              | Invocation of the outgoing chain if there is one（提交业务处理结果，并触发输入连接器） |

- 输出拦截器的阶段

| 阶段名称                    | 说明                                       |
| :---------------------- | :--------------------------------------- |
| SETUP                   | Any set up for the following phases（设置阶段） |
| (PRE/USER/POST)_LOGICAL | Processing of objects about to marshalled |
| PREPARE_SEND            | Opening of the connection（消息发送准备阶段，在该阶段创建Connection） |
| PRE_STREAM              | 流准备阶段                                    |
| PRE_PROTOCOL            | Misc protocol actions（协议准备阶段）            |
| WRITE                   | Writing of the protocol message, such as the SOAP Envelope.（写消息阶段） |
| MARSHAL                 | Marshalling of the objects               |
| (USER/POST)_PROTOCOL    | Processing of the protocol message       |
| (USER/POST)_STREAM      | Processing of the byte level message（字节处理阶段，在该阶段把消息转为字节） |
| SEND                    | 消息发送                                     |



#### 自定义拦截器

自定义拦截器需要继承 `AbstractPhaseInterceptor` ，或者其子类如 `AbstractSoapInterceptor`。

以下例子是一个用于身份验证的拦截器。客户端传用户名密码，服务端收到后进行验证，若验证不通过则抛异常并不会调用服务。

- 客户端输出拦截器

```java
public class ClientOutInterceptor extends AbstractPhaseInterceptor<SoapMessage>{ 
	
	private String username;  
	private String password;  
 
    public ClientOutInterceptor(String username, String password) {  
        //定义拦截器阶段
        super(Phase.PREPARE_SEND);  
        this.username = username;  
        this.password = password;  
    } 

    public void handleMessage(SoapMessage message) throws Fault {
        List<Header> headers = message.getHeaders();  
          
        Document doc = DOMUtils.createDocument();  
          
        Element auth = doc.createElement("auth");  
        Element username = doc.createElement("username");  
        Element password = doc.createElement("password");  
          
        username.setTextContent(this.username);  
        password.setTextContent(this.password);  
          
        auth.appendChild(username);  
        auth.appendChild(password);  
          
        headers.add(0, new Header(new QName("auth"),auth));
    }  
  
  	//在 handleMessage 异常后执行
    public void handleFault(SoapMessage message) {
    }
  
    public void setUsername(String username) {  
        this.username = username;  
    }  
  
    public void setPassword(String password) {  
        this.password = password;  
    }  
}
```

- 服务端输入拦截器

```java
public class ServerInInterceptor extends AbstractPhaseInterceptor<SoapMessage>{
	
	public ServerInInterceptor() {
        //定义拦截器阶段
        super(Phase.INVOKE);
    }

	public void handleMessage(SoapMessage message) throws Fault {
		Header header = message.getHeader(new QName("auth"));
		if(header == null){
        	throw new Fault(new IllegalArgumentException("没有头信息"));
		}
        Element appleEle=(Element) header.getObject();
        String username = appleEle.getElementsByTagName("username").item(0).getTextContent();
        String password = appleEle.getElementsByTagName("password").item(0).getTextContent();
        if ("admin".equals(username) && "123123".equals(password)) {
            return;
        } else {
        	throw new Fault(new IllegalArgumentException("用户名密码错误"));
        }
	}
	
    public void handleFault(SoapMessage message) {
        super.handleFault(message);
    }
}
```

- 服务端添加输入拦截器

```java
JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 
factory.setServiceClass(DemoService.class); 
factory.getInInterceptors().add(new ServerInInterceptor()); 
Server server = factory.create(); 
server.start();
```

- 客户端添加输出拦截器

```java
JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 
factory.setServiceClass(IDemoService.class); 
factory.getOutInterceptors().add(new ClientOutInterceptor("admin", "123123")); 
IDemoService demoService = (IDemoService) factory.create(); 
System.out.println(demoService.say("world")); 
```

​    

### 4. Feature

Feature 能定制服务端，客户端或 Bus ，通常添加功能。

例如，在自定义 Feature 里添加一些拦截器，服务端或客户端就可以直接配置该 Feature ，从而避免每次一个个去添加。

自定义一个 Feature 只需要继承 `AbstractFeature` 类并重写 `initializeProvider` 方法。

自定义一个服务端的 Feature ：

```java
public class ServerFeature extends AbstractFeature {
	
	@Override  
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {  
    	//添加拦截器
        provider.getInInterceptors().add(new ServerInInterceptor());  
    }
}
```

服务端添加该 Feature ：

```java
JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 
List<Feature> features = new ArrayList<Feature>();
Feature f = new ServerFeature();
features.add(f);
factory.setFeatures(features);
factory.setServiceClass(DemoService.class); 
Server server = factory.create(); 
server.start();
```

​    

### 5. RESTful 服务

#### JAX-RS 

> JAX-RS 是 Java 提供用于开发 RESTful Web 服务基于注解 ( annotation ) 的 API 。JAX-RS 旨在定义一个统一的规范，使得 Java 程序员可以使用一套固定的接口来开发 REST 应用，避免了依赖第三方框架。同时 JAX-RS 使用 POJO 编程模型和基于注解的配置并集成 JAXB ，可以有效缩短 REST 应用的开发周期。JAX-RS 只定义 RESTful API ，具体实现由第三方提供，如 Jersey 、Apache CXF 等。

**JAX-RS 常用注解：**

> - @Path：标注资源类或方法的相对路径。
>
> - @GET、@PUT、@POST、@DELETE：标注方法的HTTP请求类型。
>
> - @Produces：标注返回的MIME媒体类型。
>
> - @Consumes：标注可接受请求的MIME媒体类型。
>
> - @PathParam、@QueryParam、@HeaderParam、@CookieParam、@MatrixParam、@FormParam：标注方法的参数来自于HTTP请求的位置。
>
>   @PathParam来自于URL的路径，@QueryParam来自于URL的查询参数，@HeaderParam 来自于HTTP请求的头信息，@CookieParam 来自于HTTP请求的Cookie。



#### RESTful 服务例子

- 服务接口

```java
@WebService
public interface IDemoService {
	
	@GET  
    @Path(value = "/user/{id}")
	User getUser(@PathParam("id")int id);
}
```

- 服务接口实现类

```java
@WebService
public class DemoService implements IDemoService{
	@Override
	public User getUser(int id) {
		User user = new User();
		user.setId(id);
		user.setName("admin");
		return user;
	}
}
```

- User 类

```java
@XmlRootElement(name="User")
public class User {

	int id;
	String name;

	// getters, setters, toString...
}
```

- 服务端

```java
JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();  
factoryBean.setResourceClasses(DemoService.class);  
factoryBean.setAddress("http://localhost:8081/cxf");  
factoryBean.create();
```

- 客户端

```java
HttpClient client = new HttpClient();
GetMethod method = new GetMethod("http://localhost:8081/cxf/user/1");  
int statusCode = client.executeMethod(method);  
if (statusCode != HttpStatus.SC_OK) {  
	System.err.println("Method failed: " + method.getStatusLine());  
}  
byte[] responseBody = method.getResponseBody();  
String result = new String(responseBody);
System.out.println(result);
```

​    

### 6. WS-Security

在 cxf 中实现服务端或者客户端的 `WS-Security` ，需要添加 `WSS4J` 拦截器。

- 服务端

```java
JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 

//添加 WSS4J 拦截器
Map<String,Object> inProps = new HashMap<String,Object>();
inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
// Password type : plain text
inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
// for hashed password use:
//properties.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
// Callback used to retrieve password for given user.
inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
            ServerPasswordCallbackHandler.class.getName());
factory.getInInterceptors().add(new WSS4JInInterceptor(inProps)); 

factory.setServiceClass(DemoService.class); 
Server server = factory.create(); 
server.start();
```

- ServerPasswordCallbackHandler

```java
public class ServerPasswordCallbackHandler implements CallbackHandler {
 
    public void handle(Callback[] callbacks) throws IOException, 
        UnsupportedCallbackException {
 
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
 
        if (pc.getIdentifier().equals("admin")) {
          	// setPassword 时会自动验证客户端传过来的密码是否相同，
          	// 不同则抛异常，不能调用服务方法。
            pc.setPassword("123123");
        }
    }
}
```

- 客户端

```java
JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
factory.setAddress("http://localhost:8081/cxf"); 

//添加 WSS4J 拦截器
Map<String, Object> outProps = new HashMap<String, Object>();
outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
//设置用户名
outProps.put(WSHandlerConstants.USER, "admin");
outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
             ClientPasswordCallbackHandler.class.getName());
factory.getOutInterceptors().add(new WSS4JOutInterceptor(outProps));

factory.setServiceClass(IDemoService.class); 
IDemoService demoService = (IDemoService) factory.create(); 
System.out.println(demoService.say("world"));
```

- ClientPasswordCallbackHandler

```java
public class ClientPasswordCallbackHandler implements CallbackHandler {
 
    public void handle(Callback[] callbacks) throws IOException, 
        UnsupportedCallbackException {

        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
 
        // set the password for our message.
        pc.setPassword("123123");
    }
}
```

​    

### 7. 客户端动态调用

在 1. Hello World 中的例子基础上进行修改。

- 服务接口实现类

```java
@WebService(
		targetNamespace = "http://service.demo/", 
		serviceName = "demoService", 
		endpointInterface = "demo.service.IDemoService")
public class DemoService implements IDemoService{
	//...
}
```

- 客户端

```java
try {
	JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
	Client client = dcf.createClient("http://localhost:8081/cxf?wsdl", 
                                     new QName("http://service.demo/","demoService"));
	Object[] resultArr = client.invoke("say", "world");
	System.out.println(resultArr[0]);
} catch (Exception e) {
	e.printStackTrace();
}
```

