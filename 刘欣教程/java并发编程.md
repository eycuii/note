# Java 并发编程



进程：运行的程序。每个进程拥有各自的地址空间。

线程：每个线程都有自己的堆栈，寄存器。多个线程共享进程的数据。

​    

线程之间的通信：wait、notify、notifyAll

线程的状态

Timed Waiting -> Waiting -> Blocked -> Ready -> Running

等待中（wait()）的线程被唤醒（notify()）后进入 Blocked 状态，成功获得锁再进入 Ready 状态。

​    

java 常用的锁： 

可重入互斥锁 ReentrantLock

信号量 Semaphore：可以指定同一时刻，可以有n个线程能获得锁，所以是非互斥的。

读写锁 ReentrantReadWriteLock

倒计时 CountDownLatch：等前面 n 个线程执行后再执行业务逻辑。

栅栏 CyclicBarrier：n 个线程互相等其他线程都达到某"栅栏"处才执行。比如，某业务场景：旅游时，全部人（线程）都到集合点1后游览景点1，然后到集合点2后游览景点2...

​    

死锁：

银行转账例子：

```java
synchronized(from) {
	synchronized(to) {
		// 转账
	}
}
```

如果两个线程分别进行 A -> B，B -> A 账户之间的转账，互相持有锁不放，就会发生死锁。

解决方法：规定锁的顺序。比较 from，to 两个账户的 id，规定先获取 id 较小的对象的锁。

​    

线程池

本质是一个先进先出的阻塞队列 BlockingQueue，每次执行都会从队列取出一个线程启动。线程都存到 list 中，队列满了的时候不能 put 一个线程，需要先等待（阻塞）。

刚创立还没 Task 时，线程都处于阻塞状态。

停止时，已经 execute 执行的线程不会马上被打断。

​    

CAS

优点：线程不会阻塞，只是不停地检查。也不会出现死锁，因为线程之间没有互相等待的情况。

缺点：反复修改值的话，等待时间会长。

ABA 问题

解决方法：用版本戳或直接比较引用。

​    

ThreadLocal：内置一个 Map，数据从这个 map 中获取，所以是线程安全的。

​    

不变类

不能有改变对象的方法；

不能被扩展（拥有子类），使用 final 或者静态工厂；

所有的字段都应该是私有的、final 的。

