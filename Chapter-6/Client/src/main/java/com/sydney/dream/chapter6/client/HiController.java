package com.sydney.dream.chapter6.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class HiController {
    @Value("${demokey}")
    String demokey;
    @RequestMapping(value = "/hi")
    public String hi(){
        return demokey;
    }
}
