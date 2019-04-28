# spring-clound 学习打卡3：简单入门-服务消费，基于【Feign】 【Greenwich.SR1版本】
# 一、简介  
>工程传送门  
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-3  

利用feign 可以访问注册在服务中心的服务，自带负载均衡功能。  
Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。  
>简而言之：  
Feign 采用的是基于接口的注解  
Feign 整合了ribbon，具有负载均衡的能力  
整合了Hystrix，具有熔断的能力  
# 二、准备工作
继续用第一篇的工程， 启动server，端口为8761; 启动client 两次，端口分别为8762 、8773.
# 三、上代码。 
## 3.1 pom 文件
```xml
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

    <artifactId>chapter-3</artifactId>


    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
    </properties>

    <dependencies>
        <!-- 服务注册。 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--feign 客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
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
## 3.2 properties
```properties
server.port=8765

spring.application.name=service-consumer-client-demo2

eureka.instance.hostname=localhost
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/

remote.eureka.servername=srvice-clientdemo
```

## 3.3 application
注意添加@EnableFeignClients  

```java 
package com.sydney.dream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class FeignClientDemoApp {
    public static void main(String[] args) {
        SpringApplication.run(FeignClientDemoApp.class, args);
    }
}

```

## 3.4 编写消费服务
```java 
package com.sydney.dream.chapter3;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${remote.eureka.servername}")
@Component
public interface HelloWorldService {
    @PostMapping(value = "/hi")
    String getHello(@RequestParam("name") String name);
}

```

## 3.5 controller
```java 
package com.sydney.dream.chapter3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Autowired
    private HelloWorldService helloWorldService;

    @RequestMapping("/hi")
    public String getHelloWorld(String name) {
        return helloWorldService.getHello(name);
    }
}

```

# 四、启动服务
启动程序，多次访问http://localhost:8765/hi?name=forezp,浏览器交替显示：  
>hi didi,i am from port:8762  
hi didi,i am from port:8763  