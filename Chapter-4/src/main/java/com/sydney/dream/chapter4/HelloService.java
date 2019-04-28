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
