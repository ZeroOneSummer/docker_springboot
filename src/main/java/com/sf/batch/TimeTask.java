package com.sf.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 **/
@Slf4j
@Component
public class TimeTask {

    private final JobLauncher jobLauncher;  //job执行器

    private final BatchJobDemo batchJobDemo; //批量任务

    @Autowired
    public TimeTask(JobLauncher jobLauncher, BatchJobDemo batchJobDemo) {
        this.jobLauncher = jobLauncher;
        this.batchJobDemo = batchJobDemo;
    }

	// 定时任务，每30秒执行一次
    @Scheduled(cron = "0/15 * * * * ?")
    public void runBatch() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        log.info("开始执行定时任务...");
        // 至少添加一个唯一参数，job才会执行。这里使用时间戳，batch_job_execution_params查询
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        // 获取job并运行
        Job job = batchJobDemo.multiJob();
        JobExecution execution = jobLauncher.run(job, jobParameters);
        log.info("定时任务执行结束！{}", execution.getStatus());
    }
}