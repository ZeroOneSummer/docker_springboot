package com.sf.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 简单任务示例
 */
@Component
public class SimpleJobDemo {

     // 注入创建任务的对象
     @Autowired
     private JobBuilderFactory jobBuilderFactory;

     // 注入创建步骤的对象
     @Autowired
     private StepBuilderFactory stepBuilderFactory;

     @Bean
     public Job jobDemo() {
         return jobBuilderFactory.get("jobDemo")
                 .start(step1())
                 .build();
     }

     @Bean
     public Step step1() {
         return stepBuilderFactory.get("step1")
                 .tasklet(new Tasklet() {
                     @Override
                     public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                         System.out.println(Thread.currentThread().getName() + "------" + "hello world");
                         // 返回执行完成状态
                         return RepeatStatus.FINISHED;
                     }
                 }).build();
     }
}