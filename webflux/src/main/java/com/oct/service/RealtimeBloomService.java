package com.oct.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RealtimeBloomService {

    public static final String BLOOM_PRE = "kHAo_";

    @Resource
    private ReactiveRedisTemplate<String, byte[]> userBloomByteReactiveRedisTemplate;


    public void getRollBloom(String uid){
        userBloomByteReactiveRedisTemplate.execute(connection ->
            Flux.fromIterable(Arrays.asList(uid, BLOOM_PRE + uid)).flatMap(
                    key -> connection.stringCommands()
                    .get(ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8)))
                    .doOnError(err -> {
                        System.out.println("err=" + err);
                    })
                    .defaultIfEmpty(ByteBuffer.wrap(new byte[]{})))
                    .flatMap(byteBuffer -> {
                        return Mono.just(byteBuffer.array());
                    })
        ).reduce(new byte[]{}, reduceRollBloomFunction()).map(r -> {
            System.out.println("r=" + r);
            return Flux.just(r);
        }).doOnError(err -> {
            System.out.println("error=" + err);
        }).subscribe(ret -> {
            System.out.println("ret=" + ret);
        });
    }
    public BiFunction<byte[], byte[], byte[]> reduceRollBloomFunction(){
        return (pre, curr) -> {
            System.out.println("pre=" + pre + ", len=" + pre.length);
            System.out.println("curr=" + curr + ", len=" + curr.length);
            return pre;
        };
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        Map<Object, String> collect = list
                .stream()
                .map(Function.identity())
                .map(str -> str)
                .collect(Collectors.toMap(Function.identity(), task -> task));
        collect.forEach((o, s) -> System.out.println(s + ":" + o));
    }
}
