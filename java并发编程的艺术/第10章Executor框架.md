# 第10章  Executor框架

​    

Executor 框架包含的主要的类与接口：

![java并发编程的艺术-Executor框架的类与接口](..\img\java并发编程的艺术-Executor框架的类与接口.png)

​    

​    

## ThreadPoolExecutor

Executors 可以创建 3 种类型的 ThreadPoolExecutor：SingleThreadExecutor、FixedThreadPool 和 CachedThreadPool。

​    

### FixedThreadPool

线程数固定。

##### 缺点

FixedThreadPool 使用无界队列 LinkedBlockingQueue 作为线程池的工作队列（队列的容量为 Integer.MAX_VALUE）。造成的影响：

1. 当线程池中的线程数达到 corePoolSize 后，新任务将在无界队列中等待，因此线程池中的线程数不会超过 corePoolSize。
2. 由于 1，使用无界队列时 maximumPoolSize 将是一个无效参数。
3. 由于 1 和 2，使用无界队列时 keepAliveTime 将是一个无效参数。
4. 由于使用无界队列，运行中的 FixedThreadPool（未执行方法 shutdown() 或 shutdownNow()）不会拒绝任务（不会调用 RejectedExecutionHandler.rejectedExecution 方法）。 

##### 适用场景

适用于为了满足资源管理的需求，而需要限制当前线程数量的应用场景，它适用于负载比较重的服务器。

​    

### SingleThreadExecutor

单个线程。

corePoolSize 和 maximumPoolSize 被设置为 1。其他参数 与 FixedThreadPool 相同。 

##### 缺点

也使用无界队列。

##### 适用场景

适用于需要保证顺序地执行各个任务；并且在任意时间点，不会有多个线程是活动的应用场景。

​    

### CachedThreadPool

根据需要创建线程的线程池。

CachedThreadPool 的 corePoolSize 被设置为 0，即 corePool 为空；maximumPoolSize 被设置为 Integer.MAX_VALUE，即 maximumPool 是无界的。这里把 keepAliveTime 设置为 60L，意味着 CachedThreadPool 中的空闲线程等待新任务的最长时间为 60 秒，空闲线程超过 60 秒后将会被终止。 

##### 缺点

使用没有容量的 SynchronousQueue 作为线程池的工作队列，但 CachedThreadPool 的 maximumPool 是无界的。这意味着，如果主线程提交任务的速度高于 maximumPool 中线程处理任务的速度时，CachedThreadPool 会不断创建新线程。极端情况下，CachedThreadPool 会因为创建过多线程而耗尽 CPU 和内存资源。 

##### 适用场景

适用于执行很多的短期异步任务的小程序，或者是负载较轻的服务器。

​    

​    

## ScheduledThreadPoolExecutor

Executors 可以创建 2 种类型的 ScheduledThreadPoolExecutor：

​    

### ScheduledThreadPoolExecutor

若干个线程。

调用 scheduleAtFixedRate() 方法或者 scheduleWithFixedDelay() 方法时，会向 DelayQueue 队列添加一个实现了 RunnableScheduledFutur 接口的 ScheduledFutureTask。

DelayQueue 封装了一个 PriorityQueue，这个 PriorityQueue 会对队列中的 ScheduledFutureTask 进行排序。排序时，time 小的排在前面（时间早的任务将被先执行）。如果两个 ScheduledFutureTask 的 time 相同，就比较 sequenceNumber 序号，sequenceNumber 小的排在前面（也就是说，如果两个任务的执行时间相同，那么先提交的任务将被先执行）。

任务被执行后，其 time 会被修改为下一个执行时间，再加入到 DelayQueue 里。

##### 缺点

DelayQueue 是一个无界队列，所以 ThreadPoolExecutor 的 maximumPoolSize 在 ScheduledThreadPoolExecutor 中没有什么意义（设置 maximumPoolSize 的大小没有什么效果）。

##### 适用场景

适用于需要多个后台线程执行周期任务，同时为了满足资源管理的需求而需要限制后台线程的数量的应用场景。 

​    

### SingleThreadScheduledExecutor

单个线程的 ScheduledThreadPoolExecutor。

适用于需要单个后台线程执行周期任务，同时需要保证顺序地执行各个任务的应用场景。

​    

​    

## Future 接口

Future 接口和实现 Future 接口的 FutureTask 类用来表示异步计算的结果。

​    

### FutureTask

把 Runnable 或 Callable 接口的实现类提交（submit）给 ThreadPoolExecutor 或 ScheduledThreadPoolExecutor 时，ThreadPoolExecutor 或 ScheduledThreadPoolExecutor 会返回一个 FutureTask 对象。 

FutureTask 可以交给 Executor 执行；也可以通过 ExecutorService.submit（…）方法返回一个 FutureTask，然后执行 FutureTask.get() 方法或 FutureTask.cancel（…）方法；也可以由调用线程直接执行（FutureTask.run()）（FutureTask 除了实现 Future 接口外，还实现了 Runnable 接口）。  

FutureTask 的实现基于 AbstractQueuedSynchronizer 队列同步器（第 5 章）。

##### 状态

1. 未启动。FutureTask.run() 方法还没有被执行之前，FutureTask 处于未启动状态。当创建一个 FutureTask，且没有执行 FutureTask.run() 方法之前，这个 FutureTask 处于未启动状态。
2. 已启动。FutureTask.run() 方法被执行的过程中，FutureTask 处于已启动状态。
3. 已完成。FutureTask.run() 方法执行完后正常结束，或被取消（FutureTask.cancel（…）），或

执行 FutureTask.run() 方法时抛出异常而异常结束，FutureTask 处于已完成状态。 

![java并发编程的艺术-FutureTask的get和cancel的执行示意图](..\img\java并发编程的艺术-FutureTask的get和cancel的执行示意图.png)

​    

​    

## Runnable 接口与 Callable 接口

Runnable 不会返回结果，Callable 可以返回结果。

可以使用工厂类 Executors 来把一个 Runnable 包装成一个 Callable。

​    

下面是 Executors 提供的，把一个 Runnable 包装成一个 Callable 的 API：

`public static Callable<Object> callable(Runnable task) // 假设返回对象Callable1`

下面是 Executors 提供的，把一个 Runnable 和一个待返回的结果包装成一个 Callable 的 API：

`public static <T> Callable<T> callable(Runnable task, T result) // 假设返回对象Callable2`

​    

前面讲过，当我们把一个 Callable 对象（比如上面的 Callable1 或 Callable2）提交给 ThreadPoolExecutor 或 ScheduledThreadPoolExecutor 执行时，submit（…）会向我们返回一个 FutureTask 对象。我们可以执行 FutureTask.get() 方法来等待任务执行完成。

当任务成功完成后 FutureTask.get() 将返回该任务的结果。例如，如果提交的是对象 Callable1，FutureTask.get() 方法将返回 null；如果提交的是对象Callable2，FutureTask.get() 方法将返回 result 对象。

​    

