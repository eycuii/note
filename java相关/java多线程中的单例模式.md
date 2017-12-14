探究java多线程中正确的单例模式 volatile关键字：http://blog.csdn.net/glory1234work2115/article/details/50814419



## 探究java多线程中正确的单例模式 volatile关键字

**关键点是volatile关键字的作用**

单例模式的实现：



### 1.饿汉模式

```java
public class Singleton {
	
	private static final Singleton instance=new Singleton();
	
	private Singleton(){
	}
	
	public static Singleton getInstance(){
		return instance;
	}
}
```

优点：因为加载就初始化了，是线程安全的。且不用额外判断加锁等。性能较好。

缺点：如果这个单例没有被用起来会造成资源浪费。



### 2.懒汉模式

```java
public class Singleton{
	
	private static final Singleton instance;
	
	private Singleton(){
	}
	
	public static Singleton getInstance(){
		if(instance==null){
			instance=new Singleton();
		}
		return instance;
	}
}
```

缺点：多线程环境下无法保证单例效果，会多次执行 `instance=new Singleton()` ，需要考虑到多线程



### 3.同步方法或同步块 懒汉模式

同步方法方式

```java
public class Singleton{
	
	private static final Singleton instance;
	
	private Singleton(){
	}
	
	public static synchronized Singleton getInstance(){
		if(instance==null){
			instance=new Singleton();
		}
		return instance;
	}
}
```

缺点：性能不高，同步范围太大，在实例化 instacne 后，获取实例仍然是同步的，效率太低，需要缩小同步的范围。

```java
public class Singleton{
	
	private static final Singleton instance;
	
	private Singleton(){
	}
	
	public static  Singleton getInstance(){
		
		if(instance==null){
			synchronized(Singleton.class){
				instance=new Singleton();
			}
		}
		return instance;
	}
}
```

缺点：缩小同步范围，来提高性能，但是让然存在多次执行 `instance=new Singletom()` 的可能，由此引出 double check



### 4.double check 懒汉模式 （DCL）

```java
public class Singleton{
	
	private static final Singleton instance;
	
	private Singleton(){
	}
	
	public static  Singleton getInstance(){
		
		if(instance==null){
			synchronized(Singleton.class){
				if(instance==null){
					instance=new Singleton();
				}
			}
		}
		return instance;
	}
}
```

缺点：避免的上面方式的明显缺点，但是 java 内存模型（jmm）并不限制处理器重排序，在执行 `instance=new Singleton();` 时，并不是原子语句，实际是包括了下面三大步骤：

1. 为对象分配内存
2. 初始化实例对象
3. 把引用 instance 指向分配的内存空间

这个三个步骤并不能保证按序执行，处理器会进行指令重排序优化，存在这样的情况：

优化重排后执行顺序为：1,3,2, 这样在线程 1 执行到 3 时，instance 已经不为 null 了，线程 2 此时判断`instance!=null`，则直接返回 instance 引用，但现在实例对象还没有初始化完毕，此时线程 2 使用 instance 可能会造成程序崩溃。

现在要解决的问题就是怎样限制处理器进行指令优化重排。



### 5.volatile double check 懒汉模式

在JDK1.5之后，使用 volatile 关键字修饰 instance 就可以实现正确的 double check 单例模式了

```java
public class Singleton{
	
	private static final volatile Singleton instance;
	
	private Singleton(){
	}
	
	public static Singleton getInstance(){
		
		if(instance==null){
			synchronized(Singleton.class){
				if(instance==null){
					instance=new Singleton();
				}
			}
		}
		return instance;
	}
}
```

这里就要介绍一下 volatile 的作用了：

1.保证可见性

可以保证在多线程环境下，变量的修改可见性。每个线程都会在工作内存（类似于寄存器和高速缓存），实例对象都存放在主内存中，在每个线程要使用的时候把主内存中的内容拷贝到线程的工作内存中。使用 volatile 关键字修饰后的变量，保证每次修改了变量需要立即写回主内存中，同时通知所有的该对变量的缓存失效，保证缓存一致性，其他线程需要使用该共享变量时就要重新从住内存中获取最新的内容拷贝到工作内存中供处理器使用。这样就可以保证变量修改的可见性了。但 volatile 不能保证原子性，比如++操作。

**2.提供内存屏障**

volatile关键字能够通过提供内存屏障，来保证某些指令顺序处理器不能够优化重排，编译器在生成字节码时，会在指令序列中插入内存屏障来禁止特定类型的处理器重排序。

下面是保守策略插入内存屏障：

- 在每个 volatile 写操作的前面插入一个 StoreStore 屏障。


- 在每个 volatile 写操作的后面插入一个 StoreLoad 屏障。


- 在每个 volatile 读操作的前面插入一个 LoadLoad 屏障。


- 在每个 volatile 读操作的后面插入一个 LoadLoad 屏障。

这样可以保证在volatile关键字修饰的变量的赋值和读取操作前后两边的大的顺序不会改变，在内存屏障前面的顺序可以交换，屏障后面的也可以换序，但是不能跨越内存屏障重排执行顺序。

好了，现在来看上面的单例模式，这样就可以保证3步骤（instance 赋值操作）是保持最后一步完成，这样就不会出现 instance 在对象没有初始化时就不为 null 的情况了。这样也就实现了正确的单例模式了。



### 6.静态内部类懒汉模式

```java
public class Singleton{
	
	private Singleton(){
	}
	public static Singleton getInstance(){
		return InstanceHolder.instance;
	}
	static class InstanceHolder{
		private static final Singleton instance=new Singleton();
	}
}
```

优点：静态内部类在没有显示调用的时候是不会进行加载的，当执行了`return InstanceHolder.instance` 后才加载初始化，这样就实现了正确的单例模式。不用去加锁。



### 7.枚举实现

java1.5才出现的枚举类

```java
public enum EasySingleton {
	INSTANCE;
}
```

这样写很简便