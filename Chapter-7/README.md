# spring-clound 学习打卡7：简单入门-高可用的统一的配置中心，Spring-clound-config 【Greenwich.SR1版本】  
# 一、简介
因为项目配置往往是应用的组成部分，如果配置中心挂掉了，那么往往可能会导致整个应用给挂掉，所以这个时候，就要避免  
所谓的单点问题，也就是一个服务其中一个节点挂了，还有其他的节点提供服务，从而提供高可用的目的。  
# 1.1 代码传送门
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-7    
# 二，高可用的配置中心。
## 2.1 首先启动一个eureka 注册中心，参见第一章：  
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-1  
## 2.2 改造配置中心。  
### 2.2.1 简介
注册中心的配置，参见第6章。  
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-6  
思路是把配置中心配置到注册中心去。  
然后启动多个服务，组成一个集群。  
### 2.2.2 pom 配置如下：
主要是添加了如下配置：
```xml 
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>Config-Server</artifactId>

    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
    </properties>

    <dependencies>

       <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

       <!-- 注册中心 -->
      <!--  <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>-->

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
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
### 2.2.3 properties 文件配置
```
spring.application.name=config-server
server.port=8889

spring.cloud.config.server.git.uri=https://github.com/unnunique/SpringCloundDakaLearning
spring.cloud.config.server.git.searchPaths=Chapter-6/config-center
spring.cloud.config.label=master
spring.cloud.config.server.git.username=
spring.cloud.config.server.git.password=


eureka.instance.hostname=localhost
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

## 2.3 改造调用配置中心的应用， 
如本章的：Chapter7-Client  
### 2.3.1  properties
注意原先的是：#spring.cloud.config.uri= http://localhost:8888/  
现在直接改成：spring.cloud.config.discovery.service-id=config-server
```
spring.application.name=app1
server.port=8881

spring.cloud.config.label=master
spring.cloud.config.profile=dev
#spring.cloud.config.uri= http://localhost:8888/
spring.cloud.config.discovery.service-id=config-server
spring.cloud.config.request-read-timeout=6000000
management.endpoints.web.exposure.include=refresh

eureka.instance.hostname=localhost
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

### 2.3.2 pom 文件配置参见第二章。  
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-2  

### 2.4 测试。 
启动注册中心， 启动多个应用，模拟部署多个节点，使得服务组成一个集群【参见第二章：https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-2】。  
启动后如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190506161642378.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
然后启动client服务：  
访问如下：  
http://localhost:8881/hi  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190506161829500.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
然后kill 掉其中一个节点的服务。 如下：  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190506163554592.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
再次访问，还可以进行访问，如下：   
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190506163631243.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
# 至此spring-clound 学习打卡7 done。