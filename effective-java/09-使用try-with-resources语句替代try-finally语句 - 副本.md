# 9. 使用try-with-resources语句替代try-finally语句

> 第三版

​    

Java 类库中包含许多必须通过调用 `close` 方法手动关闭的资源。 比如 `InputStream`，`OutputStream` 和 `java.sql.Connection`。 客户经常忽视关闭资源，其性能结果可想而知。 尽管这些资源中有很多使用 finalizer 机制作为安全网，但 finalizer 机制却不能很好地工作（条目 8）。

​    

### try-finally 的缺点

1）有多个资源时，代码会很长。如下：

```java
static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```

2）finally 代码块中的异常会覆盖掉 try 里面的异常。异常堆栈跟踪里只有 finally 中的异常信息（即第二个异常）。

如下，在 `firstLineOfFile` 方法中，由于底层物理设备发生故障，对 `readLine` 方法的调用可能会引发异常，并且由于相同的原因，调用 `close` 方法可能会失败。 在这种情况下，第二个异常完全冲掉了第一个异常。 在异常堆栈跟踪中没有第一个异常的记录，这可能使实际系统中的调试非常复杂——通常这是你想要诊断问题的第一个异常。 虽然可以编写代码来抑制第二个异常，但是实际上没有人这样做，因为它太冗长了。

```java
static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}
```

​    

### 解决方法

使用 Java 7 引入的 try-with-resources 语句。

要使用这个构造，资源必须实现 `AutoCloseable` 接口，该接口由一个返回为 `void` 的 `close` 方法组成。如果你编写的类表示必须关闭的资源，那么这个类也应该实现 `AutoCloseable` 接口。

如下是使用后的代码：

```java
static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(
           new FileReader(path))) {
       return br.readLine();
    }
}
```

有两个资源时：

```java
static void copy(String src, String dst) throws IOException {
    try (InputStream   in = new FileInputStream(src);
         OutputStream out = new FileOutputStream(dst)) {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) >= 0)
            out.write(buf, 0, n);
    }
}
```

