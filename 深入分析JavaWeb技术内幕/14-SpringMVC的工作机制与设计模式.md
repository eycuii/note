# 第14章 SpringMVC的工作机制与设计模式

基于 Spring 2.5.6 版本介绍 Spring MVC 框架的工作机制。

如何实现 M、V、C 部分；如何基于 Spring 框架工作；

​    

## Spring MVC 的总体设计

要使用 Spring MVC，只需在 web.xml 中配置一个 DispatcherServlet，再定义一个 dispatcherServlet-servlet.xml 配置文件即可。

DispatcherServlet 继承了 HttpServlet，在 Servlet 的 init 方法调用时 DispatcherServlet 执行 Spring MVC 的初始化。DispatcherServlet 初始化什么，可以在其 initStrategies 方法中知道：

- initMultipartResolver：初始化 MultipartResolver，用于处理文件上传服务。如果有文件上传，会将当前的 HttpServletRequest 包装成 DefaultMultipartHttpServletRequest，并且将每个上传的内容封装成 CommonsMultipartFile 对象。
- initLocaleResolver：用于处理应用的国际化问题。通过解析请求的 Locale 和设置响应的 Locale 来控制应用中的字符编码问题。
- initThemeResolver：用于定义一个主题。
- initHandlerMappings：用于定义用户设置的请求映射关系。
- initHandlerAdapters：用于根据 Handler 的类型定义不同的处理规则。
- initHandlerExceptionResolvers：当 Handler 处理错误时，会通过这个 Handler 来统一处理。默认的实现类是 SimpleMappingExceptionResolver，将错误日志记录在 log 文件中，并且转到默认的错误页面。
- initRequestToViewNameTranslator：将指定的 ViewName 按照定义的 RequestToViewNameTranslator 替换成想要的格式，如加上前缀、后缀等。
- initViewResolvers：用于将 View 解析成页面。

核心组件：HandlerMapping、HandlerAdapter、ViewResolver。

Spring 容器在加载时会调用 DispatcherServlet 的 initStrategies 方法来完成初始化工作。该方法会初始化 Spring MVC 需要的 8 个组件，这 8 个组件对应的 8 个 Bean 对象都保存在 DispatcherServlet 类中。

​    

## Control 设计

主要由 HandlerMapping、HandlerAdapters 提供。

HandlerMapping 负责映射用户的 URL 和对应的处理类，HandlerMapping 并没有规定这个 URL 与应用的处理类如何映射，该接口只定义了根据一个 URL 必须返回一个 HandlerExecutionChain 代表的处理链。我们可以在这个处理链中添加任意的 HandlerAdapters 实例来处理这个 URL 对应的请求。

### HandlerMapping 初始化

默认实现类是 BeanNameUrlHandlerMapping，可以根据 Bean 的 name 属性映射到 URL 中。

HandlerMapping 负责映射用户的 URL 和对应的处理类，简单理解，就是将一个或多个 URL 映射到一个或多个 Spring Bean 中。它会把这关系保存到 handlerMap 中，并将所有的 interceptors 对象保存在 adaptedInterceptors 数组中。

### HandlerAdapter 初始化

HandlerApdapter 可以帮助自定义各种 Handler。

### Control 的调用逻辑

整个 Spring MVC 的调用是从 DispatcherServlet 的 **doService** 方法开始的。在 doService 方法中会将 ApplicationContext、localeResolver、themeResolver 等对象添加到 request 中以便于在后面使用。

接着就是调用 **doDispath** 方法，该方法主要处理用户的请求。

Control 的处理逻辑关键就是在 DispatcherServlet 的 handlerMappings。会根据请求的 URL 找出 Handler，找到后会返回这个 Handler 的处理链 HandlerExecutionChain 对象。这个对象中将会包含用户自定义的多个 HandlerInterceptor 对象。

HandlerExecutionChain 的 getHandler 方法返回的是 Object 对象。它的 Handler 类型由 HandlerAdapter 决定的。DispatcherServlet 会根据 Handler 对象在其 handlerAdapters 集合中匹配哪个 HandlerAdapter 实例来支持该 Handler 对象。接下来执行 Handler 对象的相应方法，如该 Handler 对象的相应方法返回一个 ModelAndView 对象，接下来就去执行 View 渲染。

​    

## Model 设计

ModelAndView 对象是连接业务逻辑层与 View 展现层的桥梁，对 Spring MVC 来说是连接 Handler 和 View 的桥梁。

ModelAndView 持有 ModelMap 对象和一个 View 对象或者 View 的名称。ModelMap 就是执行模板渲染时所需要的变量对应的实例，如 JSP 通过 request.getAttribute(String) 获取的对象。

这些对象会传递到 View 对应的 ViewResolvers 中。不同的 ViewResolvers 对这个 Map 中的对象有不同的处理方式。如 JSP 将每个对象分别设置到 request.setAttribute(modelName, modelValue) 中。

​    

## View 设计

RequestToViewName：如请求的 ViewName 加上前缀、后缀，或者替换成特定的字符串等。

ViewResolver：根据请求的 ViewName 创建合适的模板引擎来渲染最终的页面。会根据 ViewName 创建一个 View 对象，并调用 View 的 void render(Map model, HttpServletRequest request, HttpServletResponse response) 方法渲染页面。

​    

## 框架设计的思考

​    

## 设计模式解析之模板模式

