package com.oct.controller;

import com.oct.service.RealtimeBloomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@RestController
public class RealtimeBloomController {

    @Resource
    private RealtimeBloomService realtimeBloomService;

//    @GetMapping("/webflux/{uid}")
    public Mono<String> getRollBloom(@PathVariable("uid") String uid){
        System.out.println("uid=" + uid);
        realtimeBloomService.getRollBloom(uid);
        return Mono.just(uid);
    }
}
