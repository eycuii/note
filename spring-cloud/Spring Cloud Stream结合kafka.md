# Spring Cloud Stream 结合 kafka

​    

## 版本

Spring Cloud：Finchley.M8

Spring Boot：2.0.0.RELEASE

JDK：1.8

kafka：1.0.1

​    

## 介绍

是一个用来为微服务应用构建消息驱动能力的框架，为一些供应商的消息中间件产品提供了个性化的自动化配置实现。目前仅支持RabbitMQ、Kafka。

​    

## 发送消息

1. pom.xml：

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>

      <groupId>com.demo</groupId>
      <artifactId>spring-cloud-kafka</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>jar</packaging>

      <name>spring-cloud-kafka</name>
      <description>Demo project for Spring Boot</description>

      <parent>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-parent</artifactId>
         <version>2.0.0.RELEASE</version>
         <relativePath/> <!-- lookup parent from repository -->
      </parent>

      <properties>
         <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
         <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
         <java.version>1.8</java.version>
         <spring-cloud.version>Finchley.M8</spring-cloud.version>
      </properties>

      <dependencies>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
         </dependency>
         <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-kafka</artifactId>
         </dependency>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
         </dependency>

         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
         </dependency>
      </dependencies>

      <dependencyManagement>
         <dependencies>
            <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-dependencies</artifactId>
               <version>${spring-cloud.version}</version>
               <type>pom</type>
               <scope>import</scope>
            </dependency>
         </dependencies>
      </dependencyManagement>

      <build>
         <plugins>
            <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
         </plugins>
      </build>
   </project>
   ```

2. application.yml：

   ```yaml
   server:
     port: 8050
   spring:
     application:
       name: spring-cloud-kafka
     cloud:
        instance-count: 1
        instance-index: 0
        stream:
           kafka:
             binder:
               brokers: 140.143.224.183:9092
               zk-nodes: 140.143.224.183:2182
           bindings:
             output:
               destination: test
               content-type: text/plain
   ```

3. SendService.java：

   ```java
   @EnableBinding(Source.class)
   public class SendService {

       @Autowired
       private Source source;

       public boolean sendMessage(String msg) {
           return source.output().send(
                   MessageBuilder.withPayload(msg)
                           .setHeader("partitionKey", "springcloud-key").build());
       }
   }
   ```

4. MsgController.java：

   ```java
   @RestController
   public class MsgController {

       @Autowired
       private SendService service;

       @RequestMapping(value = "/msg/{content}", method = RequestMethod.GET)
       public String hello(@PathVariable("content") String content) {
           return "hello " + service.sendMessage(content);
       }
   }
   ```

5. 启动后访问 http://localhost:8050/msg/xxx，就可以发送消息。

​    

## 消费消息

1. application.yml：

   ```yaml
   server:
     port: 8060
   spring:
     application:
       name: spring-cloud-kafka-consumer
     cloud:
        instance-count: 1
        instance-index: 0
        stream:
           kafka:
             binder:
               brokers: 140.143.224.183:9092
               zk-nodes: 140.143.224.183:2182
           bindings:
             input:
               destination: springcloud
               group: s1
               consumer:
                 autoCommitOffset: false
                 concurrency: 1
                 partitioned: false
   ```

2. MsgSink.java：

   ```java
   @EnableBinding(Sink.class)
   public class MsgSink {

       @StreamListener(Sink.INPUT)
       public void process(Message<String> message) {
           System.out.println(message.getPayload());
           Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
           if (acknowledgment != null) {
               System.out.println("Acknowledgment provided");
               acknowledgment.acknowledge();
           }
       }
   }
   ```

3. 启动后发送消息，就可以在控制台看到消费的消息。

