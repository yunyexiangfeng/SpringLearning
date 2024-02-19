package com.oct.service;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WebClientTestService {


    public Mono<String> recall(){
        String urla = "";
        int timeout = 150;
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10)
                                .option(ChannelOption.TCP_NODELAY, true)
                                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
                                )
                );
        long start = System.currentTimeMillis();
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build()
                .get()
                .uri(urla)
                .headers(h -> {
                    h.add("Content-Type", "application/json;charset=utf-8");
                })
//                .bodyValue(JSONObject.toJSONString(args))
//                .bodyValue("")
                .retrieve()
                .onStatus(e -> e.is4xxClientError() || e.is5xxServerError(), resp -> {
                    log.error("apiSend err url: {}, code: {}, msg: {}", urla, resp.statusCode().value(),resp.statusCode().getReasonPhrase());
                    return Mono.error(new RuntimeException(resp.statusCode().value() + " : " + resp.statusCode().getReasonPhrase()));
                })
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnError(e -> {
                    long cost = System.currentTimeMillis() - start;
                    log.error("http.error: {} error={}", cost, e.getMessage());
                })
                .map(ret -> {
                    long cost = System.currentTimeMillis() - start;
                    log.info("map cost = {}, ret = {}", cost, ret);
                    return ret;
                })
                .onErrorReturn("")
                .switchIfEmpty(Mono.just(""))
                .flatMap(json -> {
                    long cost = System.currentTimeMillis() - start;
                    log.info("last flatMap cost={}, json={}", cost, json);
                    return Mono.just("recall");
                });
    }





}
