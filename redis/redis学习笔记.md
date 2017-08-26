# 1. 安装

**下载：**

```shell
wget http://download.redis.io/releases/redis-4.0.0.tar.gz
```

**解压：**

```shell
tar xzf redis-4.0.0.tar.gz
```

**编译：**

```shell
cd redis-4.0.0
make
```

**安装：**

```shell
make install
```

**启动：**（redis 默认使用 6379 端口）

```shell
redis-server
```

**客户端连接redis：**

```shell
redis-cli
set name abc
get name
```

​    



# 2. 基本数据类型

#### String 字符串

普通的 key-value 结构，value 可以是字符串或数字。

#### List 列表

是一个双向链表，添加新元素可以从左或右端 push 进去。

#### Hash 哈希

类似 java 的 HashMap。

#### Set 集合

不可重复。可以根据两个集合求出交集、并集、差集。

#### Sorted Set 有序集合

每个成员都有它的 score 值，有序集合会根据 score 值对集合进行排序。



#### 常用命令

可参考 http://doc.redisfans.com/
附：redis 中可以对每个 key 设置过期时间（`expires key 秒数`），如果超过该过期时间会被删除。

​    



# 3. redis cluster 集群

redis 3.0 开始支持集群。具有数据分片、主从复制（异步）、故障转移等特性。

### 数据分片

把数据分别放到 16384 个哈希槽中，集群中每个节点负责其中一部分的槽 slot 。

### 故障转移

集群中某一主节点宕机后，会选择它的一个从节点转为主节点继续服务。



### 搭建集群

搭建一个 3 个主节点和 3 个从节点的集群。其 IP 和端口分别为：

	192.168.42.128:7000
	192.168.42.128:7001
	192.168.42.128:7002
	192.168.42.128:7003
	192.168.42.128:7004
	192.168.42.128:7005
`192.168.42.128` 是在测试中使用的虚拟机的 IP。网上很多在本机上搭建集群的例子中直接用了 `127.0.0.1` 这个 IP，但这样会在外部连接不了 redis 集群，故不推荐。



**搭建步骤：**

（1）创建集群所需要的目录：

新建一个名为 cluster 的文件夹，并在里面分别建 7000、7001、7002....7006 ，6 个文件夹。

（2）把 redis-4.0.0 下的 `redis.conf` 配置文件拷到刚才 6 个文件夹里。

（3）打开 7000/redis.conf 并找出下面几个配置项进行修改：

```
port 7000
daemonize yes
cluster-enabled yes
cluster-config-filenodes.conf
cluster-node-timeout 5000
appendonly yes
```

（4）其他 5 个文件夹里的 `redis.conf` 也如上修改。注意端口号 port 参数，分别对应其文件夹名。

（5）分别进入每一个（6 个）文件夹下，执行以下命令启动各节点：

```shell
redis-server redis.conf
```

（6）可以使用 `ps aux|grep redis` 命令查看是否启动成功。

（7）在 redis-4.0.0/src 路径下执行 redis 的创建集群命令创建集群：

```shell
redis-trib.rb create --replicas 1 192.168.42.128:7000 192.168.42.128:7001 192.168.42.128:7002 192.168.42.128:7003 192.168.42.128:7004 192.168.42.128:7005
```

执行上面的命令的时候会报错，因为是执行的 ruby 的脚本，需要 ruby 的环境。错误内容：`ruby: No such file or directory` 。所以需要安装 ruby 的环境：

```shell
yum installruby
```

然后再执行刚才的创建集群命令，还会报错，提示缺少 rubygems 组件，使用 yum 安装：

```shell
yum installrubygems
```

还会报错，提示不能加载 redis，是因为缺少 redis 和 ruby 的接口，使用 gem 安装：

```shell
gem install redis
```

之后再执行：

```shell
redis-trib.rb create --replicas 1 192.168.42.128:7000 192.168.42.128:7001 192.168.42.128:7002 192.168.42.128:7003 192.168.42.128:7004 192.168.42.128:7005
```

确认后输入 yes ，便搭建成功。默认前三个（7000 ~ 7002）为主节点，后三个（7003 ~ 7005）为从节点，分别对应前三个。

（8）使用 `redis-cli` 命令进入集群环境：

```shell
redis-cli -p 7000
```

再执行 `cluster nodes` 命令即可看到该 7000 端口所在集群的各节点的状态、节点 ID 等信息。

​    



# 4. java 连接 redis

- 所需 jar 包：`jedis-2.9.0.jar`


- 连接 redis 单个 server ：

```
Jedis jedis = new Jedis("192.168.42.128", 7000);
System.out.println(jedis.get("name"));
```

- 连接 redis 集群：


```java
Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
jedisClusterNodes.add(new HostAndPort("192.168.42.128", 7000));
jedisClusterNodes.add(new HostAndPort("192.168.42.128", 7001));
jedisClusterNodes.add(new HostAndPort("192.168.42.128", 7002));
jedisClusterNodes.add(new HostAndPort("192.168.42.128", 7003));
jedisClusterNodes.add(new HostAndPort("192.168.42.128", 7004));
jedisClusterNodes.add(new HostAndPort("192.168.42.128", 7005));
JedisCluster jc = new JedisCluster(jedisClusterNodes);
jc.set("abc", "def");
System.out.println(jc.get("abc"));
try {
	jc.close();
} catch (Exception e) {
	e.printStackTrace();
}
```

