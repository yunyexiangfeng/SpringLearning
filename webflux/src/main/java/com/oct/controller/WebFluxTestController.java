package com.oct.controller;


import com.oct.service.WebFluxTestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.function.Consumer;

@RestController
@Controller
public class WebFluxTestController {

    @Resource
    WebFluxTestService webFluxTestService;


    @GetMapping("/webflux/hello")
    public Mono<String> sayHello(){
        webFluxTestService.checkBlackUser("1").subscribe(c -> {
                    System.out.println("ee:" + c);
                },
                err -> {
                    System.out.println("err: err" + err);
                },
                this::test);
        return webFluxTestService.sayHello();
    }
    public void test(){
        System.out.println("dd");
    }
}
