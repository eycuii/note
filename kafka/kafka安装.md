# kafka 安装

​    

## 版本

kafka 1.0.1

centos 7

jdk 1.8

​    

## 安装步骤

1. 去 kafka 官网（http://kafka.apache.org/downloads）下载 kafka_2.11-1.0.1.tgz。

2. 解压

   ```shell
   tar zxvf kafka_2.11-1.0.1.tgz
   mv kafka_2.11-1.0.1 kafka
   cd kafka
   ```

3. 赋权限

   ```shell
   chmod 777 bin/*.sh
   ```

4. 启动 Zookeeper

   ```shell
   bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
   ```

5. 修改 kafka 配置文件

   修改 config/server.properties 中的 advertised.listeners：

   ```shell
   # Hostname and port the broker will advertise to producers and consumers. If not set, 
   # it uses the value for "listeners" if configured.  Otherwise, it will use the value
   # returned from java.net.InetAddress.getCanonicalHostName().
   advertised.listeners=PLAINTEXT://本机IP:9092
   ```

   如果没有设置该项，客户端操作时连的会是 127.0.0.1:9092，而不是该服务器地址，导致报 time out exception。

6. 启动 kafka

   ```shell
   bin/kafka-server-start.sh config/server.properties
   ```

​    

## 简单操作

1. 创建 topic

   创建单分区单副本的topic：test

   ```shell
   bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
   ```

2. 查看 topic

   ```shell
   $bin/kafka-topics.sh --list --zookeeper localhost:2181
   test
   ```

3. 发送消息

   向 test 的 topic 发送消息

   ```shell
   $bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
   hello world~
   ```

4. 消费消息

   消费 test 的消息并在终端打印

   ```shell
   $bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
   hello world~
   ```

   ​