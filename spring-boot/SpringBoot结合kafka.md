# Spring Boot 结合 kafka

​    

## 版本

Spring Boot：2.0.0.RELEASE

JDK：1.8

服务器上的 kafka：1.0.1

​    

## 发送消息

1. pom.xml：

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   	<modelVersion>4.0.0</modelVersion>

   	<groupId>com.demo</groupId>
   	<artifactId>spring-boot-kafka</artifactId>
   	<version>0.0.1-SNAPSHOT</version>
   	<packaging>jar</packaging>

   	<name>spring-boot-kafka</name>
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
   	</properties>

   	<dependencies>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>org.springframework.kafka</groupId>
   			<artifactId>spring-kafka</artifactId>
   		</dependency>

   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-test</artifactId>
   			<scope>test</scope>
   		</dependency>
   	</dependencies>

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

2. application.properties：

   ```shell
   spring.application.name=spring-boot-kafka
   server.port=8070

   spring.kafka.bootstrap-servers=140.143.224.183:9092
   spring.kafka.consumer.group-id=springboot-group1
   ```

3. HelloController.java：

   ```java
   @RestController
   public class HelloController {

   	@Autowired
   	private KafkaTemplate<String, String> kafkaTemplate; // 会自动注入

   	@GetMapping("/hello/{name}")
       public String hello(@PathVariable("name") String name) {
   		kafkaTemplate.send("test", "springboot-key",  "springboot-hello "+ name +"~");
           return "hello " + name;
       }
   }
   ```

4. 启动后，访问 http://localhost:8070/hello/aaa，kafka 上就可以看到 “springboot-hello aaa~” 的消息了。

​    

## 消费消息

只需要在上面例子的基础上加个 listener 就行。

1. MsgListener.java：

   ```java
   @Component
   public class MsgListener {

       @KafkaListener(topics = "test")
       public void processMessage(String content) {
           System.out.println("收到消息：" + content);
       }
   }
   ```

2. 启动后发送一条消息，就可以在控制台看到输出 “收到消息：XXX”。