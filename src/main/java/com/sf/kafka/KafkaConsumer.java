package com.sf.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消费者
 */
@Slf4j
@Component
public class KafkaConsumer {

    // 消费监听
    @KafkaListener(topics = "#{kafkaTopicName}", groupId = "#{topicGroupId}")
    public void onMessage1(ConsumerRecord<String, Object> record) {
        log.info("kafka consumer received：[topic:{} - partition:{} - value:{}]", record.topic(), record.partition(), record.value());
    }

    /*//指定topic、partition、offset消费
    //同时监听topic1和topic2，监听topic1的0号分区、topic2的 "0号和1号" 分区，指向1号分区的offset初始值为8
    @KafkaListener(id = "consumer1", groupId = "felix-group", topicPartitions = {
            @TopicPartition(topic = "topic1", partitions = {"0"}),
            @TopicPartition(
                    topic = "topic2",
                    partitions = "0",
                    partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "8")
            )
    })
    public void onMessage2(ConsumerRecord<?, ?> record) {
        System.out.println("topic:" + record.topic() + "|partition:" + record.partition() + "|offset:" + record.offset() + "|value:" + record.value());
    }

    //List来接收
    @KafkaListener(id = "consumer2", groupId = "felix-group", topics = "topic1")
    public void onMessage3(List<ConsumerRecord<?, ?>> records) {
        log.info(">>>批量消费一次，records.size()=" + records.size());
        for (ConsumerRecord<?, ?> record : records) {
            log.info(record.value().toString());
        }
    }

    //指定异常处理
    @KafkaListener(topics = "topic1", errorHandler = "consumerAwareErrorHandler")
    public void onMessage4(List<ConsumerRecord<?, ?>> records) {
        log.info(">>>批量消费一次，records.size()=" + records.size());
        for (ConsumerRecord<?, ?> record : records) {
            log.info(record.value().toString());
        }
    }*/
}
