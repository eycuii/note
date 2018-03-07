# 第4章 Java 并发编程基础

​    

### Daemon 守护线程

当一个 Java 虚拟机中不存在非 Daemon 线程的时候，Java 虚拟机将会退出。即使有 finally 代码块，也不会执行而直接停止。

在 start() 前 通过 thread.setDaemon(true); 可设置为 Daemon 线程。

​    

### 中断

从 Java 的 API 中可以看到，许多声明抛出 InterruptedException 的方法（例如 Thread.sleep(long millis) 方法）这些方法在抛出 InterruptedException 之前，Java虚拟机会先将该线程的中断标识位清除，然后抛出 InterruptedException，此时调用 isInterrupted() 方法将会返回 false。

而其他在正常运行中的线程，中断后 isInterrupted() 方法会返回 true 。

​    

### notify() 与 wait()

调用 wait() 方法，线程状态由 RUNNING 变为 WAITING，并释放锁，将当前线程放置到对象的等待队列。 

notify() 方法将等待队列中的一个等待线程从等待队列中移到同步队列中，而 notifyAll() 方法则是将等待队列中所有的线程全部移到同步队列，被移动的线程状态由 WAITING 变为
BLOCKED。

等待线程在获得锁后 wait() 方法才会返回。

​    

### 管道输入 / 输出流

PipedOutputStream、PipedInputStream、PipedReader 和 PipedWriter 

​    

### Thread.join() 

如果一个线程 A 执行了 thread.join() 语句，其含义是：当前线程 A 等待 thread 线程终止之后才从 thread.join() 返回（终止时会调用线程自身的 notifyAll() 方法，而通知所有等待在该线程对象上的线程）。线程 Thread 除了提供 join() 方法之外，还提供了 join(long millis) 和 join(long millis,int nanos) 两个具备超时特性的方法。这两个超时方法表示，如果线程 thread 在给定的超时时间里没有终止，那么将会从该超时方法中返回。 

​    

### ThreadLocal

作用是提供线程内的局部变量。

每个 Thread 维护一个 ThreadLocalMap 映射表，这个映射表的 key 是 ThreadLocal 实例本身，value 是要存储的对象。

https://www.zhihu.com/question/23089780

会引发内存泄漏。（所以建议定义 ThreadLocal 实例为 private static 的，并手动调用 ThreadLocal 的 remove()）//////////////////////////////////////////////////////////////

​    

### 线程池

当客户端调用 execute(Job) 方法时，会不断地向任务列表 jobs 中添加 Job，而每个工作者线程会不断地从 jobs 上取出一个 Job 进行执行，当 jobs 为空时，工作者线程进入等待状态。
添加一个 Job 后，对工作队列 jobs 调用了其 notify() 方法，而不是 notifyAll() 方法，因为能够确定有工作者线程被唤醒，这时使用 notify() 方法将会比 notifyAll() 方法获得更小的开销（避免将等待队列中的线程全部移动到阻塞队列中）。 //////////////////////////////////////////////////////////////

​    

