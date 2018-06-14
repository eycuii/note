### 分布式锁丢失

项目中 mybatis 的二级缓存用了 redis，具体实现类为 RedisCache。

RedisCache 中我在刷新缓存（clear）时直接清掉了 redis 里面的所有数据，导致用来当分布式锁的 key 也被清掉了，所以出现了线程还没释放（删除）锁，其他线程就拿到（SETNX）了锁的情况。

​    

### mybatis 缓存脏读

下单服务是 OrderService 的 orderWithLock 方法，该方法内部如果当前线程获得了锁，会执行业务逻辑，即获取商品信息、修改商品数量、创建订单。

一开始我在 orderWithLock 方法上加了事务（@Transactional），后来测试时发现分布式锁没问题，但获取商品信息时出现了脏读的情况。

#### 原因

原因就是 orderWithLock 方法上加了事务，在事务内进行了对锁的获取和释放：

mybatis 二级缓存在 getObject 时如果发现没有，会从数据库查询，然后提交事务时会 putObject 更新缓存和数据库。而这里获取商品信息时的 putObject，是在释放锁之后 orderWithLock 事务结束时进行的，所以其他线程在加锁成功后获取商品信息时出现了脏读。

获取商品信息部分的具体流程、细节：

1. 线程 1 获取锁
2. 线程 1 获取商品信息。RedisCache.getObject 发现没有，所以直接从数据库获取
3. 线程 1 修改商品数据并创建订单
4. 线程 1 释放锁
5. 线程 1 的 orderWithLock 还没结束，此时线程 2 获取锁
6. 线程 2 获取商品信息。RedisCache.getObject 发现没有，所以直接从数据库获取（脏读）
7. 线程 1 的 orderWithLock 方法结束，RedisCache.putObject 更新缓存及数据库（这里因为还有修改商品，所以还会进行 clear 刷新缓存）。

#### 初步解决方案

去掉 orderWithLock 上的 @Transactional。但这样下单服务就不能回滚，所以该方法不可取。

#### 最终解决方案

在 service 的上层进行获取、释放锁，service 只做业务处理。

#### 参考

关于mybatis缓存机制的介绍：https://tech.meituan.com/mybatis_cache.html

