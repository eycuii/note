# Ehcache、Redis、Memcached区别

​    

#### Ehcache：

1. 用 LinkedHashMap 存储 Element 。
2. 直接在 JVM 里，所以访问速度比 Redis 、Memcached 快。但 Java 程序终止时也不存在了。
3. 有缓存共享的方案，但复杂。不适用于大数据的缓存。

#### Redis：

1. 支持多种数据结构。
2. 有主从复制。
3. 有持久化方案（RDB、AOF 两种）。
4. 单核。

#### Memcached：

1. 支持多核多线程。
2. Key - Value 键值缓存。
3. 不支持数据持久化。