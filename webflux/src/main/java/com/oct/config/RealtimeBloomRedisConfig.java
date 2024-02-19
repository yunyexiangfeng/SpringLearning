package com.oct.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RealtimeBloomRedisConfig {

    /**
     * 生产环境redis
     */
    private String host = "";
    private int port = 6379;
    private String password = "";

    @Bean("UserBloomRedisPoolConfig")
    public GenericObjectPoolConfig UserBloomRedisPoolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1000);
        config.setMinIdle(10);//连接池中的最小空闲连接
        config.setMaxIdle(20);//连接池中的最大空闲连接
        config.setMaxWaitMillis(200);//连接池最大阻塞等待时间,如果调用JedisPool.getResource()将阻塞,则等待多长时间,以毫秒为单位,默认为-1,表示无限期阻塞
        return config;
    }


    @Bean("UserBloomByteReactiveRedisTemplate")
    @DependsOn("UserBloomLettuceConnectionFactory")
    public ReactiveRedisTemplate<String, byte[]> UserBloomByteReactiveRedisTemplate(@Qualifier("UserBloomLettuceConnectionFactory") ReactiveRedisConnectionFactory redisConnectionFactory,
                                                                                    ResourceLoader resourceLoader) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisSerializationContext<String, byte[]> serializationContext = RedisSerializationContext.<String, byte[]>newSerializationContext()
                .key(stringRedisSerializer)
                .value(RedisSerializer.byteArray())
                .hashKey(stringRedisSerializer)
                .hashValue(RedisSerializer.byteArray()).build();
        return new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
    }



    @Bean("UserBloomLettuceConnectionFactory")
    public LettuceConnectionFactory UserBloomLettuceConnectionFactory(@Qualifier("UserBloomRedisPoolConfig") GenericObjectPoolConfig poolConfig) {
        return new LettuceConnectionFactory(RedisClusterConfigOnline(),
                LettucePoolingClientConfiguration.builder()
                        .commandTimeout(Duration.ofMillis(500))//redis命令执行超时时间
                        .poolConfig(poolConfig).build());
    }


    /**
     * redis集群连接方式
     */
    private RedisClusterConfiguration RedisClusterConfigOnline() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setPassword(password);
        //支持添加多个节点
        redisClusterConfiguration.setClusterNodes(Collections.singletonList(new RedisNode(host, port)));
        return redisClusterConfiguration;
    }

}
