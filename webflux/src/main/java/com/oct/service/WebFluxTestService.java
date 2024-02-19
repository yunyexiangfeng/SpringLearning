package com.oct.service;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Service
public class WebFluxTestService {

//    @Resource
//    private KafkaTemplate<String, String> tongjiKafkaTemplate;

    public Mono<String> sayHello(){
        return Mono.just("sayHello method.");
    }

    public Mono<String> checkBlackUser(String uid){
        return Mono.just("checkBlackUser method.").map(c -> {
            if ("1".equals(uid)){
                throw new NullPointerException("throw exception");
            }
            return "cc";
        }).map(item -> {
            System.out.println("执行map");
            return item;
        }).doOnError(err -> {
            System.out.println("err: " + err);
//            tongjiKafkaTemplate.send("engine_error_logs", "kafka connection test");
        }).onErrorReturn("onErrorReturn").flatMap(fm -> {
            System.out.println(fm + ":final flatMap");
            return Mono.just("final flatMap");
        });
    }

//    @KafkaListener(topics = "engine_error_logs")
//    public void consumer(String record){
//        System.out.println("record=" + record);
//    }
}
