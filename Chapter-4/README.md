# spring-clound 学习打卡4：简单入门-熔断器-（Hystrix） 【Greenwich.SR1版本】
# 一、微服务中的雪崩效应简述  
> 微服务架构中，一般根据业务功能，划分成一个又一个的服务。  
服务和服务之间，一般通过rpc或者http调用。本系列入门学习打卡教程中的第二和第三章，讲的是利用ribbon 和feign 进行访问服务的例子。  
为了提高整个系统的稳定性和可用性，一般单个服务，会部署成集群模式，从而不会因为一个服务挂掉了，导致整个系统Bug 或者不可用的情况。  
不过，有这么一种情况，如果某一个服务异常，那么访问这个服务的线程就会被阻塞掉。如果大量的请求请求到这个服务，那么资源会被迅速耗费掉。从而导致这个服务瘫痪的情况。  
而因为这个服务的瘫痪，消耗掉了系统的资源，那么整个系统没有资源了，进而影响到其他的服务，从而出现大面积服务瘫痪的情况。这就是小编理解的雪崩的情况。  

# 二、准备工作。
启动第一章的server 和一个client， 端口8763.   
工程传送门：  
https://github.com/unnunique/SpringCloundDakaLearning/tree/master/Chapter-4  


# 三、利用熔断器解决雪崩效应，提高整个系统的可用性。 
如下是微服务架构中，客户端【一般是手机端或者浏览器端】访问服务的架构大致如下。 
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190423104854613.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
较底层的服务如果出现故障，会导致连锁故障。
（Hystrix）熔断器是这样处理的。  
当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开。这里显示为对不可用的服务，直接返回一个错误的信息或者固定的值（带有欺骗性的值）。如下图，通过falkback 方式来返回。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190423105231251.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70) 

# 四，上代码
## 4.1 pom 文件
复用Chapter-2 的pom，注意添加熔断器依赖jar 包. 如下。  
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>

```
完整pom 文件如下：
```
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

    <artifactId>chapter-4</artifactId>

    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
    </properties>

    <dependencies>
        <!--服务注册 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- spring-boot 应用-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--ribbon 模式下，调用注册中心的服务 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>

        <!-- 方便测试用-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- 熔断器依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
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

## 4.2 properties 文件
```properties
server.port=8766

spring.application.name=service-hystrix-demo

eureka.instance.hostname=localhost
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/

remote.eureka.servername=srvice-clientdemo
```

## 4.3 代码
app
注意启动熔断器处理
@EnableHystrix
```java 
package com.sydney.dream.chapter4;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableHystrix
public class HytrixDemoApp {
    public static void main(String[] args) {
        SpringApplication.run(HytrixDemoApp.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

```

service 服务调用   
熔断器的处理，参见如下：  
@HystrixCommand(fallbackMethod = "hiError")  
```java 
package com.sydney.dream.chapter4;

        import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;
        import org.springframework.web.client.RestTemplate;

@Service
public class HelloService {
    @Autowired
    RestTemplate restTemplate;

    @Value("${remote.eureka.servername}")
    private String serverName;

    @HystrixCommand(fallbackMethod = "hiError")
    public String hiService(String name) {
        return restTemplate.getForObject("http://" + serverName + "/hi?name="+name,String.class);
    }

    public String hiError(String name) {
        return "hi, " + name + ", service error.";
    }

}

```

controller
```java 
package com.sydney.dream.chapter4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @Autowired
    HelloService helloService;

    @GetMapping(value = "/hi")
    public String hi(@RequestParam String name) {
        return helloService.hiService( name );
    }
}
```

# 五，启动服务测试
如果服务是被kill 掉的，则可以得到如下结果。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019042311362022.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Vhc2VzX3N0b25l,size_16,color_FFFFFF,t_70)
