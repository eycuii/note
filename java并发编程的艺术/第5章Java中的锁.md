# 第5章 Java 中的锁

​    

## Lock 接口

比起 synchronized，Lock 可以：

1. 响应中断，被中断时抛出中断异常并释放锁；
2. 超时获取（tryLock(long time, TimeUnit unit)），指定时间内未获取锁时可以返回；
3. 非阻塞地获取锁（tryLock()），调用会立即返回，如果能获取则返回 true，否则返回 false。

​    

## AbstractQueuedSynchronizer 队列同步器

