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
