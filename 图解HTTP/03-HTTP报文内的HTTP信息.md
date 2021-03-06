# 第3章 HTTP报文内的HTTP信息

请求和响应是怎样运作的。

​    

## HTTP 报文

HTTP 报文本身是由多行（用 CR+LF 作换行符）数据构成的字符串文本。

由最初出现的 空行（CR+LF）来划分报文首部和报文主体。 

​    

## 请求报文及响应报文的结构

​    

## 编码提升传输效率

### 报文主体和实体主体的差异

通常，报文主体等于实体主体。只有当传输中进行编码操作时，实体主体的内容发生变化，才导致它和报文主体产生差异。

### 压缩传输的内容编码

gzip、compress、deflate、identity

### 分割发送的分块传输编码

传输大容量数据时，通过把数据分割成多块，能够让浏览器逐步显示页面。

由接收的客户端负责解码， 恢复到编码前的实体主体。

HTTP/1.1 中存在一种称为传输编码（Transfer Coding）的机制，它可以在通信时按某种编码方式传输，但只定义作用于分块传输编码中。

​    

## 发送多种数据的多部分对象集合

multipart/form-data：

在 Web 表单文件上传时使用。

multipart/byteranges：

状态码 206（Partial Content，部分内容）响应报文包含了多个范围的内容时使用。

使用 boundary 字符串来划分多部分对象集合指明的各类实体。在 boundary 字符串指定的各个实体的起始行之前插入“--”标记（例如： - -AaB03x、--THIS_STRING_SEPARATES），而在多部分对象集合对应的字符串的最后插入“--”标记（例如： --AaB03x--、-- THIS_STRING_SEPARATES--）作为结束。多部分对象集合的每个部分类型中，都可以含有首部字段。另外，可以在某个部分中嵌套使用多部分对象集合。

​    

## 获取部分内容的范围请求

对一份 10000 字节大小的资源，如果使用范围请求，可以只请求 5001~10000 字节内的资源。

比如下载中断后又恢复继续下载。

执行范围请求时，会用到首部字段 Range 来指定资源的 byte 范围。

针对范围请求，响应会返回状态码为 206 Partial Content 的响应报文。如果服务器端无法响应范围请求，则会返回状态码 200 OK 和完整的实体内容。

​    

## 内容协商返回最合适的内容

当浏览器的默认语言为英语或中文，访问相同 URI 的 Web 页面时，则会显示对应的英语版或中文版的 Web 页面。这样的机制称为内容协商（Content Negotiation）。

包含在请求报文中的某些首部字段（如下）就是判断的基准：

- Accept
- Accept-Charset
- Accept-Encoding
- Accept-Language
- Content-Language

内容协商技术有以下 3 种类型：

- 服务器驱动协商（Server-driven Negotiation）

  由服务器端进行内容协商。以请求的首部字段为参考，在服务器端自动处理。但对用户来说，以浏览器发送的信息作为判定的依据，并不 一定能筛选出最优内容。

- 客户端驱动协商（Agent-driven Negotiation）

  由客户端进行内容协商的方式。用户从浏览器显示的可选项列表中手动选择。还可以利用 JavaScript 脚本在 Web 页面上自动进行上述选择。比如按 OS 的类型或浏览器类型，自行切换成 PC 版页面或手机版页面。

- 透明协商（Transparent Negotiation）

  是服务器驱动和客户端驱动的结合体，是由服务器端和客户端各自进 行内容协商的一种方法。

