# 62. Avoid strings where other types are more appropriate

​    

Strings are poor substitutes for enum types. As discussed in Item 34,
enums make far better enumerated type constants than strings。

优先使用枚举而不是String。根据第34条，枚举比字符串常量更适合。

​    

对于容器，String 可能带来安全问题。比如，像ThreadLocal类，如果设计成：

```java
public class ThreadLocal {
	private ThreadLocal() { } // Noninstantiable
    
	// Sets the current thread's value for the named variable.
	public static void set(String key, Object value);
    
	// Returns the current thread's value for the named variable.
	public static Object get(String key);
}
```

由于String key会是全局共享的namespace，如果某个客户端也拥有相同的key，就可以获取到其他线程的本地变量。所以这种设计是不安全的。（所以 Java 中根据 ThreadLocal 实例引用来获取了线程的本地变量）