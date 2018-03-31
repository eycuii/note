# 写给大忙人看的Java SE 8

​    

## 第1章 lambda 表达式

### 表达式语法

### 函数式接口

@FuntionalInterface

只有一个抽象方法。

### 方法引用

- `对象::实例方法`


- `类::静态方法`


- `类::示例方法`

### 构造器引用

`类::new`

### 变量作用域

### 默认方法

default

类优先

### 接口中的静态方法

​    

​    

## 第2章 Stream API

### 创建 Stream

Collection 接口的 stream 方法（list.stream()）；

Stream.of(数组) 方法；

### filter、map、flatMap 方法

### 提取子流和组合流

limit、skip 方法

peek 方法：创建相同元素的流，并且每次获取一个元素时都会调用一个函数。

### 简单的聚合方法

max、min、count 方法。

聚合方法都是终止操作。

max、min 返回的是 Optional<T> 对象。

### Optional 类型

Optional 对象的 get 方法，如果 value 存在会返回，如果不存在则报异常。

### 聚合操作

### 收集结果

collect 方法

Collections 的 toXXX、summarizing 方法

forEach 方法

### 将结果收集到 Map 中

### 分组和分片

collect 方法

Collections.groupingBy、partitioningBy 方法

### 原始类型流

IntStream、DoubleStream 等类型。不必使用包装。

创建方法：

IntStream.of；

Arrays.stream；

### 并行流

parallel 方法

默认情况下流操作都创建的是一个串行流。

使用并行流需要注意线程安全的问题。

​    

​    

## 第3章 使用 lambda 编程

### 延迟执行

比如：

```java
logger.info("x:" + x + ", y:" + y);
// 会先组成一个字符串后，再根据日志级别判断是否要打印
```

改成：

```java
logger.info(() -> "x:" + x + ", y:" + y);
```

这样，就能在判断后需要打印时才会组成字符串。

### 常用函数式接口

![ava8-常用函数式接口](../img/java8-常用函数式接口1.png)

![ava8-常用函数式接口](../img/java8-常用函数式接口2.png)

### 返回函数

### 组合

使用 UnaryOperator 实现组合操作：

```java
public static <T> UnaryOperator<T> compose(UnaryOperator<T> op1, UnaryOperator<T> op2) {
    return t -> op2.apply(op1.apply(t));
}
```

### 延迟

### 并行操作

