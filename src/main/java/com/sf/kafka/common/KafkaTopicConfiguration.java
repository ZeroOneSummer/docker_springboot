package com.sf.kafka.common;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;

@Slf4j
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaTopicConfiguration {

    private final KafkaTopicProperties properties;

    @Bean
    public String[] kafkaTopicName() {
        return properties.getTopicName();
    }

    @Bean
    public String topicGroupId() {
        return properties.getGroupId();
    }

    //新建一个异常处理器，用@Bean注入
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, exception, consumer) -> {
            log.info("自定义异常处理器处理异常信息：" + message.getPayload());
            return null;
        };
    }
}