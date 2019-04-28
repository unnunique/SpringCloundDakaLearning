

# spring-clound 学习打卡1：简单入门-服务注册 【Greenwich.SR1版本】
# 一、spring-clound 简介
简介
> 解决庞杂的系统带来的臃肿和耦合过高等的问题。如果一个系统耦合性比较高，
那么容灾性就可能比较差。spring-clound可以把一个繁杂的系统，切分成一个一个的
小系统，相当于一个由一个小的服务，也就是业界比较流行的微服务架构。  

学习spring-clound应该了解如下内容：  
>spring-clound中的微服务架构原则【待理解】
注册中心(服务发现&注册、kv配置中心)【这个有get到】
rpc(网络模块、协议)【完全不知道是什么】
限流熔断(触发策略、实现算法)【一点掉理解】
api网关（api网关作用、原理）【no理解，一脸懵逼】
统一日志【字面意思上的理解】、链路监控【二脸懵逼】
微服务基于容器的部署【有用过docker部署，但是docker 不理解】。  

# 二、这是一个入门教程
这里的eureka是一个服务注册和发现模块。
> 主要讲解基于eureka的服务注册于发现，虽然本司用的是基于consul的服务注册于发现
【之所以不用eureka，想来是因为后面spring团队不再在eureka上迭代开发了吧】。
 

## 2.1 首先创建一个maven 工程。
### 2.1.1 parent pom 
maven 工程的创建，这里就不展开了。  
本工程的目录结构大致如下：  
工程传送门：
https://github.com/unnunique/SpringCloundDakaLearning

工程大致结构
> Spring-Clound[springcloundlearning]  
--Chapter1[chapter-1]  
----Client[client]  
----Server[server]  
--Chpter2[chapter-2]  
![工程目录结构](https://img-blog.csdnimg.cn/20190419143413173.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)

首先， maven parent 工程pom 文件如下：
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>com.sydney.dream.springclound</groupId>
    <artifactId>parent</artifactId>
    <packaging>pom</packaging>
    <version>1.0.0</version>
    <modules>
        <module>chapter-1</module>
        <module>Chapter-2</module>
    </modules>


</project>
```

### 2.1.2 在下面建立一个子模块【chapter-1】
pom 文件如下
```xml 文件
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>parent</artifactId>
        <groupId>com.sydney.dream.springclound</groupId>
        <version>1.0.0</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>chapter1</artifactId>


</project>
```
### 2.1.3 在chapter-2 下面建立两个子模块分别叫做Server和client
Server 代表注册中心，pom文件如下：
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.4.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.sydney.dream.chapter1</groupId>
	<artifactId>server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>server</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
		<spring-cloud.version>Greenwich.SR1</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
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

client pom 文件如下：
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.4.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.sydney.dream.chapter1</groupId>
	<artifactId>client</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>client</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
		<spring-cloud.version>Greenwich.SR1</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
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
## 2.2 应用配置

### 2.2.1 对于基于Eureka 的注册中心的配置如下, 即对应的server 应用。
application.properties
```properties
server.port=8761

eureka.instance.hostname=localhost
eureka.client.register-with-eureka=false   ##eureka 把eureka.client.register-with-eureka 以及eureka.client.fetch-registry 置为false 来标识这个服务是注册中心
eureka.client.fetch-registry=false
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

App
```java
package com.sydney.dream.chapter1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
```

### 2.2.2  对于基于Eureka 服务注册应用如下，对应着client.
application.properties
```properties
server.port=8762

spring.application.name=srvice-clientdemo

eureka.instance.hostname=localhost
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/

```

App
```java
package com.sydney.dream.chapter1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Value("${server.port}")
	String port;
	@RequestMapping("/hi")
	public String home(@RequestParam String name) {
		return "hi "+name+",i am from port:" +port;
	}
}

```

## 2.3 注册中心的启动，以及应用服务的注册。
### 2.3.1 首先启动注册中心应用。
注册中心地址： http://localhost:8761/
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190419175418185.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
可以看到， 此时注册中心已经启动，但是注册中心还没有注册有服务。

### 2.3.2 启动两个一样的client 服务， 只是端口号不一样，模拟服务集群。
#### 2.3.2.1 在idea 启动两个服务的办法：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190419175708951.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
把如下的勾选去掉：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190419175756826.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
#### 2.3.2 启动两个client，端口号分别为8762 和8763
启动后，在注册中心的显示如下。可以看到两个服务已经注册上来了。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190419180421166.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)

### 2.4 client 中的hello world 请求。
http://localhost:8762/hi?name=didi

响应如下：
hi didi,i am from port:8762 

### 2.5 至此Demo 第一个打卡行动 Done