package com.sf.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sf.kafka.common.KafkaConstant.KFK_TOPIC_ZERO;
import static com.sf.kafka.common.KafkaConstant.KFK_TOPIC_ZERO1;

/**
 * 生产者
 */
@Slf4j
@RequestMapping("/kafka/")
@RestController
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // 发送消息
    @GetMapping("send/{message}")
    public void sendMessage1(@PathVariable("message") String message) {
        kafkaTemplate.send(KFK_TOPIC_ZERO1, message);
        log.info("kafka sendMessage success topic = {}, data = {}", KFK_TOPIC_ZERO1, message);
    }

    //回调发送
    @GetMapping("send2/{message}")
    public void sendMessage2(@PathVariable("message") String message) {
        kafkaTemplate.send(KFK_TOPIC_ZERO1, message).addCallback(success -> {
            // 消息发送到的topic
            String topic = success.getRecordMetadata().topic();
            // 消息发送到的分区
            int partition = success.getRecordMetadata().partition();
            // 消息在分区内的offset
            long offset = success.getRecordMetadata().offset();
            System.out.println("发送消息成功:" + topic + "-" + partition + "-" + offset);
        }, failure -> {
            System.out.println("发送消息失败:" + failure.getMessage());
        });
    }

    @GetMapping("send3/{message}")
        public void sendMessage3(@PathVariable("message") String message) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KFK_TOPIC_ZERO, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("kafka sendMessage error, ex = {}, topic = {}, data = {}", ex, KFK_TOPIC_ZERO, message);
            }
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("kafka sendMessage success topic = {}, data = {}", result.getRecordMetadata().topic(), message);
            }
        });
    }

    @GetMapping("send4/{message}")
    public void sendMessage4(@PathVariable("message") String message){
        // 声明事务：后面报错消息不会发出去
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(KFK_TOPIC_ZERO1, message);
            throw new RuntimeException("fail");
        });
        // 不声明事务：后面报错但前面消息已经发送成功了
//        kafkaTemplate.send(KFK_TOPIC_ZERO1, message);
//        throw new RuntimeException("fail");
    }
}
