# 8. 避免使用Finalizer和Cleaner机制

> 第三版

​    

不要把 Java 中的 Finalizer 或 Cleaner 机制当成的 C++ 析构函数的等价物。在 Java 中，当一个对象变得不可达时，垃圾收集器回收与对象相关联的存储空间，不需要开发人员做额外的工作。 

​    

### 缺点

**Java 规范不能保证 Finalizer 和 Cleaner 机制能及时运行；它甚至不能能保证它们是否会运行。**当一个程序结束后，一些不可达对象上的 Finalizer 和 Cleaner 机制仍然没有运行。因此，不应该依赖于 Finalizer 和 Cleaner 机制来更新持久化状态。例如，依赖于 Finalizer 和 Cleaner 机制来释放对共享资源(如数据库)的持久锁，这是一个使整个分布式系统陷入停滞的好方法。

不要相信 `System.gc` 和 `System.runFinalization` 方法。 他们可能会增加 Finalizer 和 Cleaner 机制被执行的几率，但不能保证一定会执行。 曾经声称做出这种保证的两个方法：`System.runFinalizersOnExit` 和它的孪生兄弟 `Runtime.runFinalizersOnExit`，包含致命的缺陷，并已被弃用了几十年。

Finalizer 机制的另一个问题是在执行 Finalizer 机制过程中，**未捕获的异常会被忽略**。

​    

### 好处

Finalizer 和 Cleaner 机制有两个合法用途。

一个是作为一个安全网（safety net），以防资源的拥有者忽略了它的 `close` 方法。虽然不能保证 Finalizer 和 Cleaner 机制会迅速运行(或者根本就没有运行)，最好是把资源释放晚点出来，也要好过客户端没有这样做。如果你正在考虑编写这样的安全网 Finalizer 机制，请仔细考虑一下这样保护是否值得付出对应的代价。一些 Java 库类，如 `FileInputStream`、`FileOutputStream`、`ThreadPoolExecutor` 和 `java.sql.Connection`，都有作为安全网的 Finalizer 机制。

第二种合理使用 Cleaner 机制的方法与本地对等类（native peers）有关。本地对等类是一个由普通对象委托的本地(非 Java)对象。由于本地对等类不是普通的 Java 对象，所以垃圾收集器并不知道它，当它的 Java 对等对象被回收时，本地对等类也不会回收。假设性能是可以接受的，并且本地对等类没有关键的资源，那么 Finalizer 和 Cleaner 机制可能是这项任务的合适的工具。但如果性能是不可接受的，或者本地对等类持有必须迅速回收的资源，那么类应该有一个 `close` 方法。

