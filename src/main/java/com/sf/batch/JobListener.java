package com.sf.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * job监听器：任务环绕处理
 **/
@Slf4j
@Component
public class JobListener implements JobExecutionListener {

    private long startTime;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public JobListener(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    /**
     * job开始前执行
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
        log.info("job before -> 任务参数: {}", jobExecution.getJobParameters());
    }

    /**
     * job结束后执行
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("job after -> 任务状态: {}", jobExecution.getStatus());
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("-> 任务完成");
            threadPoolTaskExecutor.destroy();
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("-> 任务失败");
        }
        log.info("job after -> 任务{} 总耗时: {} ms", jobExecution.getJobId(), (System.currentTimeMillis() - startTime));
    }
}