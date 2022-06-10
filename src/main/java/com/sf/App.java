package com.sf;

import com.sf.kafka.common.KafkaTransactionConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})  //暂时屏蔽kafka
@MapperScan("com.sf.mapper")
@EnableBatchProcessing //开启批处理
@EnableScheduling //开启定时任务
public class App implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args){
        SpringApplication.run(App.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("App is started ok.");
    }
}