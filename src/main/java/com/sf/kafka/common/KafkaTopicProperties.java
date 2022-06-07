package com.sf.kafka.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.io.Serializable;

@Setter
@Getter
@ConfigurationProperties(prefix = "kafka.topic")
public class KafkaTopicProperties implements Serializable {
    private String groupId;
    private String[] topicName;
}