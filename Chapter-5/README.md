
# spring-clound 学习打卡5：简单入门-路由器-（Zuul） 【Greenwich.SR1版本】  
# 一、工程传送门
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-5  
# 二、简介  
在微服务架构中，需要几个基础的服务治理组件，包括服务注册与发现、服务消费、负载均衡、断路器、智能路由、配置管理等，由这几个基础组件相互协作，共同组建了一个简单的微服务系统。一个简答的微服务系统如下图：  
和第一篇介绍的差不多，如下：
>spring-clound中的微服务架构原则【待理解】
注册中心(服务发现&注册、kv配置中心)【这个有get到】
rpc(网络模块、协议)【完全不知道是什么】
限流熔断(触发策略、实现算法)【一点掉理解】
api网关（api网关作用、原理）【no理解，一脸懵逼】
统一日志【字面意思上的理解】、链路监控【二脸懵逼】
微服务基于容器的部署【有用过docker部署，但是docker 不理解】。  

# 三、一个典型的微服务架构，大致如下
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019042816411587.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
图片来源网络，将就着看。   
网上找到了一个介绍zuul 作用的介绍，如下：  
>这些过滤器完成以下功能：  
1 身份认证和安全：识别每个资源的验证要求，并拒绝那些与要求不符的请求。  
2 审查与监控：在边缘位置追踪有意义的数据和统计结果，从而带来精确的生产视图。  
3 动态路由：动态地将请求路由到不同的后端集群。  
4 压力测试：逐渐增加指向集群的流量，以了解性能。  
5 负责分配：为每一种负载类型分配对应容量，并弃用超出限定值的请求。  
6 静态响应处理：在边缘位置直接建立部分响应，避免其转发到内部集群。  
7 多区域弹性：跨越AWS Region进行请求路由，旨在实现ELB（Elastic Load Blancing）使用的多样化，以及让系统的边缘更贴近系统的使用者。  

# 四、关于利用zuul 作为api 路由设置，准备工作
代码传送门：  
首先，参照第一章， 编写一个server 段应用。  
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-5  
服务端口：8761  
其次创建两个服务，注册到注册中心，即server 中。  
服务名字1：chapter5-srvice-client1，端口：8762  
服务名字1：chapter5-srvice-client2，端口：8763  
编写好后，启动server 和 两个服务， 打开网页端，http://localhost:8761/  
如下：  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190428165428170.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)

# 五、关于zuul 作为路由的代码，
## 5.1 首先是pom 文件
注意添加zuul 相关依赖：
```xml 
<dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>Chapter-5</artifactId>
        <groupId>com.sydney.dream.springclound</groupId>
        <version>1.0.0</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>zuuldemo</artifactId>

    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
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
## 5.2 然后是应用配置： 如下：
主要是看zuul 开头的相关配置。
```
server.port=8764

spring.application.name=chapter5-zuuldemo

eureka.instance.hostname=localhost
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/

zuul.routes.api-a.path=/api-a/**
zuul.routes.api-a.serviceId=chapter5-srvice-client1

zuul.routes.api-b.path=/api-b/**
zuul.routes.api-b.serviceId=chapter5-srvice-client2
```

## 5.3 如下：最后是app
注意添加@EnableZuulProxy 注解，弃用zuul 代理功能。  
```java 
package com.sydney.dream.chapter5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
public class ServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run( ServiceZuulApplication.class, args );
    }
}
```

## 5.4 至此，done 。
启动服务，服务端口在：8764 
通过访问如下api，可以展现zuul 作为api 路由的功能。   
http://localhost:8764/api-a/hi?name=didi  
结果：  hi didi,i am from port:8762  
http://localhost:8764/api-b/hi?name=didi  
结果：  hi didi,i am from port:8763  
# 以上，则是zuul 的简单入门。  