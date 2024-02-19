//package com.oct.config;
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class KafkaTongjiConfig {
//    private static final String metadataBrokerList = "";
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, metadataBrokerList);//分配kafka集群
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);//序列化密钥对象的类
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);//序列化值对象的类
//        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 8000);//在配置中指定缓冲区大小
//        configProps.put(ProducerConfig.RETRIES_CONFIG, 0);
//        configProps.put(ProducerConfig.ACKS_CONFIG, "1");//只要Partition Leader接收到消息而且写入本地磁盘就认为成功了,不管其他的Follower有没有同步过去这条消息
//        configProps.put("producer.type", "async");
//        configProps.put("queue.enqueue.timeout.ms", 0);
//        configProps.put("queue.buffering.max.messages", 1000);
//        configProps.put("batch.num.messages", 100);
//        configProps.put("metadata.broker.list", metadataBrokerList);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean(name = "tongjiKafkaTemplate")
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//}
