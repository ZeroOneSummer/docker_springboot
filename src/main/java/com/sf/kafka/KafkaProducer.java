package com.sf.kafka;

import com.sf.bean.UserBean;
import com.sf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    UserService userService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // 同步发送
    @Transactional
    @GetMapping("send/{message}")
    public void sendMessage1(@PathVariable("message") String message) throws Exception {
        kafkaTemplate.send(KFK_TOPIC_ZERO1, message).get(2, TimeUnit.SECONDS);
        log.info("kafka sendMessage success topic = {}, data = {}", KFK_TOPIC_ZERO1, message);
    }

    //异步发送-回调
    @GetMapping("send2/{message}")
    public void sendMessage2(@PathVariable("message") String message) {
        kafkaTemplate.send(KFK_TOPIC_ZERO1, message).addCallback(success -> {
            // 消息发送到的topic
            String topic = success.getRecordMetadata().topic();
            // 消息发送到的分区
            int partition = success.getRecordMetadata().partition();
            // 消息在分区内的offset
            long offset = success.getRecordMetadata().offset();
            log.info("发送消息成功:" + topic + "-" + partition + "-" + offset);
        }, failure -> {
            log.info("发送消息失败:" + failure.getMessage());
        });
    }

    //回调发送2
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

    //事务
    @GetMapping("send4/{message}")
    public void sendMessage4(@PathVariable("message") String message){
        // 不声明事务：后面报错但前面消息已经发送成功了
//        kafkaTemplate.send(KFK_TOPIC_ZERO1, message);
//        throw new RuntimeException("fail");
        // 声明事务：后面报错消息不会发出去
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(KFK_TOPIC_ZERO1, message);
            throw new RuntimeException("fail");
        });
        log.info("事务消息，发送成功");
    }

    //同步事务(spring + kafka)
    @Transactional(transactionManager ="chainedKafkaTransactionManager", rollbackFor = Exception.class)
    @GetMapping("send5/{message}")
    public void sendMessage5(@PathVariable("message") String message){
        //db
        userService.addUser(new UserBean().setName("丽莎4").setSex("女"));
        //kafka
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(KFK_TOPIC_ZERO1, message);
            throw new RuntimeException("fail");
        });
        log.info("事务消息，发送成功");
    }

    // 批量发送
    @Transactional
    @GetMapping("send6/list")
    public void sendMessage6() throws Exception {
        List<UserBean> list = Arrays.asList(new UserBean().setName("路西"), new UserBean().setName("娜美"));
        kafkaTemplate.send(KFK_TOPIC_ZERO1, list).get(2, TimeUnit.SECONDS);
        log.info("kafka sendMessage success topic = {}, data size = {}", KFK_TOPIC_ZERO1, list.size());
    }
}
