# 第9章 Servlet 工作原理解析

Servlet 容器如何工作（以 Tomcat 为例）；

Web 工程在 Servlet 容器如何启动；

Servlet 容器如何解析 web.xml 中定义的 Servlet；

用户的请求如何被分配给指定的 Servlet；

Servlet 容器如何管理 Servlet 生命周期；

​    

## 从 Servlet 容器说起

### Servlet 容器的启动过程

一个 Web 应用对应一个 Context 容器，也就是 Servlet 运行时的 Servlet 容器。

Tomcat 有启动类 org.apache.catalina.startup.Tomcat。创建一个该实例对象并调用其 start 方法就会启动 Tomcat。

向 Tomcat 添加一个 Web 应用时会创建一个 StandardContext 容器来设置一些参数，其中的 ContextConfig 配置负责解析 Web 应用的配置。

（书中有贴 Tomcat 主要类的启动时序图）

Tomcat 的启动是基于观察者模式的，所有容器都会继承 Lifecycle 接口，它管理这容器的整个生命周期，所有容器的修改、状态的改变都会由它去通知已经注册的观察者。如 Context 的 state 改为 init 时 ContextConfig 作为观察者将会被通知，并触发 ContextConfig.lifecycleEvent 方法。

### Web 应用的初始化工作

ContextConfig 的 configureStart 方法中进行初始化工作。主要是解析 web.xml 文件。

Tomcat 首先会找 globalWebXml（org/apache/catalina/startup/NO_DEFAULT_XML 或 conf/web.xml），然后找 hostWebXml，再找应用的 WEB-INF/web.xml。web.xml 文件中的各个配置项会被解析成相应的属性保存在 WebXml 对象中。

接下来会将 WebXml 对象的属性设置到 Context 容器中，这里包括创建 Servlet 对象（其实是 Tomcat 里的包装类 `StandardWrapper`，里面有 Tomcat 容器的特征。Servlet 是一个 Web 开发标准，所以为了避免耦合 Tomcat 并没有直接创建 Servlet 对象）、filter、listener 等，这段代码在 WebXml 的 configureContext 方法中。

所以 Context 容器才是真正运行 Servlet 的 Servlet 容器。一个 Web 应用对应一个 Context 容器。

​    

## 创建 Servlet 实例

解析 Servlet 时会包装成 StandardWrapper 添加到 Context 容器中，但这时并没有被实例化，所以还不能为我们工作。

### 创建 Servlet 对象

如果 Servlet 的 `load-on-startup` 值大于 0，那么在 Context 容器启动时就会被实例化。

前面提到的解析配置文件时会读取默认的 globalWebXml，在 conf/web.xml 文件中定义了一些默认的配置项，其中定义的两个 Servlet：org.apache.catalina.servlets.DefaultServlet 和 org.apache.jasper.servlet.JapServlet。它们的 load-on-startup 分别是 1 和 3，也就是当 Tomcat 启动时这两个 Servlet 就会被启动。

创建 Servlet 实例的方法从 `Wrapper.loadServlet` 开始。loadServlet 方法会获取 servletClass，然后把它交给 InstanceManager 去创建一个基于 servletClass.class 的对象。如果这个 Servlet 配置了 jsp-file，那么这个 servletClass 就是在 conf/web.xml 中定义的 org.apache.jasper.servlet.JspServlet 了。

### 初始化 Servlet

初始化 Servlet 在 StandardWrapper 的 initServlet 方法中，里面就是调用 Servlet 的 init() 方法，同时把包装了 StandardWrapper 对象的 StandardWrapperFacade 作为 ServletConfig 传给 Servlet（为何要传这个会在后面解释）。

如果该 Servlet 关联的是一个 JSP 文件，那么前面初始化的就是 JspServlet，接下来会模拟一次简单请求，请求调用这个 JSP 文件，以便编译这个 JSP 文件为类，并初始化这个类。

（书中有贴大致完整的初始化 Servlet 的时序图）

​    

## Servlet 体系结构

Servlet 本身如何运转。

Servlet 顶层类：Servlet、ServletConfig、ServletContext、ServletRequest、ServletResponse。

ServletConfig、ServletRequest、ServletResponse 都是由容器传递给 Servlet 的。其中，ServletConfig 在 Servlet 初始化时传给 Servlet，后两个是请求达到时调用 Servlet 传递过来的。

Servlet 的运行模式是一个“握手型的交互式”运行模式，即：两个模块为了交换数据通常都会准备一个交易场景，这个场景一直跟随这个交易过程直到这个交易完成为止。这个交易场景的初始化是根据这次交易对象指定的参数（配置类）来定制的。Servlet 中的交易场景就由 ServletContext 来描述，而定制的参数集合就由 ServletConfig 来描述，ServletRequest、ServletResponse 就是要交互的具体对象。

**ServletConfig**：

StandardWrapper、StandardWrapperFacade 都实现了 ServletConfig 接口。StandardWrapperFacade 通过 StandardWrapper 对象初始化，并保存该 StandardConfig 对象，从而保证 StandardWrapperFacade 只会提供 ServletConfig 中所规定的数据，而不会暴露 StandardWrapper 中的其他信息。传给 Servlet 对象的 ServletConfig 实际上就是 StandardWrapperFacade 对象。

**ServletContext**：

也跟 ServletConfig 一样拥有类似的结构。在 Servlet 中拿到的 ServletContext 对象实际是 ApplicationContextFacade 对象。