若集群中某一节点宕机，只要故障转移成功还是可以正常连接该 redis 集群。

​    



# 5. redis 优化

### 使用 hash 类型

- 使用 hash 会比普通 key-value 类型占的内存更少，官网也推荐使用 hash。


- 在 `redis.conf` 配置文件中有两个和 hash 类型相关的配置参数： 

  ```shell
  # 如果hash中字段的数量小于64，redis将对该hash的value采用特殊编码。
  hash-max-ziplist-entries 64
  # 如果hash中各字段的最大长度不超过512字节，redis将对该hash的value采用特殊编码方式。
  hash-max-ziplist-value 512
  ```

  在不超过此最大值前，hash 会以一维数组的形式存储数据，比 map 结构占的内存小很多，而超过最大值后会转为正常的 hash 表。


- 以 hash 类型存储某种数据时用多个 hash 存储更省内存。

  比如，key 为 users 的 hash 上存储 n 个用户，与 key 为 users1、users2 的两个 hash 上分别存 n/2 的用户，两种相比后面的方案所占的内存更小。


​    

### redis.conf 参数

- 设置最大内存：**maxmemory**

  - 可以使用 `info memory` 命令查看当前内存使用情况。

  - 当 redis 内存使用达到 maxmemory 时，需要选择设置好的 `maxmemory-policy` 淘汰策略进行对老数据的淘汰。下面是可以选择的**淘汰策略**：

    - noeviction：不进行置换，表示即使内存达到上限也不进行置换，所有能引起内存增加的命令都会返回 error。
    - allkeys-lru：优先删除掉最近最不经常使用的 key，用以保存新数据。
    - volatile-lru：只从设置失效（expireset）的 key 中选择最近最不经常使用的 key 进行删除，用以保存新数据。
    - allkeys-random：随机从 all-keys 中选择一些 key 进行删除，用以保存新数据。
    - volatile-random：只从设置失效（expireset）的 key 中，选择一些 key 进行删除，用以保存新数据。
    - volatile-ttl：只从设置失效（expireset）的 key 中，选出存活时间（TTL）最短的 key 进行删除，用以保存新数据。


  - redis 4.0 新加了 **memory** 命令，可以查看某个可以占用的内存大小：`memory usage key`

- 使用 **slowlog** 慢日志查出引发延迟的命令：

  ```shell
  # 如果你的命令时间默认超过10ms，那么就会被记录。
  slowlog-log-slower-than 10000
  # 以list类型存放128个命令，如果超过则删除老的命令。
  slowlog-max-len 128
  ```

  使用 `slowlog get` 命令可以查看引发延迟的命令。

- 设置 redis 允许处理的最大请求连接数 **maxclients**，减少延迟时间。

- 设置集群节点超时时间：**cluster-node-timeout**


- 两种持久化方式 AOF 和 RDB（可以两种一起用）：

  - **RDB**（默认）：在指定的时间间隔能对数据进行快照存储。注意的是，每次快照持久化都是将内存数据完整写入到磁盘一次，并不是增量的只同步脏数据。

    ```shell
    # 900秒内如果超过1个key被修改，则发起快照存储
    save 900 1
    # 300秒内容如超过10个key被修改，则发起快照存储
    save 300 10
    ```

  - **AOF**：记录每次对服务器写的操作，当服务器重启的时候会重新执行这些命令来恢复原始的数据。

    ```shell
    # 启用aof持久化方式
    appendonly yes
    # 每次收到写命令就立即强制写入磁盘，最慢的，但是保证完全的持久化
    appendfsync always
    # 每秒钟强制写入磁盘一次，在性能和持久化方面做了折中，推荐
    appendfsync everysec
    ```

  - **RDB-AOF**：在 redis 4.0 新增了混合持久化格式（ RDB 格式的内容用于记录已有的数据，而 AOF 格式的内存则用于记录最近发生了变化的数据）：`aof-use-rdb-preamble yes`

  - 相关内容：<http://www.redis.cn/topics/persistence.html>


​    

### 减少碎片

可以使 key 是等长的从而减少内存碎片。

​    

### 异步删除

在 redis 4.0 之前，用户在使用 `DEL` 命令删除体积较大的键，又或者在使用 `FLUSHDB` 和 `FLUSHALL` 删除包含大量键的数据时，都可能会造成服务器阻塞。

为了解决以上问题，redis4.0 新添加了 `UNLINK` 命令，这个命令是 DEL 命令的异步版本， 它可以将删除指定键的操作放在后台线程里面执行，从而尽可能地避免服务器阻塞：

```shell
unlink key
```

此外，redis 4.0 中的 `FLUSHDB` 和 `FLUSHALL` 这两个命令都新添加了 ASYNC 选项，带有这个选项的数据库删除操作将在后台线程进行：

```
flushdb async
flushall async
```

