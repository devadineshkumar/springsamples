package com.test.springbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class MyJobListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(MyJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job starting: name={}, executionId={}", jobExecution.getJobInstance().getJobName(), jobExecution.getId());
        // global setup if needed
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job finished: name={}, executionId={}, status={}",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId(), jobExecution.getStatus());
        // global cleanup/notifications if needed
    }
}