通过 ServletContext 可以拿到 Context 容器中的一些信息，如应用的工作路径等。

**ServletRequest、ServletResponse**：

通常使用的 HttpServletRequest、HttpServletResponse 继承了 ServletRequest、ServletResponse。为何 Context 容器传过来的 ServletRequest、ServletResponse 被转化为 HttpServletRequest、HttpServletResponse 呢？

Tomcat 接收到请求时会先创建 org.apache.coyote.Request、Response 对象，作用是经过简单解析后将这个请求快速分配给后续线程去处理。接下来当交给一个用户线程去处理这个请求时会创建 org.apache.catalina.connector.Request、Response 对象，这两个对象会一直贯穿整个 Servlet 容器直到要传给 Servlet。这里传给 Servlet 的是 Request、Response 的门面类 RequestFacade、ResponseFacade（这样的目的与上面 ServletConfig 一样：封装数据）。

​    

## Servlet 如何工作

Servlet 如何被调用。

用户从浏览器向服务器发起的一个请求 hostname:port/contextpath/servletpath：hostname、port 用来与服务器建立 TCP 连接，后面的 URL 才用来选择在服务器中哪个子容器服务用户的请求。这里服务器是如何根据这个 URL 来达到正确的 Servlet 容器中的呢？

在 Tomcat 7 中，这种映射工作由 org.apache.tomcat.util.http.**Mapper** 来完成。这个类保存了 Tomcat 的 Container 容器中的所有子容器的信息。org.apache.catalina.connector.Request 类在进入 Container 容器之前，Mapper 会根据这次请求的 hostname、contextpath 将 host、context 容器设置到 Request 的 mappingData 属性中。所以当 Request 进入 Container 容器之前，它要访问哪个子容器是已经确定了的。

Mapper 如何拥有容器的完整映射关系？

Tomcat 在 start() 方法里初始化 MapperListener 时，**MapperListener** 作为一个监听者，注册到整个 Container 容器的每个子容器中，使只要任何一个容器有发生变化，都会通知 MapperListener。

Request 经过 Filter 链、通知 web.xml 中的 listener 之后，会执行 Servlet 的 service 方法。通常，自定义的 Servlet 并不会直接去实现 javax.servlet.Servlet 接口，而是去继承更简单的 HttpServlet 或者 GenericServlet 类，从而选择性地覆盖想要完成的方法。

现在的 MVC 框架没有直接将交互的页面用 Servlet 来实现，而是把所有的请求都映射到一个 Servlet，然后去实现 service 方法，这个方法也就是 MVC 框架的入口。

​    

## Servlet 中的 Listener

观察者模式。

总体分为 EventListeners 和 LifecycleListeners。实际上这些 Listener 都继承了 EventListener 接口。

除了 web.xml，还可以在程序中动态添加 Listener。

Spring 中通过实现 ServletContextListener 类，实现了当容器加载时启动 Spring 容器。

​    

## Filter 如何工作

在 Tomcat 容器中，FilterConfig、FilterChain 的实现类分别是 ApplicationFilterConfig、ApplicationFilterChain。

**FilterConfig** 与 ServletConfig 类似，除了都能获取到容器环境类 ServletContext 对象之外，还能获取在 web.xml 中 filter 标签下的 init-param 标签的参数值。

**ApplicationFilterChain** 可以将多个 Filter 串联起来，调用 FilterChain.doFilter 方法可以将请求继续传递下去，如果想拦截就可以不调用该方法，所以 Filter 是一个**责任链设计模式**。

ApplicationFilterChain 中的 filters 数组保存了 Servlet 对象的所有 Filter 对象。每次执行一个 Filter 对象，数组的计数就会加 1。执行所有的 Filter 对象后，就会执行 Servlet，所以在 ApplicationFilterChain 对象中会持有 Servlet 对象的引用。

**具体流程**：StandardWrapperValue 使用单例模式获取 ApplicationFilterFactory 工厂。工厂会通过 createFilterChain 方法创建 ApplicationFilterChain 对象（此时把 Servlet 对象设置到 Chain 对象中，并且会获取 web.xml 中配置的 Filter 添加到 filters 数组）。之后 StandardWrapperValue 会调用 doFilter 方法开始进行过滤。注意，filters 数组中获取的实际上是 FilterConfig 对象，如果没有初始化，会初始化 Filter 对象。

​    

## Servlet 中的 url-pattern

何时匹配：

请求分配到一个 Servlet 是通过 Mapper 来完成的。这个类会根据请求的 URL 来匹配在每个 Servlet 中配置的 url-pattern，所以它在一个请求被创建时就已经匹配了。

Filter 的 url-pattern 匹配是在创建 ApplicationFilterChain 对象时进行的。它会把所有匹配成功的 Filter 保存到 filters 数组里。

web.xml 加载时，首先会检查 url-pattern 配置是否符合规则（通过 StandardContext 的 validateURLPattern 方法检查），如果检查不成功，Context 容器会启动失败。

如何匹配：

Servlet 的匹配规则在 Mapper.internalMapWrapper 方法中定义。如果 Servlet 有多个 url-pattern，其匹配顺序就是：先精确匹配，然后最长路径匹配，最后根据后缀进行匹配。一次请求只会成功匹配一个 Servlet。

Filter 的匹配规则在 ApplicationFilterFactory.matchFiltersURL 方法中定义。与 Servlet 的匹配原则不同：只要匹配成功，这些 Filter 都会在请求链上被调用。



