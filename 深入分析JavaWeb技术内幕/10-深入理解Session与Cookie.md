# 第10章 深入理解Session与Cookie

​    

## 理解 Cookie

Cookie 作用：当一个用户通过 HTTP 访问服务器时，这个服务器会将一些 K-V 键值对返回给客户端浏览器，使用户下次访问时可以再把这个数据传给服务器。

HTTP 是无状态协议，所以服务器无法知道这次访问的是不是上次访问的用户。

### Cookie 属性项

### Cookie 如何工作

response.addCookie 方法：主要是通过 org.apache.catalina.connector.Response 类完成构建 Cookie 的。Response 调用 generateCookieString 方法将 Cookie 对象构造成 String，然后将这个字符串命名为 Set-Cookie 添加到 MimeHeaders 中。

每次调用 addCookie 方法都会创建一个 Header，然后把 Cookie 加到该 Header 上。

但是 Tomcat 在最终构造 Http 响应头的代码时，即 HTTP 返回字节流时会将所有的 Header 中的项按顺序写出。所以浏览器在接收 HTTP 返回的数据时是分别解析每一个 Header 项的。

### 使用 Cookie 的限制

Cookie 是 HTTP 头中的一个字段，最终是存储在浏览器里的。不同的浏览器对 Cookie 的存储都有一些限制，比如每个域名的数量、Cookie 总大小。

每次访问都会必传 Cookie，如果 Cookie 很多会增加数据传输量。

​    

## 理解 Session

### Session 与 Cookie

Session 是根据 Cookie 里的 sessionId 在服务器里获取用户相关数据的。

### Session 如何工作

org.apache.catalina.Manager 的 sessions 保存所有 Session 并管理它们的生命周期。

Tomcat 后台线程会检查 Session 是否过期。并且调用 request.getSession() 方法时也会检查，如果过期了会自动创建一个 StandardSession 对象，这时不再拥有之前设置的 Session 值（也可以传 boolean 值不让自动创建）。

​    

## Cookie 安全问题

可以通过工具修改 Cookie，所以不安全。

​    

## 分布式 Session 框架

### 存在哪些问题

分布式框架中如果使用 Cookie 时会出现的问题：

- Cookie 是在客户端存的，有可能因为大小限制出现丢弃 Cookie 的现象。
- 如何每个应用系统都自己管理它的 Cookie，会导致混乱。
- Cookie 安全问题。

### 可以解决哪些问题

分布式 Session 框架解决的问题：

### 总体实现思路

需要一个服务订阅服务器：在应用启动时可以从服务订阅服务器中订阅这个应用需要的可写 Session 项和可写 Cookie 项。

**分布式缓存**。可以用 MemCache 等。

在 web.xml 里加一个 SessionFilter，请求时创建自己实现的 InnerHttpSession 对象并设置到 request、response 中。请求完成时把这个 InnerHttpSession 对象的内容传到分布式缓存里。

**对于 Cookie 的跨域名问题（如何实现 Session 同步）**：

需要另外一个跳转应用。它可以从一个域名下获取 sessionId，然后将这个 sessionId 同步到另外一个域名下。

这个 sessionId 就是一个 Cookie，相当于我们经常遇到的 JSESSIONID。所以要实现两个域名下的 Session 同步，必须将同一个 sessionId 作为 Cookie 写到两个域名下。

**对于 Cookie 被盗取的问题**：

设置一个 Session 签名：用户登录成功后根据用户信息生成一个签名，以表示当前这个唯一的合法登录状态。然后将这个签名作为一个 Cookie 在当前这个用户的浏览器和服务器传递。

用户每次访问服务器都会检查这个签名和分布式缓存中取得的 Session 重新生成的签名是否一致。如果不一致，服务端会把这个 sessionId 在分布式缓存中的 Session 给清除，然后让用户重新登录。

​    

## Cookie 压缩

文本压缩。可以使用 gzip、deflate 算法。

注意，Cookie 中不能有控制字符，只能包含 ASCII 码为 34~126 的可见字符。所以可以使用 Base64 编码。

​    

## 表单重复提交问题

可以在表单里加一个隐藏项，该项的值每次都是唯一的，然后把这个值存到用户的 Session 中。

​    

## 多终端 Session 统一

比如手机和 PC 端。如果没有统一就有可能出现二次登录的情况。

1. 多端共享 Session

   共享 Session、Cookie 信息。

   分布式 Session 框架。

2. 多终端登录

   比如扫码登录：手机扫 PC 网页上的二维码后，服务端会对该二维码设置标识位。这时 PC 端网页会不断请求服务端是否已经设置标识位，如果有，PC 端会跳转到登录后页面。





