package com.oct.controller;

import com.oct.service.WebClientTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RestController("/webflux")
public class WebClientTestController {


    @Resource
    WebClientTestService webClientTestService;

    @GetMapping("/recall")
    public Mono<String> recall(){
        System.out.println("recall");
        return webClientTestService.recall();
    }

}
