# 第6章 深入分析ClassLoader工作机制

​    

类加载器。将 Class 加载到 JVM。

​    

## ClassLoader 类结构分析

findClass()：双亲委派

defineClass()：byte 字节流解析成 Class 对象。注：这个对象还不会被链接。

resolveClass()：链接

loadClass()：加载

​    

## ClassLoader 的等级加载机制

​    

## 如何加载 class 文件

加载 - 链接（验证、准备、解析） - 初始化

### 加载字节码到内存

ClassLoader

### 验证与解析

### 初始化 Class 对象

​    

## 常见加载类错误分析

### ClassNotFoundException

一般发生在显示加载类的时候。

显示加载：

- Class 的 forName() 方法；
- ClassLoader 的 loadClass() 方法；
- ClassLoader 的 findSystemClass() 方法；

解决方法：检查当前的 classpath 目录下有没有指定的文件存在。可通过 `this.getClass().getClassLoader().getResource("").toString();` 查看当前的 classpath 路径。

### NoClassDefFoundError

第一次使用命令行执行 Java 类时很可能会碰到。如 `java -cp excample.jar Example` ，Example 前面需要加上包名。

还有一种是 JVM 规范描述里的，new 关键字、属性引用某个类、继承了某个接口或类、方法的某个参数中引用了某个类时（隐式加载）可能会出现此错误（发现这些类不存在）。

解决方法就是确保类在 classpath 下。

### UnsatisfiedLinkError

解析 native 方法时找不到对应的本机库文件。

### ClassCastException

强制类型转换时出现。

### ExceptionInIinitializerError

​    

## 常用的 ClassLoader 分析

Tomcat

​    

## 如何实现自己的 ClassLoader

### 加载自定义路径下的 class 文件

### 加载自定义格式的 class 文件

为了安全，对字节码进行加密、解密。

​    

## 实现类的热部署

使用同一个 ClassLoader 的不同的实例来进行加载。

注：ClassLoader 实例也跟其他对象一样会被 GC。但是，被这个 ClassLoader 加载的类的字节码是存在 PermGen 区的，而这里一般只是 Full GC 时才会被回收，所以如果有大量的动态类加载，Full GC 次数不够，会导致 PermGen 区的内存溢出。

​    

## Java 应不应该动态加载类

JSP