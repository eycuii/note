# 第3章 深入分析Java Web中的中文编码问题

​    

## 几种常见的编码格式

### 为什么要编码

计算机存储最小单元是 1 个字节，即 8 个 bit，所以能表示的字符范围是 256 个。

而人类要表示的符号太多，无法用 1 个字节来完全表示。

所以必须要有一个新的数据结构 char。而从 char 到 byte 必须编码。

### 如何“翻译”

提供了多种翻译方式，如 ASCII、ISO-8859-1、GBK、UTF-8 等。它们都规定了转化规则，按这个规则就可以让计算机正确地表示我们的字符。

ASCII 码：共 128 个。用一个字节的低 7 位表示。

ISO-8859-1：共 256 个字符。单字节编码。

GBK：汉字内码扩展规范。

UTF-16：用两个字节表示一个字符。所占的存储空间大，不适合网络传输。

UTF-8：使用变长技术，不同类型的字符可以由 1~6 个字节组成。

​    

## 在 Java 中需要编码的场景

### 在 I/O 操作中存在的编码

Java I/O 中的 StreamDecoder、Charset 等。

### 在内存操作中的编码

```java
String str = "中文字符串";
btye[] b = str.getBytes("UTF-8");
String s = new String(b, "UTF-8");
```

ByteBuffer：提供一种 char 和 byte 之间的软转换（只是把 16 bit 的 char 拆分成 2 个 8 bit 的 byte 表示，并不修改实际值）

```java
ByteBuffer heapByteBuffer = ByteBuffer.allocate(1024);
ByteBuffer byteBuffer = heapByteBuffer.putChar(c);
```

​    

## 在 Java 中如何编解码

​    

## 在 Java Web 中涉及的编解码

浏览器发起一个 HTTP 请求，需要存在编码的地方是 URL、Cookie、Parameter。

### URL 的编解码

每个浏览器对 PathInfo、QueryString 的编码方式可能不同。

Tomcat 配置里 Connector 的 useBodyEncodingForURI 为 true：QueryString 的解码可通过 Header 中 ContentType 定义的 Charset 进行解码，不然就是默认的 ISO-8859-1。

### HTTP Header 的编解码

如 Cookie、redirectPath 等。

Tomcat 默认对 Header 使用 ISO-8859-1。不能设置其他解码格式，所以不要在 Header 中传递非 ASCII 字符。

如果一定要传递，可以先将这些字符进行编码，再添加到 Header 里。

request.getHeader 时第一次进行解码

### POST 表单的编解码

浏览器根据 ContentType 的 Charset 会对表单的参数进行编码，再提交到服务器端。服务器端同样也是用 ContentType 的 Charset 进行解码。

注：GET、POST 都是在 request.getParameter 时第一次进行解码。所以必须要在此之前通过 request.setCharacterEncoding(charset) 设置。要注意比如过滤器上可能先调用了 request.getParameter 方法。

### HTTP BODY 的编解码

返回时可以通过 response.setCharacterEncoding 进行设置，它会通过 Header 的 Content-Type 返回客户端。如果 HTTP 的 Content-Type 没有设置 Charset，浏览器会根据 HTML 的 `<meta HTTP-equiv="Content-Type" content="text/html;charset=GBK"/>` 中的 charset 来解码。如果也没有定义，就会使用浏览器默认的编码来解码。

​    

## 在 JS 中的编码问题

### 外部引入 JS 文件

在 script 标签里通过 charset 可以设置。

### JS 的 URL 编码

ajax

encodeURI()：将整个 URL 中的字符（除一些特殊字符）进行 UTF-8 编码。在每个值前加上“%”。通过 decodeURI() 解码。

encodeURLComponent()：比 encodeURI() 编码的字符种类更多（比如"&"）。一般将一个 URL 当做另一个 URL 的参数时使用。

#### Java 与 JS 编解码问题

对于中文字符，JS 默认使用 UTF-8，服务器端默认 GBK 或 GBK2313。可以通过两次编码、解码来解决。

### 其他需编码的地方

JSP：page 标签的 contentType 值。

XML：头里的 encoding 值。

​    

## 常见问题分析

### 中文变成了看不懂的字符

编解码时的字符集不一致。

### 一个汉字变成一个问号

如像 ISO-8859-1 不支持中文的，编码后会把不认识的字符变成“？”。

### 一个汉字变成两个问号

多次编码时出现的编码错误。

### 一种不正常的正确编码

拆成两半后解码时又组成了一个正确的汉字。

​    

## 一种繁简转换的实现方式

