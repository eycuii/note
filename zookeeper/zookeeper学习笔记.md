zab，cas，paxos

安装

```shell
# 解压到/usr/local
tar -zxvf zookeeper-3.4.12.tar.gz -C /usr/local
# 重命名
cd /usr/local
mv zookeeper-3.4.12/ zookeeper
# 设置环境变量
vim /etc/profile
# vim进入后，顶部加：export ZOOKEEPER_HOME=/usr/local/zookeeper
# 然后把这个加到PATH里：PATH=.:$JAVA_HOME/IN:$ZOOKEEPER_HOME/BIN:$PATH
# 刷新/生效环境变量
source /etc/profile
# 修改配置文件名
cd /usr/local/zookeeper/conf
mv zoo_sample.cfg zoo.cfg
# 修改配置
vim zoo.cfg
# 修改dataDir（存放数据的地方）：dataDir=/usr/local/zookeeper/data
# 如果弄集群，需要在最后加各节点信息：
# server.0=192.168.1.121:2888:3888
# server.1=192.168.1.122:2888:3888
# server.2=192.168.1.123:2888:3888
# 创建dataDir指定的文件夹
cd /usr/local/zookeeper
mkdir data
# 弄集群时：data文件夹里创建myid文件，并填写内容为0（各节点id）
vim myid
# 写：0
# 启动zookeeper（/usr/local/zookeeper/bin的zkServer.sh，已经配了环境变量所以可以直接使用命令）
zkServer.sh start
# 查看状态。可以查看是follower还是leader
zkServer.sh status

# 客户端使用
# 进入zookeeper客户端
zkCli.sh
# 查找。会返回数组
ls /
ls /zookepper
# 创建并赋值
create /hello world
# 获取
get /hello
# 设值
set /hello world~
# 递归删除节点
rmr /hello
# 删除指定的某个节点
delete /hello/hi
```

​    

zookeeper配置说明（zoo.cfg）

tickTime

服务器间或客户端与服务器之间维持心跳的时间间隔。单位：毫秒

dataDir

存储内存中数据库快照的位置，就是zookeeper保存数据的目录。

clientPort

客户端连接zookeeper服务器的端口。zookeeper会监听这个端口，接收客户端的访问请求

initLimit

用来配置接收客户端连接时最长能忍受多少个心跳时间间隔数。

当超过 initLimit*tickTime 时还没有收到客户端返回的信息，那么表明客户端连接失败。

syncLimit

leader与follower之间发送消息时请求和应答时间长度。

最长不能超过多少个tickTime长度。

server.A=B:C:D

A：这是第几号服务器

B：这个服务器ip地址

C：这个服务器与集群中的leader交换信息的端口

D：如果leader服务器挂了，需要一个端口来重新进行选举，选出一个新的leader

​    

java操作zookeeper

create时如果已存在，会报错。

create对get相比性能不高，所以最好先get后再尝试create

create不能递归创建

delete可以检查版本号。版本号传-1表示不检查，直接删

delete方法能指定回调方法

​    

watch事件：一次性触发的。当watch监视的数据发生变化时，通知设置了该watch的client，即watcher。

watcher监听数据的事件类型：

（节点相关的）

EventType.NodeCreated

EventType.NodeDataChanged

EventType.NodeChildrenChanged

EventType.NodeDeleted

状态类型：（客户端实例相关的）

KeeperState.Disconnected

KeeperState.SyncConnected

KeeperState.AuthFailed

KeeperState.Expired

​    

zkClient：不用每次去弄watcher；返回更多数据；可直接传Object对象

Curator：

可以递归创建节点；

性能好（用cache，而不是重复注册的方式）；

监听时，如果watcher重启了会重新获取到所有的事件；

提供了分布式锁、原子类、barrier等；