package com.sydney.dream.chapter2;

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

    public String hiService(String name) {
        return restTemplate.getForObject("http://" + serverName + "/hi?name="+name,String.class);
    }
}
