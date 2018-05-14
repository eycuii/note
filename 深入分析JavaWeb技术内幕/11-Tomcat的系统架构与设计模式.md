# 第11章 Tomcat的系统架构与设计模式

Tomcat 如何分发请求、如果处理多用户同时请求、它的多级容器是如何协调工作的。

​    

## Tomcat 总体设计

### Tomcat 总体架构

核心组件：Connector、Container。

**Service：**

多个 Connector 和一个 Container 形成一个 Service。Service 接口的标准实现类是 StandardService。

**Server：**

Service 的生命周期由 Server 来维护，Server 会提供访问各 Service 的接口。Server 的标准实现类是 StandardServer。

**Lifecycle：**

Tomcat 中组件的生命周期是通过 Lifecycle 接口来控制的。组件只要实现这个接口就可以统一被拥有它的上级组件控制了。有 start()、stop() 等方法。

### Connector 组件

主要任务：负责接收浏览器发过来的 TCP 连接请求，然后分配线程让 Container 来处理这个请求。

创建一个 Request、Response 对象用于和请求端交换数据。然后产生一个线程来处理这个请求，并把 Request、Response 对象传给这个线程。

Connector 处理接收请求是多线程的。启动后会先进入等待请求状态，有请求时会唤醒一个线程。然后这个线程会把 Socket 封装成 Request、Response 对象。接下来就交给 Container 来处理。

### Servlet 容器 Container

Container 是容器的父接口，所有子容器必须实现这个接口。

Container 容器的设计用的是典型的责任链的设计模式。它包含 4 个子容器组件：Engine、Host、Context、Wrapper。它们不是平行的，前面组件包含后面组件。

通常一个 Servlet class 对应一个 Wrapper。

**容器的总体设计**

要运行 war 程序，必须要用 Host。因为 war 中必有 web.xml，这个文件的解析需要 Host。

如果要有多个 Host 就要定义一个 Engine。一个 Engine 代表一个完整的 Servlet 引擎。

**Engine 容器**

**Host 容器**

一个 Host 在 Engine 中代表一个虚拟主机。

**Context 容器**

具有 Servlet 运行的基本环境。主要功能就是管理它里面的 Servlet 实例。

Context 如何找出正确的 Servlet 来执行？Tomcat 5 之前是通过 Mapper 类，之后这个功能移到了 Request 中。

理论上只要有 Context 就能运行 Servlet 了。简单的 Tomcat 可以没有 Engine 和 Host。

**Wrapper 容器**

Wrapper 的实现类是 StandardWrapper。

**Tomcat 中的其他组件**

如安全组件 security、日志组件 logger、session、naming、mbeans 等。这些组件共同为 Connector、Container 提供必要的服务。

​    

## Tomcat 中的设计模式

### 门面（Facade）设计模式

### 观察者设计模式

### 命令设计模式

Connector 通过命令模式调用 Container。

### 责任链设计模式

从 Engine 到 Wrapper。

Pipeline-Value。

https://www.cnblogs.com/killbug/archive/2012/11/03/2752921.html：

Pipeline 相当于 filter chain，Value 相当于 filter。每个Container 的 Pipeline 的最后一个 Value 都是 StandardXXXValue，它会在执行后调用子容器 Container 的 Pipeline 第一个 Value，然后按顺序执行后面的 Value。

比如 StandardEngineValue 是：`host.getPipeline().getFirst().invoke(request, response);`

而普通的 Value 是：`getNext().invoke(request, response);`

