package com.test.springbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @GetMapping("/startJob")
    public String startJob() {
        try {
            JobParameters parameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters();
            jobLauncher.run(job, parameters);
            return "Batch job started";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start job";
        }
    }

}
