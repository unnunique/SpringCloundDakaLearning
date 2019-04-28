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
