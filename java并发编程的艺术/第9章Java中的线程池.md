# 第9章  Java中的线程池

​    

## ThreadPoolExecutor

### execute 方法执行步骤

ThreadPoolExecutor 执行 execute 方法分下面4种情况：

1. 如果当前运行的线程少于 corePoolSize，则创建新线程来执行任务（注意，执行这一步骤需要获取全局锁）。
2. 如果运行的线程等于或多于 corePoolSize，则将任务加入 BlockingQueue。Worker 工作线程执行完任务后，会循环获取工作队列里的任务来执行。
3. 如果无法将任务加入 BlockingQueue（队列已满），则创建新的线程来处理任务（注意，执行这一步骤需要获取全局锁）。
4. 如果创建新线程将使当前运行的线程超出 maximumPoolSize，任务将被拒绝，并调用 RejectedExecutionHandler.rejectedExecution() 方法。 

​    

### 创建方法

`new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, milliseconds, runnableTaskQueue, handler);`

1. **corePoolSize（线程池的基本大小）**：当提交一个任务到线程池时，线程池会创建一个线程来执行任务，即使其他空闲的基本线程能够执行新任务也会创建线程，等到需要执行的任务数大于线程池基本大小时就不再创建。如果调用了线程池的 prestartAllCoreThreads() 方法，线程池会提前创建并启动所有基本线程。

2. **runnableTaskQueue（任务队列）**：用于保存等待执行的任务的阻塞队列。可以选择以下几个阻塞队列。

   - ArrayBlockingQueue：是一个基于数组结构的有界阻塞队列，此队列按 FIFO（先进先出）原则对元素进行排序。


   - LinkedBlockingQueue：一个基于链表结构的阻塞队列，此队列按 FIFO 排序元素，吞吐量通常要高于 ArrayBlockingQueue。静态工厂方法 Executors.newFixedThreadPool() 使用了这个队列。


   - SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态，吞吐量通常要高于 LinkedBlockingQueue，静态工厂方法 Executors.newCachedThreadPool 使用了这个队列。


   - PriorityBlockingQueue：一个具有优先级的无限阻塞队列。

3. **maximumPoolSize（线程池最大数量）**：线程池允许创建的最大线程数。如果队列满了，并且已创建的线程数小于最大线程数，则线程池会再创建新的线程执行任务。值得注意的是，如果使用了无界的任务队列这个参数就没什么效果。

4. **ThreadFactory**：用于设置创建线程的工厂，可以通过线程工厂给每个创建出来的线程设置更有意义的名字。 

5. **RejectedExecutionHandler（饱和策略）**：当队列和线程池都满了，说明线程池处于饱和状态，那么必须采取一种策略处理提交的新任务。这个策略默认情况下是 AbortPolicy，表示无法处理新任务时抛出异常。在JDK  1.5 中 Java 线程池框架提供了以下 4 种策略：

   - AbortPolicy：直接抛出异常。

   - CallerRunsPolicy：只用调用者所在线程来运行任务。


   - DiscardOldestPolicy：丢弃队列里最近的一个任务，并执行当前任务。


   - DiscardPolicy：不处理，丢弃掉。

   当然，也可以根据应用场景需要来实现 RejectedExecutionHandler 接口自定义策略。 

​    

### submit() 与 execute() 方法

**execute()** 方法用于提交不需要返回值的任务，所以无法判断任务是否被线程池执行成功。
通过以下代码可知 execute() 方法输入的任务是一个 Runnable 类的实例。

```java
threadsPool.execute(new Runnable() {
    @Override
    public void run() {
    	// TODO Auto-generated method stub
    }
});
```

**submit()** 方法用于提交需要返回值的任务。线程池会返回一个 future 类型的对象，通过这个 future 对象可以判断任务是否执行成功，并且可以通过 future 的 get() 方法来获取返回值，get() 方法会阻塞当前线程直到任务完成，而使用 get（long timeout，TimeUnit unit）方法则会阻塞当前线程一段时间后立即返回，这时候有可能任务没有执行完。

