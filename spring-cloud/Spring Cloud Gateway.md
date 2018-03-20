# Spring Cloud Gateway

​    

## 版本

Spring Cloud：Finchley.M8

Spring Boot：2.0.0.RELEASE

JDK：1.8

​    

## 入门案例

两种配置方法：一种是在配置文件，另一种是通过RouteLocator。

1. pom.xml：

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   	<modelVersion>4.0.0</modelVersion>

   	<groupId>com.demo</groupId>
   	<artifactId>spring-cloud-gateway</artifactId>
   	<version>0.0.1-SNAPSHOT</version>
   	<packaging>jar</packaging>

   	<name>spring-cloud-gateway</name>
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
   			<artifactId>spring-boot-starter-actuator</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-starter-gateway</artifactId>
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

2. application.yml 配置文件：

   ```yaml
   server:
     port: 8040
   spring:
     application:
       name: spring-cloud-gateway
   spring:
     cloud:
       gateway:
         routes:
         - id: spring-cloud-producer
           uri: http://localhost:8010
           predicates:
           - Path=/producer/**
           filters:
           - RewritePath=/producer/(?<segment>.*), /$\{segment}
   ```

3. SpringCloudGatewayApplication.java 主程序：

   ```java
   @SpringBootApplication
   public class SpringCloudGatewayApplication {

       @Bean
       public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
           return builder.routes()
                   //basic proxy
                   .route(r -> r.path("/baidu")
                           .uri("http://baidu.com/")
                   ).build();
       }

       public static void main(String[] args) {
           SpringApplication.run(SpringCloudGatewayApplication.class, args);
       }
   }
   ```

   注：MyEclipse 从 2015 版本开始支持 lambda 表达式。

4. 启动后，访问 http://localhost:8040/producer/hello/aa 被转发到 http://localhost:8010/hello/aa，http://localhost:8040/baidu 会转发到 http://baidu.com 。