```java
Future<Object> future = executor.submit(harReturnValuetask);
try {
	Object s = future.get();
} catch (InterruptedException e) {
	// 处理中断异常
} catch (ExecutionException e) {
	// 处理无法执行任务异常
} finally {
	// 关闭线程池
	executor.shutdown();
} 
```

​    

### shutdown() 与 shutdownNow() 方法

关闭线程池。遍历线程池中的工作线程，然后逐个调用线程的 interrupt 方法来中断线程，所以无法响应中断的任务可能永远无法终止。区别：

- **shutdownNow**：首先将线程池的状态设置成 STOP，然后尝试停止所有的正在执行或暂停任务的线程，并返回等待执行任务的列表；


- **shutdown**：只是将线程池的状态设置成 SHUTDOWN 状态，然后中断所有没有正在执行任务的线程。

只要调用了这两个关闭方法中的任意一个，isShutdown 方法就会返回 true。当所有的任务都已关闭后，才表示线程池关闭成功，这时调用 isTerminaed 方法会返回 true。

至于应该调用哪一种方法来关闭线程池，应该由提交到线程池的任务特性决定，通常调用 shutdown 方法来关闭线程池，如果任务不一定要执行完，则可以调用 shutdownNow 方法。

​    

### 合理配置线程池

- CPU 密集型任务：应配置尽可能小的线程，如配置 Ncpu+1 个线程的线程池。


- IO 密集型任务：IO 密集型任务线程并不是一直在执行任务，则应配置尽可能多的线程，如 2*Ncpu。


- 混合型的任务：如果可以拆分，将其拆分成一个 CPU 密集型任务和一个 IO 密集型任务，只要这两个任务执行的时间相差不是太大，那么分解后执行的吞吐量将高于串行执行的吞吐量。如果这两个任务执行时间相差太大，则没必要进行分解。


- 执行时间不同的任务：可以交给不同规模的线程池来处理，或者可以使用优先级队列，让执行时间短的任务先执行。
- 依赖数据库连接池的任务：因为线程提交 SQL 后需要等待数据库返回结果，等待的时间越长，则 CPU 空闲时间就越长，那么线程数应该设置得越大，这样才能更好地利用 CPU。 

可以通过 Runtime.getRuntime().availableProcessors() 方法获得当前设备的 CPU 个数。 

​    

#### 建议使用有界队列

有界队列能增加系统的稳定性和预警能力，可以根据需要设大一点，比如几千。

有一次，我们系统里后台任务线程池的队列和线程池全满了，不断抛出抛弃任务的异常，通过排查发现是数据库出现了问题，导致执行SQL变得非常缓慢。因为后台任务线程池里的任务全是需要向数据库查询和插入数据的，所以导致线程池里的工作线程全部阻塞，任务积压在线程池里。

如果当时我们设置成无界队列，那么线程池的队列就会越来越多，有可能会撑满内存，导致整个系统不可用，而不只是后台任务出现问题。

当然，我们的系统所有的任务是用单独的服务器部署的，我们使用不同规模的线程池完成不同类型的任务，但是出现这样问题时也会影响到其他任务。 

​    

### 线程池的监控

可以通过线程池提供的参数进行监控，在监控线程池的时候可以使用以下属性。

- **taskCount**：线程池需要执行的任务数量。


- **completedTaskCount**：线程池在运行过程中已完成的任务数量，小于或等于 taskCount。


- **largestPoolSize**：线程池里曾经创建过的最大线程数量。通过这个数据可以知道线程池是否曾经满过。如该数值等于线程池的最大大小，则表示线程池曾经满过。


- **getPoolSize**：线程池的线程数量。如果线程池不销毁的话，线程池里的线程不会自动销毁，所以这个大小只增不减。


- **getActiveCount**：获取活动的线程数。

通过扩展线程池进行监控。可以通过继承线程池来自定义线程池，重写线程池的 beforeExecute、afterExecute 和 terminated 方法，也可以在任务执行前、执行后和线程池关闭前执行一些代码来进行监控。例如，监控任务的平均执行时间、最大执行时间和最小执行时间等。这几个方法在线程池里是空方法。 

​    

